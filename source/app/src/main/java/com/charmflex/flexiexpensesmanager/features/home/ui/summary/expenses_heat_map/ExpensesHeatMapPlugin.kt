package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardPlugin

internal class ExpensesHeatMapPlugin(
    private val viewModel: ExpensesHeatMapViewModel
): DashboardPlugin {
    @Composable
    override fun ColumnScope.Render() {
        ExpensesHeatMapScreen(viewModel = viewModel)
    }

    override fun refresh() {
        viewModel.refresh()
    }
}