package com.charmflex.flexiexpensesmanager.features.backup.data.mapper

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategory
import java.time.LocalDate
import javax.inject.Inject

internal class TransactionBackupDataMapper @Inject constructor() : Mapper<Pair<List<Transaction>, Map<Int, TransactionCategory>>, List<TransactionBackupData>> {
    override fun map(from: Pair<List<Transaction>, Map<Int, TransactionCategory>>): List<TransactionBackupData> {
        val transactionCategoryMap = from.second
        return from.first.map {
            val currentCategory = it.transactionCategory?.id?.let {
                transactionCategoryMap.getOrElse(it) { null }
            }
            TransactionBackupData(
                transactionName = it.transactionName,
                accountFrom = it.transactionAccountFrom?.accountName,
                accountTo = it.transactionAccountTo?.accountName,
                transactionType = it.transactionTypeCode,
                currency = it.currency,
                currencyRate = it.rate.toDouble(),
                primaryCurrencyRate = it.primaryCurrencyRate?.toDouble(),
                accountMinorUnitAmount = it.accountMinorUnitAmount / 100.toDouble(), // TODO
                primaryMinorUnitAmount = it.primaryMinorUnitAmount / 100.toDouble(), // TODO
                amount = it.minorUnitAmount / 100.toDouble(), // TODO: Need to use default fraction
                date = it.transactionDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN) ?: LocalDate.now(),
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