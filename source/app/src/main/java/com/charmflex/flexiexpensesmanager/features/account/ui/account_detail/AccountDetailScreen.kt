package com.charmflex.flexiexpensesmanager.features.account.ui.account_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryList
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModelParent
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold

@Composable
internal fun AccountDetailScreen(
    accountTransactionHistoryViewModel: AccountTransactionHistoryViewModel,
    accountDetailViewModel: AccountDetailViewModel
) {
    val viewState by accountDetailViewModel.viewState.collectAsState()
    SGScaffold(
        topBar = {
            BasicTopBar(
                title = viewState.title
            )
        }
    ) {
        TransactionHistoryList(transactionHistoryViewModel = accountTransactionHistoryViewModel)
    }
}