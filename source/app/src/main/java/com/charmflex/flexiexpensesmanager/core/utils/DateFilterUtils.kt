package com.charmflex.flexiexpensesmanager.core.utils

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
sealed interface DateFilter : Parcelable {

    @SuppressLint("ParcelCreator")
    object All : DateFilter

    @SuppressLint("ParcelCreator")
    data class Monthly(
        // If monthBefore = 0, means this month; if = 1, means last month and so on
        val monthBefore: Long
    ) : DateFilter

    @SuppressLint("ParcelCreator")
    data class Custom(
        val from: LocalDate,
        val to: LocalDate
    ) : DateFilter
}


internal fun DateFilter?.getStartDate(): String? {
    return  when (this) {
        is DateFilter.Monthly -> {
            LocalDate.now().minusMonths(this.monthBefore).withDayOfMonth(1).toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
        }
        is DateFilter.Custom -> {
            this.from.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
        }
        else -> null
    }
}

internal fun DateFilter?.getEndDate(): String? {
    return  when (this) {
        is DateFilter.Monthly -> {
            val localDateNow = LocalDate.now()
            localDateNow.minusMonths(this.monthBefore).let {
                val res = it.withDayOfMonth(it.lengthOfMonth())
                if (res.isAfter(localDateNow)) localDateNow
                else res
            }.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
        }
        is DateFilter.Custom -> {
            val localDateNow = LocalDate.now()
            val res = if (this.to.isAfter(this.to)) localDateNow else this.to
            res.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
        }
        else -> null
    }
}