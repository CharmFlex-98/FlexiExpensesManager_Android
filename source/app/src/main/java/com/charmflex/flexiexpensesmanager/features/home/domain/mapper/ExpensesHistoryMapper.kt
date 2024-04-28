package com.charmflex.flexiexpensesmanager.features.home.domain.mapper

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_YEAR_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.core.utils.fromISOToStringWithPattern
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.home.ui.history.ExpensesHistoryHeader
import com.charmflex.flexiexpensesmanager.features.home.ui.history.ExpensesHistoryItem
import com.charmflex.flexiexpensesmanager.features.home.ui.history.ExpensesHistorySection
import javax.inject.Inject

internal class ExpensesHistoryMapper @Inject constructor() : Mapper<List<ExpensesData>, List<ExpensesHistoryItem>> {
    override fun map(from: List<ExpensesData>): List<ExpensesHistoryItem> {
        val res = mutableListOf<ExpensesHistoryItem>()
        val sectionSet = mutableSetOf<String>()
        from.groupBy {
            val timeStamp = it.timeStamp
            Pair(timeStamp.toMonthYearFormat(), timeStamp.toDateFormat())
        }.map { (pair , history) ->
            val monthYear = pair.first
            val date = pair.second
            val dateIncluded = sectionSet.contains(monthYear).also {
                if (!it) sectionSet.add(monthYear)
            }

            res.add(
                ExpensesHistoryHeader(
                    dateKey = date,
                    title = if (dateIncluded) null else monthYear,
                    subtitle = date
                )
            )

            res.add(
                ExpensesHistorySection(
                    dateKey = date,
                    items = history.map {
                        ExpensesHistorySection.SectionItem(
                            name = it.name,
                            amount = it.amount.toString(),
                            type = it.type,
                            category = it.category,
                        )
                    }
                )
            )
        }

        return res
    }
}

private fun String.toMonthFormat(): String {
    return this.fromISOToStringWithPattern(MONTH_ONLY_DEFAULT_PATTERN)
}
private fun String.toDateFormat(): String {
    return this.fromISOToStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
}

private fun String.toMonthYearFormat(): String {
    return this.fromISOToStringWithPattern(MONTH_YEAR_PATTERN)
}