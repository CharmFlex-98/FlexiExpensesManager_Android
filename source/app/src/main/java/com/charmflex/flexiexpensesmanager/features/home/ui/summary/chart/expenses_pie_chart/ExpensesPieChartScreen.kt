package com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_pie_chart

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
    Box(
        modifier = Modifier
            .padding(vertical = grid_x1)
            .weight(1f)
            .padding(grid_x2),
        contentAlignment = Alignment.Center
    ) {
        val testPieChartData: List<PieChartData> = listOf(
            PieChartData(
                partName = "House Loan",
                data = 500.0,
                color = Color(0xFF22A699),
            ),
            PieChartData(
                partName = "Food",
                data = 700.0,
                color = Color(0xFFF2BE22),
            ),
            PieChartData(
                partName = "Shopping",
                data = 500.0,
                color = Color(0xFFF29727),
            ),
            PieChartData(
                partName = "Entertainment",
                data = 100.0,
                color = Color(0xFFF24C3D),
            ),
        )

        PieChart(
            modifier = Modifier.fillMaxSize(),
            animation = TweenSpec(durationMillis = 1000),
            pieChartData = testPieChartData,
            ratioLineColor = Color.LightGray,
            textRatioStyle = TextStyle(color = Color.Gray),
            descriptionStyle = TextStyle(color = Color.White),
            legendPosition = LegendPosition.BOTTOM
        )
    }

}