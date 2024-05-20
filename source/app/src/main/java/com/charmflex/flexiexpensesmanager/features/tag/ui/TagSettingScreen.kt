package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold

@Composable
internal fun TagSettingScreen(
    viewModel: TagSettingViewModel
) {

    val viewState by viewModel.viewState.collectAsState()

    SGScaffold {
        OutlinedTextField(value = viewState.tagName, onValueChange = viewModel::onUpdateTagValue)
        SGLargePrimaryButton(text = "Add") {
            viewModel.addNewTag()
        }
    }
}