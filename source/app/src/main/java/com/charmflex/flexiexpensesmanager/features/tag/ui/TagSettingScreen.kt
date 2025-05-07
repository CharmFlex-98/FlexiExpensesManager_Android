package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.ui_common.BasicColumnContainerItemList
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGAnimatedTransition
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.features.SettingEditorScreen
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun TagSettingScreen(
    viewModel: TagSettingViewModel
) {

    val viewState by viewModel.viewState.collectAsState()

    TagListScreen(viewModel, viewState.tags, viewState.dialogState)

    SGAnimatedTransition(
        isVisible = viewState.isEditorMode,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        SettingEditorScreen(
            fields = viewState.tagEditorState?.fields ?: emptyList(),
            appBarTitle = stringResource(id = R.string.tag_setting_editor_app_bar_title_add_new),
            screenName = "TagSettingScreen",
            onTextFieldChanged = { newValue, field ->
                viewModel.onUpdateFields(
                    field,
                    newValue
                )
            },
            onBack = viewModel::onBack
        ) {
            viewModel.addNewTag()
        }
    }
}

@Composable
private fun TagListScreen(
    viewModel: TagSettingViewModel,
    tags: List<Tag>,
    dialogState: TagSettingViewState.TagSettingDialogState?
) {

    SGScaffold(
        modifier = Modifier
            .fillMaxWidth()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(title = stringResource(id = R.string.tag_setting_app_bar_title),
                actions = {
                    IconButton(onClick =  { viewModel.onToggleEditor(null) }) {
                        SGIcons.Add()
                    }
                }
            )
        }
    ) {
        BasicColumnContainerItemList(
            items = tags,
            itemContent = {
                FEBody2(modifier = Modifier.weight(1f), text = "#${it.name}")
            },
            actionItems = {
                IconButton(onClick = { viewModel.onDeleteIconTap(it) }) {
                    SGIcons.Delete()
                }
            }
        )
    }

    dialogState?.let {
        when (it) {
            is TagSettingViewState.TagSettingDialogState.DeleteWarning -> {
                SGActionDialog(
                    title = stringResource(id = R.string.generic_warning),
                    text = stringResource(id = R.string.generic_delete_tag_warning_subtitle, it.tag.name),
                    onDismissRequest = { viewModel.onCloseDialog() },
                    primaryButtonText = stringResource(id = R.string.generic_delete)
                ) {
                    viewModel.onConfirmDelete(tag = it.tag)
                }
            }
            else -> {}
        }
    }
}