package com.charmflex.flexiexpensesmanager.core.utils

import java.time.LocalDate

sealed interface DateFilter {
    data class Monthly(
        // If monthBefore = 0, means this month; if = 1, means last month and so on
        val monthBefore: Long
    ) : DateFilter

    data class Custom(
        val from: LocalDate,
        val to: LocalDate
    ) : DateFilter
}