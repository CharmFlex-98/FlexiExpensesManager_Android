package com.charmflex.flexiexpensesmanager.features.home.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryList
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun TransactionHistoryHomeScreen(
    transactionHomeViewModel: TransactionHomeViewModel
) {
    val viewState by transactionHomeViewModel.viewState.collectAsState()
    TransactionHistoryList(
        modifier = Modifier.padding(horizontal = grid_x2),
        transactionHistoryViewModel = transactionHomeViewModel
    )
    if (viewState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}