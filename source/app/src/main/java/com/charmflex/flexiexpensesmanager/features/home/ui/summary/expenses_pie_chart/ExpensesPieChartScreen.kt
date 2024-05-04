package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun ColumnScope.ExpensesPieChartScreen(
    viewModel: ExpensesPieChartViewModel
) {
    val pieChartData by viewModel.pieViewState
    Box(
        modifier = Modifier
            .padding(vertical = grid_x1)
            .weight(1f)
            .padding(grid_x2),
        contentAlignment = Alignment.Center
    ) {
        PieChart(
            modifier = Modifier.fillMaxSize(),
            animation = TweenSpec(durationMillis = 1000),
            pieChartData = pieChartData,
            ratioLineColor = Color.LightGray,
            textRatioStyle = TextStyle(color = Color.Gray),
            descriptionStyle = TextStyle(color = Color.White),
            legendPosition = LegendPosition.BOTTOM
        )
    }

}