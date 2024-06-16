package com.charmflex.flexiexpensesmanager.features.category.category.ui.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.ListTable
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x10
import com.charmflex.flexiexpensesmanager.ui_common.grid_x14
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun CategoryStatScreen(viewModel: CategoryStatViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()

    SGScaffold(
        modifier = Modifier.padding(grid_x2),
        topBar = {
            BasicTopBar(
                title = stringResource(id = R.string.category_stat_detail_topbar_title)
            )
        }
    ) {
        DateFilterBar(currentDateFilter = dateFilter, onDateFilterChanged = viewModel::onDateFilterChanged)
        ListTable(items = viewState.categoryParentStats) { _, item ->
            FeColumnContainer(
                modifier = Modifier.padding(vertical = grid_x1)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FEBody2(text = item.name, modifier = Modifier.weight(0.4f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    FEBody2(text = item.amount, modifier = Modifier
                        .weight(0.6f)
                        .padding(vertical = grid_x1),)
                    FEBody2(text = item.percentage)
                }
            }
        }
    }
}