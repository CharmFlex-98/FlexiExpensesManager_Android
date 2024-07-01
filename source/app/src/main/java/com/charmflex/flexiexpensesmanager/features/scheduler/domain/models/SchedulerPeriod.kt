package com.charmflex.flexiexpensesmanager.features.scheduler.domain.models

internal enum class SchedulerPeriod {
    DAILY, MONTHLY, YEARLY, UNKNOWN;

    companion object {
        fun fromString(name: String): SchedulerPeriod {
            return when (name) {
                DAILY.name -> DAILY
                MONTHLY.name -> MONTHLY
                YEARLY.name -> YEARLY
                else -> UNKNOWN
            }
        }
    }
}