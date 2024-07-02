package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.navigation.FEVerticalSlideUp
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGAnimatedTransition
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.features.SettingEditorScreen
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun TagSettingScreen(
    viewModel: TagSettingViewModel
) {

    val viewState by viewModel.viewState.collectAsState()

    TagListScreen(tags = viewState.tags) {
        viewModel.onToggleEditor(it)
    }

    SGAnimatedTransition(
        isVisible = viewState.isEditorMode,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        SettingEditorScreen(
            fields = viewState.tagEditorState?.fields ?: emptyList(),
            appBarTitle = stringResource(id = R.string.tag_setting_editor_app_bar_title_add_new),
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
    tags: List<Tag>,
    navigateTagEditor: (selectedTag: Tag?) -> Unit,
) {
    SGScaffold(
        modifier = Modifier
            .fillMaxWidth()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(title = stringResource(id = R.string.tag_setting_app_bar_title),
                actions = {
                    IconButton(onClick =  { navigateTagEditor(null) }) {
                        SGIcons.Add()
                    }
                }
            )
        }
    ) {
        FeColumnContainer {
            tags.forEach {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = grid_x2),
                    contentAlignment = Alignment.CenterStart
                ) {
                    FEBody2(text = it.name)
                }
            }
        }
    }
}