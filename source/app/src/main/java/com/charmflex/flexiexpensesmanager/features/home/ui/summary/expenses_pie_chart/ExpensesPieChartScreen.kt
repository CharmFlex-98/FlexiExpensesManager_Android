package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.donutChart.PieChart
import com.charmflex.flexiexpensesmanager.ui_common.SGMediumPrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_25
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x47
import com.charmflex.flexiexpensesmanager.ui_common.grid_x63
import kotlinx.coroutines.flow.update

@Composable
internal fun ColumnScope.ExpensesPieChartScreen(
    viewModel: ExpensesPieChartViewModel
) {
    val pieChartViewState by viewModel.viewState.collectAsState()

    Column(
        modifier = Modifier
            .padding(vertical = grid_x1)
            .weight(1f)
            .padding(grid_x2),
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    viewModel.onToggleTagDialog(true)
                }
                .padding(grid_x2)
        ) {
            Text(text = "Select tag")
        }
        Box(
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                modifier = Modifier.fillMaxSize(),
                animation = TweenSpec(durationMillis = 1000),
                pieChartData = pieChartViewState.pieChartData,
                ratioLineColor = Color.LightGray,
                textRatioStyle = TextStyle(color = Color.Gray),
                descriptionStyle = TextStyle(color = Color.White),
                legendPosition = LegendPosition.BOTTOM
            )
        }
    }


    if (pieChartViewState.showTagFilterDialog) {
        var tagFilterTemp by remember(pieChartViewState.tagFilter) {
            mutableStateOf(pieChartViewState.tagFilter)
        }

        Dialog(onDismissRequest = { viewModel.onToggleTagDialog(false) }) {
            Column(
                modifier = Modifier
                    .height(grid_x47)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .verticalScroll(rememberScrollState())
            ) {
                tagFilterTemp.forEachIndexed { index, it ->
                    if (index != 0) HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    Row(
                        modifier = Modifier.padding(grid_x1),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = it.name,
                            textAlign = TextAlign.Start
                        )
                        RadioButton(selected = it.selected, onClick = {
                            tagFilterTemp = tagFilterTemp.map { item ->
                                if (it.id == item.id) {
                                    it.copy(
                                        selected = !it.selected
                                    )
                                } else item
                            }
                        })
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(grid_x2),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SGMediumPrimaryButton(modifier = Modifier.fillMaxWidth(), text = "Set") {
                        viewModel.onSetTagFilter(tagFilterTemp)
                    }
                }
            }
        }
    }
}