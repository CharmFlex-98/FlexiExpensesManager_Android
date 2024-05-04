package com.charmflex.flexiexpensesmanager.features.transactions.data.mapper

import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map.Level
import java.time.LocalDate
import javax.inject.Inject

internal class HeatMapLevelMapper @Inject constructor() {

    fun map(from: Map<LocalDate, Int>): Map<LocalDate, Level> {
        var total = 0
        from.entries.forEach {
            total += it.value
        }

        return from.mapValues {
            (it.value/total).toFloat().toLevel()
        }
    }

    private fun Float.toLevel(): Level {
        return when {
            this <= 0 -> Level.One
            this > 0f && this <= 0.3f -> Level.Two
            this > 0.3f && this <= 0.7f -> Level.Three
            else -> Level.Four
        }
    }
}