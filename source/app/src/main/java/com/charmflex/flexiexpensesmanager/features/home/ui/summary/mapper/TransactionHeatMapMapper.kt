package com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map.Level
import com.charmflex.flexiexpensesmanager.features.home.usecases.DailyTransaction
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

internal class TransactionHeatMapMapper(
    private val lowerBoundThreshold: Float,
    private val higherBoundThreshold: Float
) : Mapper<List<DailyTransaction>, Map<LocalDate, Color>> {

    class Factory @Inject constructor() {
        fun create(
            lowerBoundThreshold: Float,
            higherBoundThreshold: Float
        ): TransactionHeatMapMapper {
            return TransactionHeatMapMapper(lowerBoundThreshold, higherBoundThreshold)
        }
    }
    override fun map(from: List<DailyTransaction>): Map<LocalDate, Color> {
        val res = mutableMapOf<LocalDate, Color>()
        val dailyTransactions = from.sortedBy { it.amountInCent }
        val size = from.size

        if (size == 0) return mapOf()

        val median = if (size % 2 == 0) {
            val left = dailyTransactions[size/2 - 1]
            val right = dailyTransactions[size/2]
            left.amountInCent + right.amountInCent
        } else {
            val middle = from[size/2]
            middle.amountInCent
        }

        dailyTransactions.forEach { dailyTransaction ->
            val color = when {
                dailyTransaction.amountInCent < median * lowerBoundThreshold -> Level.One.color
                dailyTransaction.amountInCent > median * higherBoundThreshold -> Level.Four.color
                else -> getColor(dailyTransaction.amountInCent/median.toFloat())
            }
            res[dailyTransaction.date] = color
        }

        return res
    }

    private fun getColor(medianRatio: Float): Color {
        val fraction = clip((medianRatio - lowerBoundThreshold)/(higherBoundThreshold-lowerBoundThreshold))
        return lerp(Level.One.color, Level.Four.color, fraction)
    }

    private fun clip(value: Float, min: Float = 0f, max: Float = 1f): Float {
        return min(max(value, min), max)
    }
}