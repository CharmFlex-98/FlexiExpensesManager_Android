package com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_pie_chart

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardPlugin
import javax.inject.Inject

internal class ExpensesPieChartDashboardPlugin @Inject constructor(
    private val viewModel: ExpensesPieChartViewModel
): DashboardPlugin {
    @Composable
    override fun ColumnScope.Render() {
        ExpensesPieChartScreen(viewModel = viewModel)
    }

    override fun refresh() {
        // TODO: to refresh
    }
}