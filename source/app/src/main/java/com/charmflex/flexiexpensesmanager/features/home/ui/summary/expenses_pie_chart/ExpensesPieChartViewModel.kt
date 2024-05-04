package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aay.compose.donutChart.model.PieChartData
import com.charmflex.flexiexpensesmanager.features.home.usecases.GetCategoryPercentageUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

internal class ExpensesPieChartViewModel @Inject constructor(
    private val getCategoryPercentageUseCase: GetCategoryPercentageUseCase
) : ViewModel() {

    var pieViewState = mutableStateOf<List<PieChartData>>(listOf())
        private set
    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val res = getCategoryPercentageUseCase()
            res?.let {
                pieViewState.value = generatePieChartData(res)
            }
        }
    }

    private fun generatePieChartData(data: Map<String, Int>): List<PieChartData> {
        val res = mutableListOf<PieChartData>()
        for ((rootCategory, amount) in data.entries) {
            res.add(
                PieChartData(
                    data = amount.toDouble(),
                    color = generateRandomColor(),
                    partName = rootCategory
                )
            )
        }
        return res
    }

    private fun generateRandomColor(): Color {
        val random = Random.Default
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color(red, green, blue)
    }
}