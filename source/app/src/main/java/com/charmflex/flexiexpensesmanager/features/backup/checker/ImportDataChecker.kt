package com.charmflex.flexiexpensesmanager.features.backup.checker

import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupData
import com.charmflex.flexiexpensesmanager.features.backup.ui.ImportedData
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
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
    suspend fun updateRequiredData(missingData: Set<ImportedData.MissingData>, importedData: List<ImportedData>): Pair<List<ImportedData>, Set<ImportedData.MissingData>> {
        return importedData to missingData
    }

//    private suspend fun updateAccountImportedData(missingData: ImportedData.MissingData, id: Long, initialData: List<ImportedData>): List<ImportedData> {
//        val updatedData = initialData.toMutableList()
//        val account = accountRepository.getAccountById(id.toInt())
//        val newState = if (account.name == missingData.name) {
//            ImportedData.RequiredDataState.Acquired(
//                id = account.id,
//                name = account.name
//            )
//        } else {
//            missingData
//        }
//        for (index in missingData.transactionIndex) {
//            val item = initialData[index]
//            val newItem = if (missingData.dataType == ImportedData.MissingData.DataType.ACCOUNT_FROM) {
//                item.copy(
//                    accountFrom = acquiredData
//                )
//            } else {
//                item.copy(
//                    accountTo = acquiredData
//                )
//            }
//            updatedData[index] = newItem
//        }
//        return updatedData
//    }
    suspend fun checkRequiredData(backupData: List<TransactionBackupData>): Pair<List<ImportedData>, Set<ImportedData.MissingData>> {
        return withContext(dispatcher) {
            val accounts = accountRepository.getAllAccounts().firstOrNull()?.map {
                it.accounts
            }?.flatten()
            val expensesCategories = categoryRepository.getAllCategoriesIncludedDeleted()
                .filter { it.transactionTypeCode == TransactionType.EXPENSES.name }
            val incomeCategories = categoryRepository.getAllCategoriesIncludedDeleted()
                .filter { it.transactionTypeCode == TransactionType.INCOME.name }
            val tags = tagRepository.getAllTags().firstOrNull()
            val missingData = mutableSetOf<ImportedData.MissingData>()
            val importedData = backupData.mapIndexed { index, row ->
                var importedData = ImportedData(
                    transactionName = row.transactionName,
                    accountFrom = null,
                    accountTo = null,
                    transactionType = row.transactionType,
                    currency = row.currency,
                    currencyRate = row.currencyRate,
                    amount = row.amount,
                    date = row.date,
                    categoryColumns = listOf(),
                    tags = listOf()
                )

                // Account from
                row.accountFrom?.let { accountFromName ->
                    val account = accounts?.firstOrNull { it.accountName == accountFromName }
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
                                account.accountId,
                                accountFromName
                            )
                        )
                    }
                }

                // Account to
                row.accountTo?.let { accountToName ->
                    val account = accounts?.firstOrNull { it.accountName == accountToName }
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
                                id = account.accountId,
                                name = accountToName
                            )
                        )
                    }
                }

                // Category
                row.categoryColumns.forEachIndexed { ind, categoryName ->
                    val isExpenses = row.transactionType == TransactionType.EXPENSES.name
                    val categories = if (isExpenses) expensesCategories else incomeCategories
                    val cat = categories.firstOrNull { it.name == categoryName }

                    importedData = if (cat == null) {
                        appendMissingEntityData(
                            categoryName,
                            index,
                            missingData,
                            if (isExpenses) ImportedData.MissingData.DataType.EXPENSES_CATEGORY else ImportedData.MissingData.DataType.INCOME_CATEGORY
                        )
                        importedData.copy(
                            categoryColumns = importedData.categoryColumns.toMutableList()
                                .apply { add(ImportedData.RequiredDataState.Missing(categoryName)) }
                        )
                    } else {
                        importedData.copy(
                            categoryColumns = importedData.categoryColumns.toMutableList().apply {
                                add(
                                    ImportedData.RequiredDataState.Acquired(
                                        cat.id,
                                        categoryName
                                    )
                                )
                            }
                        )
                    }
                }

                // Tags
                row.tags.forEach { tagName ->
                    val targetTag = tags?.firstOrNull { it.name == tagName }
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
                                        targetTag.id,
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