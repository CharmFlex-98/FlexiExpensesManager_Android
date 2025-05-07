package com.charmflex.flexiexpensesmanager.features.category.category.ui.detail

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart.rememberMarker
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryList
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import java.time.Instant
import java.time.ZoneId

@Composable
internal fun CategoryDetailScreen(
    viewModel: CategoryDetailViewModel
) {
    val viewState by viewModel.categoryDetailViewState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val color = MaterialTheme.colorScheme.secondary.toArgb()
    val pointerShapeComponent = shapeComponent(shape = pillShape, color = MaterialTheme.colorScheme.tertiary)
    val markerListener = remember { DefaultMarkerListener {
        viewModel.onChartItemSelected(it)
    }}
    SGScaffold(
        modifier = Modifier
            .fillMaxSize().padding(grid_x2),
        topBar = {
            BasicTopBar(
                title = viewState.categoryName
            )
        },
        screenName = "CategoryDetailScreen"
    ) {
        LaunchedEffect(key1 = viewState.lineChartData.entries) {
            viewModel.chartEntryModelProducer.setEntries(
                viewState.lineChartData.entries.map {
                    FloatEntry(it.first.toFloat(), it.second.toFloat())
                }
            )
        }
        ProvideChartStyle {
            val defaultLine = currentChartStyle.lineChart.lines
            Chart(
                chart = lineChart(
                    lines = remember(defaultLine) {
                        defaultLine.map { defaultColumn ->
                            LineChart.LineSpec(
                                lineColor = color,
                                point = pointerShapeComponent,
                                pointSizeDp = 8f
                            )
                        }
                    },
                ),
                markerVisibilityChangeListener = markerListener,
                chartModelProducer = viewModel.chartEntryModelProducer,
                marker = rememberMarker(),
                startAxis = rememberStartAxis(
                    label = axisLabelComponent(
                        textSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    titleComponent = axisLabelComponent(
                        textSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    title = "Amount"
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { x, _ ->
                        Instant.ofEpochSecond(x.toLong()).atZone(ZoneId.systemDefault())
                            .toLocalDate().toStringWithPattern(
                                DATE_ONLY_DEFAULT_PATTERN
                        )
                    },
                    label = axisLabelComponent(
                        lineCount = 3,
                        textSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    titleComponent = axisLabelComponent(
                        textSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    title = "Date"
                ),
            )
        }
        DateFilterBar(
            currentDateFilter = dateFilter,
            onDateFilterChanged = { viewModel.onDateChanged(it) }
        )
        TransactionHistoryList(
            transactionHistoryViewModel = viewModel
        )
    }
}

private class DefaultMarkerListener(
    private val onMarkerShown : (Marker.EntryModel?) -> Unit
) : MarkerVisibilityChangeListener {
    override fun onMarkerShown(marker: Marker, markerEntryModels: List<Marker.EntryModel>) {
        super.onMarkerShown(marker, markerEntryModels)
        onMarkerShown(markerEntryModels.getOrNull(0))
    }
}