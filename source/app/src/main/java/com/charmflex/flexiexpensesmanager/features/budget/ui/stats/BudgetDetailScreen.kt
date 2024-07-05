package com.charmflex.flexiexpensesmanager.features.budget.ui.stats

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.BasicColumnContainerItemList
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FECallout2
import com.charmflex.flexiexpensesmanager.ui_common.FECallout3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading2
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.FEMetaData1
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x3
import com.charmflex.flexiexpensesmanager.ui_common.grid_x4
import com.charmflex.flexiexpensesmanager.ui_common.grid_x6

@Composable
internal fun BudgetDetailScreen(
    viewModel: BudgetDetailViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val items = viewState.onScreenBudgetSections

    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(title = stringResource(id = R.string.budget_detail_app_bar_title))
        }
    ) {
        items.forEach {
            BasicColumnContainerItemList(
                modifier = Modifier
                    .padding(bottom = grid_x4)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearEasing
                        )
                    ),
                items = it.contents,
                itemContent = {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = grid_x2),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.padding(bottom = grid_x2)
                        ) {
                            when (it.level) {
                                BudgetStatViewState.CategoryBudgetItem.Level.FIRST -> {
                                    FEHeading4(text = it.categoryName)
                                }

                                BudgetStatViewState.CategoryBudgetItem.Level.SECOND -> {
                                    FEBody1(text = it.categoryName)
                                }

                                BudgetStatViewState.CategoryBudgetItem.Level.THIRD -> {
                                    FEBody3(text = it.categoryName)
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            FEBody2(text = stringResource(id = R.string.generic_budget) + ": " + it.budget)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FEMetaData1(text = stringResource(id = R.string.generic_spend))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .padding(grid_x2)
                                    .weight(1f),
                                progress = { it.expensesBudgetRatio },
                                trackColor = Color.LightGray,
                                color = if (it.expensesBudgetRatio > 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                FEMetaData1(text = it.expensesAmount)
                                FEMetaData1(text = "(${toPercentageString(it.expensesBudgetRatio*100)})")
                            }
                        }

                    }
                },
                actionItems = {
                    Box(
                        modifier = Modifier.size(grid_x3)
                    ) {
                        if (it.expandable) {
                            IconButton(onClick = { viewModel.onToggleExpandable(it) }) {
                                if (viewState.isItemExpanded(it)) SGIcons.Collapse() else SGIcons.Expand()
                            }
                        }
                    }
                }
            )
        }
    }

}

private fun toPercentageString(float: Float): String {
    return "%.2f".format(float) + "%"
}