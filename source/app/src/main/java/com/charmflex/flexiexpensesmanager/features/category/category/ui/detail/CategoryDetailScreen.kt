package com.charmflex.flexiexpensesmanager.features.category.category.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryList
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
internal fun CategoryDetailScreen(
    viewModel: CategoryDetailViewModel
) {
    val viewState by viewModel.categoryDetailViewState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    chartEntryModelProducer.setEntries(
        listOf(1, 2, 3, 4).map {
            FloatEntry(it.toFloat(), it.toFloat())
        }
    )
    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(
                title = viewState.categoryName
            )
        }
    ) {
//        ProvideChartStyle {
//            Chart(
//                modifier = Modifier.padding(vertical = grid_x2),
//                chart = lineChart(),
//                chartModelProducer = chartEntryModelProducer,
//            )
//        }
        DateFilterBar(
            modifier = Modifier.padding(vertical = grid_x1),
            currentDateFilter = dateFilter,
            onDateFilterChanged = { viewModel.onDateChanged(it) })
        Box {
            TransactionHistoryList(transactionHistoryViewModel = viewModel)
        }
    }
}