package com.charmflex.flexiexpensesmanager.features.backup.checker

import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupData
import com.charmflex.flexiexpensesmanager.features.backup.ui.ImportedData
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ImportDataChecker @Inject constructor(
    private val tagRepository: TagRepository,
    private val categoryRepository: TransactionCategoryRepository,
    private val accountRepository: AccountRepository,
    @Dispatcher(Dispatcher.Type.IO)
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun updateRequiredData(
        missingData: Set<ImportedData.MissingData>,
        importedData: List<ImportedData>
    ): Pair<List<ImportedData>, Set<ImportedData.MissingData>> {
        val updatedMissingData = missingData.toMutableSet()
        val updatedImportedData = importedData.toMutableList()
        val accounts =
            accountRepository.getAllAccounts().firstOrNull()?.map { it.accounts }?.flatten()
        val tags = tagRepository.getAllTags().firstOrNull()
        val incomeCategories =
            categoryRepository.getCategories(TransactionType.INCOME.name)
                .firstOrNull()?.let {
                generateCategoriesMap(it)
            }
        val expensesCategories =
            categoryRepository.getCategories(TransactionType.EXPENSES.name)
                .firstOrNull()?.let {
                generateCategoriesMap(it)
            }
        missingData.forEachIndexed { index, data ->
            when (data.dataType) {
                ImportedData.MissingData.DataType.ACCOUNT_FROM, ImportedData.MissingData.DataType.ACCOUNT_TO -> {
                    val account = accounts?.firstOrNull { it.accountName == data.name }
                    if (account != null) {
                        data.transactionIndex.forEach {
                            updatedImportedData[it] =
                                if (data.dataType == ImportedData.MissingData.DataType.ACCOUNT_FROM) {
                                    updatedImportedData[it].copy(
                                        accountFrom = ImportedData.RequiredDataState.Acquired(
                                            id = account.accountId,
                                            name = account.accountName
                                        )
                                    )
                                } else {
                                    updatedImportedData[it].copy(
                                        accountTo = ImportedData.RequiredDataState.Acquired(
                                            id = account.accountId,
                                            name = account.accountName
                                        )
                                    )
                                }
                        }
                        updatedMissingData.remove(data)
                    }
                }

                ImportedData.MissingData.DataType.EXPENSES_CATEGORY, ImportedData.MissingData.DataType.INCOME_CATEGORY -> {
                    val categoryChain = data.name
                    val categoryID =
                        if (data.dataType == ImportedData.MissingData.DataType.EXPENSES_CATEGORY) {
                            expensesCategories?.get(categoryChain)
                        } else {
                            incomeCategories?.get(categoryChain)
                        }

                    if (categoryID != null) {
                        data.transactionIndex.forEach {
                            updatedImportedData[it] = updatedImportedData[it].copy(
                                categoryColumns = ImportedData.RequiredDataState.Acquired(
                                    id = categoryID,
                                    name = categoryChain
                                )
                            )
                        }
                        updatedMissingData.remove(data)
                    }
                }

                ImportedData.MissingData.DataType.TAG -> {
                    val tag = tags?.firstOrNull { it.name == data.name }
                    if (tag != null) {
                        data.transactionIndex.forEach { transactionIndex ->
                            val toUpdate = updatedImportedData[transactionIndex]
                            updatedImportedData[transactionIndex] = toUpdate
                                .copy(
                                    tags = toUpdate.tags.toMutableList().apply {
                                        replaceAll {
                                            if (it.name == tag.name) {
                                                ImportedData.RequiredDataState.Acquired(
                                                    id = tag.id,
                                                    name = tag.name
                                                )
                                            } else {
                                                it
                                            }
                                        }
                                    }
                                )
                        }
                        updatedMissingData.remove(data)

                    }
                }

                else -> {}
            }
        }
        return updatedImportedData to updatedMissingData
    }

    suspend fun checkRequiredData(backupData: List<TransactionBackupData>): Pair<List<ImportedData>, Set<ImportedData.MissingData>> {
        return withContext(dispatcher) {
            val accounts = accountRepository.getAllAccounts().firstOrNull()?.map {
                it.accounts
            }?.flatten()?.groupBy {
                it.accountName
            }
            val expensesCategories =
                categoryRepository.getCategories(TransactionType.EXPENSES.name)
                    .firstOrNull()?.let {
                        generateCategoriesMap(it)
                    }
            val incomeCategories =
                categoryRepository.getCategories(TransactionType.INCOME.name)
                    .firstOrNull()?.let {
                        generateCategoriesMap(it)
                    }
            val tags = tagRepository.getAllTags().firstOrNull()?.groupBy { it.name }
            val missingData = mutableSetOf<ImportedData.MissingData>()
            val importedData = backupData.mapIndexed { index, row ->
                var importedData = ImportedData(
                    transactionName = row.transactionName,
                    accountFrom = null,
                    accountTo = null,
                    transactionType = row.transactionType,
                    currency = row.currency,
                    accountAmount = row.accountAmount,
                    primaryAmount = row.primaryAmount,
                    amount = row.amount,
                    date = row.date.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN),
                    categoryColumns = generateCategoryRequiredState(
                        row,
                        expensesCategories,
                        incomeCategories,
                        index,
                        missingData
                    ),
                    tags = listOf()
                )

                // Account from
                row.accountFrom?.let { accountFromName ->
                    val account = accounts?.get(accountFromName)
                    if (account == null) {
                        appendMissingEntityData(
                            accountFromName,
                            index,
                            missingData,
                            ImportedData.MissingData.DataType.ACCOUNT_FROM
                        )
                        importedData = importedData.copy(
                            accountFrom = ImportedData.RequiredDataState.Missing(accountFromName)
                        )
                    } else {
                        importedData = importedData.copy(
                            accountFrom = ImportedData.RequiredDataState.Acquired(
                                account[0].accountId,
                                accountFromName
                            )
                        )
                    }
                }

                // Account to
                row.accountTo?.let { accountToName ->
                    val account = accounts?.get(accountToName)
                    if (account == null) {
                        appendMissingEntityData(
                            accountToName,
                            index,
                            missingData,
                            ImportedData.MissingData.DataType.ACCOUNT_TO
                        )
                        importedData = importedData.copy(
                            accountTo = ImportedData.RequiredDataState.Missing(accountToName)
                        )
                    } else {
                        importedData = importedData.copy(
                            accountTo = ImportedData.RequiredDataState.Acquired(
                                id = account[0].accountId,
                                name = accountToName
                            )
                        )
                    }
                }

                // Tags
                row.tags.forEach { tagName ->
                    val targetTag = tags?.get(tagName)
                    importedData = if (targetTag == null) {
                        appendMissingEntityData(
                            tagName,
                            index,
                            missingData,
                            ImportedData.MissingData.DataType.TAG
                        )
                        importedData.copy(
                            tags = importedData.tags.toMutableList()
                                .apply { add(ImportedData.RequiredDataState.Missing(tagName)) }
                        )
                    } else {
                        importedData.copy(
                            tags = importedData.tags.toMutableList().apply {
                                add(
                                    ImportedData.RequiredDataState.Acquired(
                                        targetTag[0].id,
                                        tagName
                                    )
                                )
                            }
                        )
                    }
                }

                importedData
            }

            importedData to missingData
        }
    }

    private fun generateCategoryRequiredState(
        row: TransactionBackupData,
        expensesCategories: Map<String, Int>?,
        incomeCategories: Map<String, Int>?,
        index: Int,
        missingData: MutableSet<ImportedData.MissingData>
    ): ImportedData.RequiredDataState? {
        val isExpenses = row.transactionType == TransactionType.EXPENSES.name
        val isTransfer = row.transactionType == TransactionType.TRANSFER.name
        val isUpdate = row.transactionType == TransactionType.UPDATE_ACCOUNT.name

        if (isTransfer || isUpdate) return null

        val categoryColumnsToString = row.categoryColumns.joinToString("-->")
        val catID = if (isExpenses) {
            expensesCategories?.get(categoryColumnsToString)
        } else {
            incomeCategories?.get(categoryColumnsToString)
        }

        // If data not exists
        return if (catID == null) {
            appendMissingEntityData(
                categoryColumnsToString,
                index,
                missingData,
                if (isExpenses) ImportedData.MissingData.DataType.EXPENSES_CATEGORY else ImportedData.MissingData.DataType.INCOME_CATEGORY
            )
            ImportedData.RequiredDataState.Missing(
                categoryColumnsToString
            )
        } else {
            ImportedData.RequiredDataState.Acquired(
                id = catID,
                name = categoryColumnsToString
            )
        }

    }

    private fun generateCategoriesMap(categories: TransactionCategories): Map<String, Int> {
        val res = mutableMapOf<String, Int>()
        categories.items.forEach {
            appendCategoryChain(it, listOf(), res)
        }

        return res
    }

    private fun appendCategoryChain(
        basicCategoryNode: TransactionCategories.BasicCategoryNode,
        visitedChain: List<String>,
        map: MutableMap<String, Int>
    ) {
        val updatedChain = visitedChain.toMutableList().apply { add(basicCategoryNode.categoryName) }
        map[updatedChain.joinToString("-->")] = basicCategoryNode.categoryId
        if (basicCategoryNode.children.isEmpty()) {
            return
        }

        basicCategoryNode.children.forEach {
            appendCategoryChain(it, updatedChain, map)
        }
    }

    private fun appendMissingEntityData(
        entityItemName: String,
        index: Int,
        missingData: MutableSet<ImportedData.MissingData>,
        type: ImportedData.MissingData.DataType
    ) {
        val data = missingData.firstOrNull {
            entityItemName == it.name && it.dataType == type
        }
        if (data == null) {
            missingData.add(
                ImportedData.MissingData(
                    name = entityItemName,
                    dataType = type,
                    transactionIndex = setOf(index)
                )
            )
        } else {
            val updated = data.copy(
                name = data.name,
                dataType = data.dataType,
                transactionIndex = data.transactionIndex.toMutableSet().apply { add(index) }
            )
            missingData.remove(data)
            missingData.add(updated)
        }
    }

    fun checkDataValidation() {

    }
}