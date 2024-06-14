package com.charmflex.flexiexpensesmanager.features.home.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryList

@Composable
internal fun TransactionHistoryHomeScreen(
    transactionHistoryViewModel: TransactionHistoryViewModel
) {
    val viewState by transactionHistoryViewModel.viewState.collectAsState()
    TransactionHistoryList(transactionHistoryViewModel = transactionHistoryViewModel)
    if (viewState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}