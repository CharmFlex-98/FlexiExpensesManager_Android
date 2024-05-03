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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

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
        AccountEditorViewState.EditorState.Type.ACCOUNT -> "Account Name"
        AccountEditorViewState.EditorState.Type.SUBGROUP -> "Subgroup Name"
    }
    val isEditorOpened = viewState.editorState.isEditorOpened
    val scrollState = rememberScrollState()

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