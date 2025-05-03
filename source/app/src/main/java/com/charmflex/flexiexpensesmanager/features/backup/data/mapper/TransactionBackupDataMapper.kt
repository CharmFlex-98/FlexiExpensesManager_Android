package com.charmflex.flexiexpensesmanager.features.backup.data.mapper

import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.SuspendableMapper
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategory
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import java.time.LocalDate
import javax.inject.Inject

internal class TransactionBackupDataMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val userCurrencyRepository: UserCurrencyRepository
) : SuspendableMapper<Pair<List<Transaction>, Map<Int, TransactionCategory>>, List<TransactionBackupData>> {
    override suspend fun map(from: Pair<List<Transaction>, Map<Int, TransactionCategory>>): List<TransactionBackupData> {
        val transactionCategoryMap = from.second
        val primaryCurrency = userCurrencyRepository.getPrimaryCurrency()
        return from.first.map {
            val currentCategory = it.transactionCategory?.id?.let {
                transactionCategoryMap.getOrElse(it) { null }
            }

            // This should always have value.
            val accountCurrency = when (TransactionType.fromString(it.transactionTypeCode)) {
                TransactionType.EXPENSES -> it.transactionAccountFrom?.currency
                TransactionType.INCOME, TransactionType.TRANSFER -> it.transactionAccountTo?.currency
                else -> it.currency
            } ?: run {
                val item = it
                println(it)
                throw Exception("account currency is not found, because account currency is null")
            }
            TransactionBackupData(
                transactionName = it.transactionName,
                accountFrom = it.transactionAccountFrom?.accountName,
                accountTo = it.transactionAccountTo?.accountName,
                transactionType = it.transactionTypeCode,
                currency = it.currency,
                accountAmount = currencyFormatter.formatWithoutSymbol(it.accountMinorUnitAmount, accountCurrency).toDouble(),
                primaryAmount = currencyFormatter.formatWithoutSymbol(it.primaryMinorUnitAmount, primaryCurrency).toDouble(),
                amount = currencyFormatter.formatWithoutSymbol(it.minorUnitAmount, it.currency).toDouble(), // TODO: Need to use default fraction
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