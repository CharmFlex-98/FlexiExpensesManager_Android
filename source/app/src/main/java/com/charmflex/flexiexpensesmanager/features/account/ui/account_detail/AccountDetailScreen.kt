package com.charmflex.flexiexpensesmanager.features.account.ui.account_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryList
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun AccountDetailScreen(
    accountDetailViewModel: AccountDetailViewModel
) {
    val dateFilter by accountDetailViewModel.dateFilter.collectAsState()
    val accountViewState by accountDetailViewModel.accountDetailViewState.collectAsState()
    SGScaffold(
        topBar = {
            BasicTopBar(
                title = accountViewState.title
            )
        },
        modifier = Modifier.padding(grid_x2),
        screenName = "AccountDetailScreen"
    ) {
        DateFilterBar(
            modifier = Modifier.fillMaxWidth(),
            currentDateFilter = dateFilter,
            onDateFilterChanged = {
                accountDetailViewModel.onDateFilterChanged(it)
            }
        )
        TransactionHistoryList(modifier = Modifier.fillMaxSize(), transactionHistoryViewModel = accountDetailViewModel)
    }
}