package com.charmflex.flexiexpensesmanager.ui_common.features

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyTextFieldOutputFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

internal const val SETTING_EDITOR_TAG = "SETTING_EDITOR_TAG"
internal const val SETTING_EDITOR_BUDGET_CATEGORY = "SETTING_EDITOR_BUDGET_CATEGORY"
internal const val SETTING_EDITOR_BUDGET_AMOUNT = "SETTING_EDITOR_BUDGET_AMOUNT"

// TODO: Considering creating a Base ViewModel to contain the logic for this
//  Or to create a composable state kit
@Composable
internal fun SettingEditorScreen(
    fields: List<FEField>,
    appBarTitle: String,
    screenName: String,
    currencyVisualTransformation: CurrencyVisualTransformation? = null,
    onTextFieldChanged: (String, FEField) -> Unit,
    onCallBackFieldTap: ((FEField) -> Unit)? = null,
    onClearField: ((FEField) -> Unit)? = null,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    BackHandler {
        onBack()
    }

    val outputCurrencyFormatter = remember { CurrencyTextFieldOutputFormatter() }


    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(title = appBarTitle)
        },
        screenName = screenName
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Top) {
            fields.forEach {
                when (it.type) {
//                    is FEField.FieldType.Text -> {
//                        SGTextField(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(grid_x1),
//                            label = stringResource(id = it.labelId),
//                            value = it.valueItem.value
//                        ) { newValue ->
//                            onTextFieldChanged(newValue, it)
//                        }
//                    }
//                    is FEField.FieldType.Currency -> {
//
//                    }
                    is FEField.FieldType.Callback -> {
                        SGTextField(
                            modifier = Modifier
                                .padding(vertical = grid_x1)
                                .fillMaxWidth(),
                            label = stringResource(id = it.labelId),
                            value = it.valueItem.value,
                            readOnly = true,
                            onValueChange = {},
                            onClicked = {
                                onCallBackFieldTap?.invoke(it)
                            },
                            trailingIcon = if (it.allowClear) {
                                {
                                    IconButton(onClick = {
                                        onClearField?.invoke(it)
                                    }) {
                                        SGIcons.Close()
                                    }
                                }
                            } else null
                        )
                    }
                    else -> {
                        SGTextField(
                            modifier = Modifier
                                .padding(vertical = grid_x1)
                                .fillMaxWidth(),
                            label = stringResource(id = it.labelId),
                            value = it.valueItem.value,
                            hint = stringResource(it.hintId),
                            enable = it.isEnable,
                            visualTransformation = if (it.type is FEField.FieldType.Currency && currencyVisualTransformation != null) {
                                currencyVisualTransformation
                            } else VisualTransformation.None,
                            keyboardType = if (it.type is FEField.FieldType.Number || it.type is FEField.FieldType.Currency) KeyboardType.Number else KeyboardType.Text,
                            outputFormatter = if (it.type is FEField.FieldType.Currency) {
                                { outputCurrencyFormatter.format(it) }
                            } else null
                        ) { newValue ->
                            onTextFieldChanged(newValue, it)
                        }
                    }
                }
            }
        }

        SGButtonGroupVertical {
            SGLargePrimaryButton(modifier = Modifier.fillMaxWidth(), text = stringResource(id = R.string.generic_confirm)) {
                onConfirm()
            }
            SGLargeSecondaryButton(modifier = Modifier.fillMaxWidth(), text = stringResource(id = R.string.generic_cancel)) {
                onBack()
            }
        }
    }
}