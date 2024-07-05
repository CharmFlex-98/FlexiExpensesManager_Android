package com.charmflex.flexiexpensesmanager.features.budget.ui.stats

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                    Column {
                        FEBody2(text = it.budget)
                        FEBody2(text = it.expensesAmount)
                    }
                }, 
                actionItems = {
                    Box(
                        modifier = Modifier.size(grid_x6)
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