package com.charmflex.flexiexpensesmanager.features.category.category.ui.stat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.FEMetaData1
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.ListTable
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_5
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryStatScreen(viewModel: CategoryStatViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()

    SGScaffold(
        modifier = Modifier.padding(grid_x2),
        isLoading = viewState.isLoading,
        topBar = {
            BasicTopBar(
                title = stringResource(id = R.string.category_stat_detail_topbar_title)
            )
        },
        screenName = "CategoryStatScreen"
    ) {
        DateFilterBar(
            currentDateFilter = dateFilter,
            onDateFilterChanged = viewModel::onDateFilterChanged
        )
        SecondaryTabRow(
            modifier = Modifier.padding(vertical = grid_x1),
            selectedTabIndex = viewState.selectedTab.index
        ) {
            CategoryStatTabItem.values().forEach { tabItem ->
                Tab(
                    modifier = Modifier.padding(vertical = grid_x0_5),
                    selected = viewState.selectedTab == tabItem,
                    onClick = { viewModel.onTabChanged(tabItem) }) {
                    FEHeading4(text = stringResource(id = tabItem.nameId))
                    FEMetaData1(
                        text = if (tabItem == CategoryStatTabItem.INCOME) viewState.incomeCategoryStats.amount else viewState.expensesCategoryStats.amount,
                        color = Color.Gray
                    )
                }
            }
        }
        ListTable(items = viewState.categoryList) { _, item ->
            FeColumnContainer(
                modifier = Modifier.clickable {
                    viewModel.onNavigateCategoryTransactionDetailScreen(item.id, item.name, item.type)
                }.padding(vertical = grid_x1)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FEBody2(
                        text = item.name,
                        modifier = Modifier.weight(0.4f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    FEBody2(
                        text = item.amount,
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(vertical = grid_x1),
                    )
                    FEBody2(text = item.percentage)
                }
            }
        }
    }
}