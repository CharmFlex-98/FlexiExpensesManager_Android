package com.charmflex.flexiexpensesmanager.ui_common.features

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

internal const val SETTING_EDITOR_ACCOUNT_NAME = "SETTING_EDITOR_ACCOUNT_NAME"
internal const val SETTING_EDITOR_ACCOUNT_INITIAL_AMOUNT = "SETTING_EDITOR_ACCOUNT_INITIAL_AMOUNT"

@Composable
internal fun SettingEditorScreen(
    fields: List<FEField>,
    appBarTitle: () -> String,
    onTextFieldChanged: (String, FEField) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(title = appBarTitle())
        }
    ) {
        fields.forEach {
            SGTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_x1),
                label = stringResource(id = it.labelId),
                value = it.valueItem.value
            ) { newValue ->
                onTextFieldChanged(newValue, it)
            }
        }

        Spacer(modifier = Modifier
            .fillMaxSize()
            .weight(1f))

        SGButtonGroupVertical {
            SGLargePrimaryButton(text = stringResource(id = R.string.generic_confirm)) {
                onConfirm()
            }
            SGLargeSecondaryButton(text = stringResource(id = R.string.generic_cancel)) {
                onBack()
            }
        }
    }
}