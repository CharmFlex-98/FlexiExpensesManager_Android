package com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_YEAR_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.core.utils.fromISOToStringWithPattern
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryHeader
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryItem
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistorySection
import javax.inject.Inject

internal class TransactionHistoryMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter
) : Mapper<List<Transaction>, List<TransactionHistoryItem>> {
    override fun map(from: List<Transaction>): List<TransactionHistoryItem> {
        val res = mutableListOf<TransactionHistoryItem>()
        val sectionSet = mutableSetOf<String>()
        from.groupBy {
            val date = it.transactionDate
            Pair(date.toMonthYearFormat(), date)
        }.map { (pair , history) ->
            val monthYear = pair.first
            val date = pair.second
            val dateIncluded = sectionSet.contains(monthYear).also {
                if (!it) sectionSet.add(monthYear)
            }

            res.add(
                TransactionHistoryHeader(
                    dateKey = date,
                    title = if (dateIncluded) null else monthYear,
                    subtitle = date
                )
            )

            res.add(
                TransactionHistorySection(
                    dateKey = date,
                    items = history.map {
                        TransactionHistorySection.SectionItem(
                            id = it.transactionId,
                            name = it.transactionName,
                            amount = currencyFormatter.format(it.amountInCent, it.currency),
                            type = it.transactionTypeCode,
                            category = it.transactionCategory?.name ?: "",
                            iconResId = it.transactionTypeCode.getTransactionIconResId()
                        )
                    }
                )
            )
        }

        return res
    }
}

private fun String.getTransactionIconResId(): Int {
    return when (this) {
        TransactionType.TRANSFER.name -> R.drawable.ic_transfer_icon
        TransactionType.INCOME.name -> R.drawable.ic_income
        else -> R.drawable.ic_spend
    }
}

private fun String.toMonthYearFormat(): String {
    return this.toLocalDate(DATE_ONLY_DEFAULT_PATTERN).toStringWithPattern(MONTH_YEAR_PATTERN)
}