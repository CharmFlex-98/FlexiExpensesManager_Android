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
            (it.value.toFloat()/total).toLevel()
        }
    }

    private fun Float.toLevel(): Level {
        return when {
            this <= 0 -> Level.Zero
            this > 0f && this <= 0.25f -> Level.One
            this > 0.25f && this <= 0.5f -> Level.Two
            this > 0.5f && this <= 0.75f -> Level.Three
            else -> Level.Four
        }
    }
}