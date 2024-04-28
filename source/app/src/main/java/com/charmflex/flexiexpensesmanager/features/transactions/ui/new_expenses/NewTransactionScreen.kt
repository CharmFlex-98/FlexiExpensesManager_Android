package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDateTime
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGAutoCompleteTextField
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGDatePicker
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewExpensesScreen(
    viewModel: NewTransactionViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val currentTransactionType by viewModel.currentTransactionType.collectAsState()
    val datePickerState = UseCaseState()
    var showCalendar = viewState.calendarState.isVisible
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarState by viewModel.snackBarState
    val genericErrorMessage = stringResource(id = R.string.generic_error)

    LaunchedEffect(snackBarState) {
        when (val state = snackBarState) {
            is SnackBarState.Error -> {
                snackbarHostState.showSnackBarImmediately(state.message ?: genericErrorMessage)
                viewModel.resetErrorState()
            }
            else -> {}
        }
    }

    SGScaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Create New Expenses", style = TextStyle(fontSize = 20.sp)
                )
            }, navigationIcon = {
                IconButton(onClick = viewModel::onBack) {
                    SGIcons.ArrowBack()
                }
            })
        },
        isLoading = viewState.isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(grid_x2)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    viewModel.transactionType.forEach {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FEBody3(text = it.name)
                                RadioButton(
                                    selected = currentTransactionType == it,
                                    onClick = {
                                        viewModel.initContent(it)
                                    }
                                )
                            }
                        }
                    }
                }
                viewState.fields.forEach { feField ->
                    when (val type = feField.type) {
                        is FEField.FieldType.SingleItemSelection -> {
                            SGAutoCompleteTextField(
                                modifier = Modifier
                                    .padding(vertical = grid_x1)
                                    .fillMaxWidth(),
                                label = stringResource(id = feField.labelId),
                                value = feField.value,
                                suggestions = type.options.map { it.title }
                            ) { newValue ->
                                viewModel.onFieldValueChanged(feField, newValue)
                            }
                        }

                        is FEField.FieldType.Date -> {
                            SGTextField(
                                modifier = Modifier
                                    .padding(vertical = grid_x1)
                                    .fillMaxWidth(),
                                label = stringResource(id = feField.labelId),
                                value = feField.value,
                                readOnly = true,
                                onValueChange = {},
                                onClicked = {
                                    viewModel.onToggleCalendar(feField)
                                }
                            )
                        }

                        else -> {
                            SGTextField(
                                modifier = Modifier
                                    .padding(vertical = grid_x1)
                                    .fillMaxWidth(),
                                label = stringResource(id = feField.labelId),
                                value = feField.value,
                                hint = stringResource(feField.hintId),
                                enable = feField.isEnable,
                                keyboardType = if (type is FEField.FieldType.Number) KeyboardType.Number else KeyboardType.Text
                            ) { newValue ->
                                viewModel.onFieldValueChanged(feField, newValue)
                            }
                        }
                    }
                }
            }
            SGButtonGroupVertical {
                SGLargePrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.new_expenses_confirm_button),
                    enabled = viewState.allowProceed
                ) {
                    viewModel.onConfirmed()
                }
                SGLargeSecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.new_expenses_cancel_button)
                ) {
                    viewModel.onBack()
                }
            }
        }
    }

    SGDatePicker(
        useCaseState = datePickerState,
        onDismiss = { viewModel.onToggleCalendar(null) },
        onConfirm = {
            viewModel.onFieldValueChanged(
                viewState.calendarState.targetField,
                it.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
            )
            viewModel.onToggleCalendar(null)
        },
        date = viewState.calendarState.targetField?.value?.toLocalDateTime(DATE_ONLY_DEFAULT_PATTERN),
        isVisible = showCalendar,
        boundary = LocalDate.now().minusYears(10)..LocalDate.now()
    )

    if (viewState.success) {
        SGActionDialog(
            title = stringResource(id = R.string.new_expenses_create_success_dialog_title),
            text = stringResource(id = R.string.new_expenses_create_success_dialog_subtitle),
            onDismissRequest = { },
            primaryButtonText = stringResource(id = R.string.generic_back_to_home)
        ) {
            viewModel.onBack()
        }
    }

    SGSnackBar(snackBarHostState = snackbarHostState, snackBarType = SnackBarType.Error)
}