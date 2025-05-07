package com.charmflex.flexiexpensesmanager.features.account.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.model.content.RectangleShape
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyTextFieldOutputFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.FEGeneralSelectionBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGModalBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SearchBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SearchItem
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_25
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_5
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountEditorScreen(
    viewModel: AccountEditorViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val selectedAccountGroup = viewState.selectedAccountGroup
    val title = when (selectedAccountGroup) {
        null -> "Account Group"
        else -> "Accounts in ${selectedAccountGroup.accountGroupName}"
    }
    val editorLabel = when (viewState.editorState) {
        is AccountEditorViewState.AccountEditorState -> "Account Name"
        else -> ""
    }
    val isEditorOpened = viewState.editorState != null
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarState by viewModel.snackBarState.collectAsState()
    val currency = (viewState.editorState as? AccountEditorViewState.AccountEditorState)?.currency
        ?: viewState.primaryCurrencyCode
    val visualTransformation =
        remember(currency) {
            currency.let {
                viewModel.getCurrencyVisualTransformer(currency)
            }
        }
    val bottomSheetState = viewState.bottomSheetState
    val modalBottomSheetState = rememberModalBottomSheetState()

    LaunchedEffect(key1 = snackBarState) {
        when (val state = snackBarState) {
            is SnackBarState.Success -> snackBarHostState.showSnackBarImmediately(state.message)
            is SnackBarState.Error -> snackBarHostState.showSnackBarImmediately(
                state.message ?: "Something went wrong"
            )

            else -> {}
        }
        viewModel.resetSnackBarState()
    }

    BackHandler {
        viewModel.back()
    }

    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(title = title)
        },
        screenName = "AccountEditorScreen"
    ) {
        if (isEditorOpened) {
            EditorScreen(
                editorLabel = editorLabel,
                viewState = viewState,
                updateAccountName = viewModel::updateAccountName,
                updateAmount = viewModel::updateInitialAmount,
                onFieldTap = {
                    viewModel.onTapField(it)
                },
                visualTransformation = visualTransformation
            ) {
                viewModel.addNewItem()
            }
        } else {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = grid_x2)
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(grid_x2))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .verticalScroll(scrollState)
                    ) {
                        if (selectedAccountGroup == null) {
                            viewState.accountGroups.forEachIndexed { index, it ->
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectAccountGroup(it)
                                            }
                                            .padding(grid_x2),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        FEBody1(
                                            modifier = Modifier.weight(1f),
                                            text = it.accountGroupName
                                        )
                                        SGIcons.NextArrow()
                                    }
                                    if (index != viewState.accountGroups.size - 1) HorizontalDivider()
                                }

                            }
                        } else {
                            selectedAccountGroup.accounts.forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(grid_x2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        FEBody1(
                                            modifier = Modifier.weight(1f),
                                            text = it.accountName
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.launchDeleteDialog(
                                                    it.accountId,
                                                    AccountEditorViewState.Type.ACCOUNT
                                                )
                                            }
                                        ) {
                                            SGIcons.Delete()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewState.selectedAccountGroup != null) {
                    SGLargePrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "ADD"
                    ) {
                        viewModel.toggleEditor(true)
                    }
                }
            }
        }
    }

    viewState.deleteDialogState?.let {
        SGActionDialog(
            title = "Warning",
            text = "You are going to delete this!",
            onDismissRequest = viewModel::closeDialog,
            primaryButtonText = "Confirm",
            secondaryButtonText = "Cancel"
        ) {
            viewModel.deleteItem(it.id, it.type)
            viewModel.closeDialog()
        }
    }

    bottomSheetState?.let {
        when (it) {
            is BottomSheetState.CurrencySelectionState -> {
                SearchBottomSheet(
                    sheetState = modalBottomSheetState,
                    onDismiss = { viewModel.resetBottomSheetState() },
                    searchFieldLabel = "Select currency",
                    items = it.currencyCodes.map {
                        object : SearchItem {
                            override val key: String
                                get() = it
                        }
                    },
                ) { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onBottomSheetItemSelected(item.key)
                            }
                            .padding(grid_x0_25),
                        shape = RectangleShape,
                        elevation = CardDefaults.cardElevation(grid_x1),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(grid_x2), contentAlignment = Alignment.Center) {
                            Text(text = item.key)
                        }
                    }
                }
            }
        }
    }

    SGSnackBar(
        snackBarHostState = snackBarHostState,
        snackBarType = if (snackBarState is SnackBarState.Error) SnackBarType.Error else SnackBarType.Success
    )
}

@Composable
private fun ColumnScope.EditorScreen(
    editorLabel: String,
    viewState: AccountEditorViewState,
    updateAccountName: (String) -> Unit,
    updateAmount: (String) -> Unit,
    visualTransformation: CurrencyVisualTransformation,
    onFieldTap: (TapFieldType) -> Unit,
    addNewItem: () -> Unit,
) {

    val outputCurrencyFormatter = remember { CurrencyTextFieldOutputFormatter() }
    SGTextField(
        modifier = Modifier.fillMaxWidth(),
        label = editorLabel,
        value = when (val vs = viewState.editorState) {
            is AccountEditorViewState.AccountEditorState -> vs.accountName
            null -> ""
        }
    ) {
        updateAccountName(it)
    }
    if (viewState.editorState is AccountEditorViewState.AccountEditorState) {
        SGTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(id = R.string.account_editor_currency_label),
            value = viewState.editorState.currency,
            readOnly = true,
            onClicked = { onFieldTap(TapFieldType.CurrencyField) }
        ) {

        }
        SGTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(id = R.string.account_editor_amount_label),
            value = viewState.editorState.amount,
            keyboardType = KeyboardType.Number,
            visualTransformation = visualTransformation,
            outputFormatter = { outputCurrencyFormatter.format(it) }
        ) {
            updateAmount(it)
        }
    }
    Spacer(modifier = Modifier.weight(1f))
    SGLargePrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = "ADD",
        enabled = true
    ) {
        addNewItem()
    }
}