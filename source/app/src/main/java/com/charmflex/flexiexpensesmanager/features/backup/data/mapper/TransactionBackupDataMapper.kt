package com.charmflex.flexiexpensesmanager.features.backup.data.mapper

import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategory
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import javax.inject.Inject

internal class TransactionBackupDataMapper @Inject constructor(
    private val transactionCategoryRepository: TransactionCategoryRepository,
) : Mapper<List<Transaction>, List<TransactionBackupData>> {
    override suspend fun map(from: List<Transaction>): List<TransactionBackupData> {
        val transactionCategoryMap = transactionCategoryRepository.getAllCategoriesIncludedDeleted().groupBy {
            it.id
        }.mapValues {
            it.value[0]
        }

        return from.map {
            val currentCategory = it.transactionCategory?.id?.let {
                transactionCategoryMap.getOrElse(it) { null }
            }
            TransactionBackupData(
                transactionName = it.transactionName,
                accountFrom = it.transactionAccountFrom?.name,
                accountTo = it.transactionAccountTo?.name,
                transactionType = it.transactionTypeCode,
                currency = it.currency,
                currencyRate = it.rate.toDouble(),
                amount = it.amountInCent / 100.toDouble(),
                date = it.transactionDate,
                categoryColumns = generateCategoryColumns(currentCategory?.let { res -> mutableListOf(res.name) } ?: mutableListOf(),  transactionCategoryMap, currentCategory).reversed(),
                tags = it.tags.map { it.name }
            )
        }
    }

    private fun generateCategoryColumns(columns: MutableList<String>, transactionCategoryMap: Map<Int, TransactionCategory>, currentCategory: TransactionCategory?): List<String> {
        return if (currentCategory == null) return emptyList()
        else if (currentCategory.parentId == 0) return columns.toList()
        else {
            val parentCategory = transactionCategoryMap[currentCategory.parentId] ?: return columns
            generateCategoryColumns(columns.apply { add(parentCategory.name) }, transactionCategoryMap, parentCategory)
        }
    }

}