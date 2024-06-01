package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun TagSettingScreen(
    viewModel: TagSettingViewModel
) {

    val viewState by viewModel.viewState.collectAsState()

    SGScaffold(
        modifier = Modifier.padding(grid_x2)
    ) {
        OutlinedTextField(value = viewState.tagName, onValueChange = viewModel::onUpdateTagValue)
        Spacer(modifier = Modifier.weight(1f))
        SGLargePrimaryButton(modifier = Modifier.fillMaxWidth(), text = "Add") {
            viewModel.addNewTag()
        }
    }
}