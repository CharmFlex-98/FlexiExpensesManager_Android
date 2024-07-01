package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardPlugin
import javax.inject.Inject

internal class ExpensesChartDashboardPlugin @Inject constructor(
    private val viewModel: ExpensesChartViewModel
): DashboardPlugin {
    @Composable
    override fun ColumnScope.Render() {
        ExpensesChartScreen(viewModel = viewModel)
    }

    override fun refresh() {
        viewModel.refresh()
    }
}