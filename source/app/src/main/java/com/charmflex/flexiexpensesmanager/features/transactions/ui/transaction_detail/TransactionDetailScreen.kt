package com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val detail = viewState.detail

    SGScaffold(
        topBar = { BasicTopBar("Transaction Detail") },
        modifier = Modifier.padding(grid_x2)
    ) {
        if (detail != null) {
            FeColumnContainer {
                TransactionDetailItem(title = "Name", text = detail.transactionName)
                TransactionDetailItem(title = "Amount", text = detail.amountInCent.toString())
                TransactionDetailItem(title = "Type", text = detail.transactionTypeCode)
            }
        }
    }
}

@Composable
private fun TransactionDetailItem(
    title: String,
    text: String
) {
    Row {
        FEBody2(
            modifier = Modifier
                .weight(0.4f),
            text = title
        )
        FEBody2(
            modifier = Modifier
                .weight(0.6f),
            text = text
        )
    }
}