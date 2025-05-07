package com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val detail = viewState.detail

    SGScaffold(
        topBar = {
            TransactionDetailTopBar(
                onDelete = viewModel::openDeleteWarningDialog,
                onEdit = viewModel::navigateToTransactionEdit,
                allowEdit = detail?.allowEdit ?: false
            )
        },
        modifier = Modifier.padding(grid_x2),
        screenName = "TransactionDetailScreen"
    ) {
        if (detail != null) {
            FeColumnContainer(
                modifier = Modifier.padding(vertical = grid_x2),
                backgroundColor = MaterialTheme.colorScheme.tertiary
            ) {
                TransactionDetailItem(title = "Name", text = detail.transactionName)
                TransactionDetailItem(title = "Amount", text = detail.formattedAmount)
                TransactionDetailItem(title = "Type", text = detail.transactionTypeCode)
            }
        }
    }

    viewState.dialogState?.let {
        BackHandler {

        }

        val title = when (it) {
            is TransactionDetailViewState.SuccessDialog -> it.title
            is TransactionDetailViewState.DeleteDialogState -> stringResource(id = R.string.generic_warning)
        }
        val subtitle = when (it) {
            is TransactionDetailViewState.SuccessDialog -> it.subtitle
            is TransactionDetailViewState.DeleteDialogState -> stringResource(id = R.string.generic_delete_warning_subtitle)
        }
        val positiveButtonText = when (it) {
            is TransactionDetailViewState.SuccessDialog -> stringResource(id = R.string.generic_back_to_home)
            is TransactionDetailViewState.DeleteDialogState -> stringResource(id = R.string.generic_yes)
        }
        val negativeButtonText = when (it) {
            is TransactionDetailViewState.SuccessDialog -> null
            is TransactionDetailViewState.DeleteDialogState -> stringResource(id = R.string.generic_cancel)
        }
        SGActionDialog(
            title = title,
            text = subtitle,
            onDismissRequest = { viewModel.onCloseDialog() },
            primaryButtonText = positiveButtonText,
            secondaryButtonText = negativeButtonText
        ) {
            when (it) {
                is TransactionDetailViewState.SuccessDialog -> viewModel.onBack()
                is TransactionDetailViewState.DeleteDialogState -> viewModel.deleteTransaction()
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
            text = title,
            color = MaterialTheme.colorScheme.onTertiary
        )
        FEBody2(
            modifier = Modifier
                .weight(0.6f),
            text = text,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Composable
private fun TransactionDetailTopBar(
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    allowEdit: Boolean
) {
    BasicTopBar(
        stringResource(id = R.string.transaction_detail_screen_title),
        actions = {
            Row {
                if (allowEdit) IconButton(onClick = onEdit) {
                    SGIcons.EditIcon()
                }
                IconButton(onClick = onDelete) {
                    SGIcons.Delete()
                }
            }
        }
    )
}