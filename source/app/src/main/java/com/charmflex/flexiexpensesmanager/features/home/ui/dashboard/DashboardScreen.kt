package com.charmflex.flexiexpensesmanager.features.home.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_pie_chart.ExpensesPieChartViewModel

@Composable
internal fun DashboardScreen(
    plugins: List<DashboardPlugin>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        plugins.forEach {
            with(it) { Render() }
        }
    }
}