package com.charmflex.flexiexpensesmanager.features.account.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGMediumPrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately
import kotlinx.coroutines.flow.collectLatest

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
    val type = viewState.editorState.type
    val editorLabel = when (type) {
        AccountEditorViewState.Type.ACCOUNT -> "Account Name"
        AccountEditorViewState.Type.ACCOUNT_GROUP -> "Subgroup Name"
    }
    val isEditorOpened = viewState.editorState.isEditorOpened
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarState by viewModel.snackBarState.collectAsState()

    LaunchedEffect(key1 = snackBarState) {
        when (val state = snackBarState) {
            is SnackBarState.Success -> snackBarHostState.showSnackBarImmediately(state.message)
            is SnackBarState.Error -> snackBarHostState.showSnackBarImmediately(
                state.message ?: "Something went wrong"
            )

            else -> {}
        }
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
        }
    ) {
        if (isEditorOpened) {
            EditorScreen(
                editorLabel = editorLabel,
                viewState = viewState,
                updateEditorValue = viewModel::updateEditorValue
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
                            viewState.accountGroups.forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectAccountGroup(it)
                                        }
                                        .padding(grid_x2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row {
                                        FEBody1(
                                            modifier = Modifier.weight(1f),
                                            text = it.accountGroupName
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.launchDeleteDialog(
                                                    it.accountGroupId,
                                                    AccountEditorViewState.Type.ACCOUNT_GROUP
                                                )
                                            }
                                        ) {
                                            SGIcons.Delete()
                                        }
                                        SGIcons.NextArrow()
                                    }

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

                SGLargePrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "ADD"
                ) {
                    viewModel.toggleEditor(true)
                }
            }
        }
    }

    if (viewState.deleteDialogState != null) SGActionDialog(
        title = "Warning",
        text = "You are going to delete this!",
        onDismissRequest = viewModel::closeDialog,
        primaryButtonText = "Confirm",
        secondaryButtonText = "Cancel"
    ) {
        viewModel.deleteItem()
        viewModel.closeDialog()
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
    updateEditorValue: (String) -> Unit,
    addNewItem: () -> Unit,
) {
    SGTextField(
        modifier = Modifier.fillMaxWidth(),
        label = editorLabel,
        value = viewState.editorState.value
    ) {
        updateEditorValue(it)
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