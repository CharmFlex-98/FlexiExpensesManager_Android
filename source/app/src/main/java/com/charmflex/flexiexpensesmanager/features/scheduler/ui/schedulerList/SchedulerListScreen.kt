package com.charmflex.flexiexpensesmanager.features.scheduler.ui.schedulerList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.ExpensesHistoryItem
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2


@Composable
internal fun SchedulerListScreen(transactionSchedulerListViewModel: SchedulerListViewModel) {
    val viewState by transactionSchedulerListViewModel.viewState.collectAsState()
    SGScaffold(
        modifier = Modifier.padding(grid_x2),
        topBar = {
            BasicTopBar(
                actions = {
                    IconButton(onClick = { transactionSchedulerListViewModel.addScheduler() }) {
                        SGIcons.Add()
                    }
                }
            )
        }
    ) {
        viewState.schedulerItems.forEach {
            ScheduledTransactionItem(
                id = it.id,
                name = it.name,
                amount = it.amount,
                category = it.category,
                type = it.type.name,
                iconResId = it.iconResId
            ) {

            }
        }
    }
}

@Composable
private fun ScheduledTransactionItem(
    id: Long,
    name: String,
    amount: String,
    category: String,
    type: String,
    iconResId: Int?,
    onClick: (Long) -> Unit
) {
    ExpensesHistoryItem(
        Modifier.padding(vertical = grid_x1),
        id,
        name,
        amount,
        category,
        type,
        iconResId ?: R.drawable.error_image,
        onClick
    )
}