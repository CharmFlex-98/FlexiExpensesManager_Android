package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import android.app.Activity
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.SGDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGMediumPrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SettingScreen(
    viewModel: SettingViewModel,
    onRefresh: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()
    val localContext = LocalContext.current
    val snackbarState by viewModel.snackBarState
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarType = remember(snackbarState) {
        when (snackbarState) {
            is SnackBarState.Success -> SnackBarType.Success
            is SnackBarState.Error -> SnackBarType.Error
            else -> null
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.onDataClearedEvent.collectLatest {
            when (it) {
                OnDataCleared.Refresh -> onRefresh()
                OnDataCleared.Finish -> (localContext as? Activity)?.finish()
            }
        }
    }

    LaunchedEffect(key1 = snackbarState) {
        when (val state = snackbarState) {
            is SnackBarState.Error -> {
                snackbarHostState.showSnackBarImmediately(state.message ?: "Something went wrong")
                viewModel.refreshSnackBarState()
            }

            is SnackBarState.Success -> {
                snackbarHostState.showSnackBarImmediately(state.message)
                viewModel.refreshSnackBarState()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(grid_x2),
    ) {
        viewState.items.forEachIndexed { index, it ->
            if (index != 0) HorizontalDivider(color = Color.White, thickness = 0.5.dp)
            Box(
                modifier = Modifier
                    .padding(vertical = grid_x1)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(grid_x2))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable {
                        viewModel.onTap(it.action)
                    }
                    .padding(grid_x2)
            ) {
                FEHeading4(text = it.title, color = MaterialTheme.colorScheme.onTertiary)
            }
        }
    }

    viewState.dialogState?.let {
        SGDialog(
            title = stringResource(id = it.title),
            subtitle = stringResource(id = it.subtitle),
            onDismissRequest = viewModel::closeDialog
        ) {
            when (it) {
                is SettingDialogState.ResetDataDialogState -> {
                    ResetDataDialogContentSelection(
                        resetDataDialogState = it,
                        viewModel::toggleResetSelection
                    ) {
                        viewModel.onFactoryResetConfirmed()
                    }
                }
            }
        }
    }

    if (viewState.isLoading) Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    snackbarType?.let {
        SGSnackBar(snackBarHostState = snackbarHostState, snackBarType = it)
    }
}


@Composable
private fun ResetDataDialogContentSelection(
    resetDataDialogState: SettingDialogState.ResetDataDialogState,
    onToggleSelection: (SettingDialogState.ResetDataDialogState.ResetType) -> Unit,
    onFactoryResetConfirmed: () -> Unit
) {
    Column {
        SettingDialogState.ResetDataDialogState.ResetType.values().forEach {
            Row(
                modifier = Modifier.padding(grid_x1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = it.name,
                    textAlign = TextAlign.Start
                )
                RadioButton(selected = resetDataDialogState.selection == it, onClick = {
                    onToggleSelection(it)
                })
            }
        }
        Spacer(modifier = Modifier.height(grid_x1))
        SGLargePrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.generic_confirm)
        ) {
            onFactoryResetConfirmed()
        }
    }
}