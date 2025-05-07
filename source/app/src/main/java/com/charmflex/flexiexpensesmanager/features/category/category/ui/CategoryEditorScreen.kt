package com.charmflex.flexiexpensesmanager.features.category.category.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately


@Composable
internal fun CategoryEditorScreen(viewModel: CategoryEditorViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val currentNode = viewState.currentNode
    val title = when (val n = currentNode) {
        null -> "Category"
        else -> "Add Subcategory for ${n.categoryName}"
    }
    val items = when (val n = currentNode) {
        null -> viewState.categoryChain.categoryTree.items
        else -> n.children
    }
    val isEditorOpened = viewState.editorState.isOpened
    val scrollState = rememberScrollState()
    val snackBarState by viewModel.snackBarState.collectAsState()
    val snackBarHostState = remember {
        SnackbarHostState()
    }

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
        },
        screenName = "CategoryEditorScreen"
    ) {
        if (isEditorOpened) {
            SGTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "New category",
                value = viewState.editorState.value
            ) {
                viewModel.updateEditorValue(it)
            }
            Spacer(modifier = Modifier.weight(1f))
            SGLargePrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "ADD",
                enabled = true
            ) {
                viewModel.addNewCategory()
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
                        items.forEach {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onClickItem(it)
                                    }
                                    .padding(grid_x2),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FEBody1(
                                        modifier = Modifier.weight(1f),
                                        text = it.categoryName
                                    )
                                    IconButton(onClick = {
                                        viewModel.launchDeleteDialog(it.categoryId)
                                    }) {
                                        SGIcons.Delete()
                                    }
                                    if (it.allowSubCategory) Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_next),
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }
                }

                if (viewState.dialogState != null) SGActionDialog(
                    title = "Warning",
                    text = "Are you sure you want to DELETE?",
                    onDismissRequest = viewModel::closeDeleteDialog,
                    primaryButtonText = "Confirm",
                    secondaryButtonText = "Cancel"
                ) {
                    viewModel.deleteCategory()
                    viewModel.closeDeleteDialog()
                }

                SGLargePrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "ADD"
                ) {
                    viewModel.openEditor()
                }
            }
        }
    }

    SGSnackBar(
        snackBarHostState = snackBarHostState,
        snackBarType = if (snackBarState is SnackBarState.Error) SnackBarType.Error else SnackBarType.Success
    )
}