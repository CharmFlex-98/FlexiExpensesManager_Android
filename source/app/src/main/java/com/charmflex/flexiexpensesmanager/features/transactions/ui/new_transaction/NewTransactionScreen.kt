package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyTextFieldOutputFormatter
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.currency.usecases.CurrencyRate
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading2
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGAutoCompleteTextField
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGDatePicker
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGModalBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x20
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import kotlinx.coroutines.delay
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionEditorScreen(
    viewModel: TransactionEditorBaseViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val currentTransactionType by viewModel.currentTransactionType.collectAsState()
    val datePickerState = UseCaseState()
    val showCalendar = viewState.calendarState != null
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarState by viewModel.snackBarState
    val genericErrorMessage = stringResource(id = R.string.generic_error)
    val bottomSheetState = rememberModalBottomSheetState()
    val currencyVisualTransformation = remember(viewState.currencyCode) {
        viewModel.currencyVisualTransformationBuilder().create(viewState.currencyCode)
    }
    val outputCurrencyFormatter = remember { CurrencyTextFieldOutputFormatter() }
    val type = viewModel.getType()
    val title = when (type) {
        TransactionRecordableType.NEW_TRANSACTION -> stringResource(id = R.string.new_transaction_app_bar_title)
        TransactionRecordableType.EDIT_TRANSACTION -> stringResource(id = R.string.edit_transaction_app_bar_title)
        TransactionRecordableType.NEW_SCHEDULER -> stringResource(id = R.string.new_scheduler_app_bar_title)
        TransactionRecordableType.EDIT_SCHEDULER -> stringResource(id = R.string.edit_scheduler_app_bar_title)
    }
    val actionTitle = when (type) {
        TransactionRecordableType.NEW_TRANSACTION, TransactionRecordableType.NEW_SCHEDULER -> stringResource(
            id = R.string.general_congratz
        )

        else -> stringResource(id = R.string.generic_success)
    }
    val actionSubtitle = when (type) {
        TransactionRecordableType.NEW_TRANSACTION -> stringResource(id = R.string.create_new_transaction_success_dialog_subtitle)
        TransactionRecordableType.EDIT_TRANSACTION -> stringResource(id = R.string.edit_new_transaction_success_dialog_subtitle)
        TransactionRecordableType.NEW_SCHEDULER -> stringResource(id = R.string.create_new_scheduler_success_dialog_subtitle)
        TransactionRecordableType.EDIT_SCHEDULER -> stringResource(id = R.string.edit_new_scheduler_success_dialog_subtitle)
    }
    var initLoader by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        delay(500)
        initLoader = false
    }

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
                    text = title, style = TextStyle(fontSize = 20.sp)
                )
            }, navigationIcon = {
                IconButton(
                    onClick = { viewModel.onBack() }
                ) {
                    SGIcons.ArrowBack()
                }
            }
            )
        },
        isLoading = viewState.isLoading || initLoader
    ) {
        if (initLoader.not()) Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(grid_x2)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                                        viewModel.onTransactionTypeChanged(it)
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
                                value = feField.value.value,
                                suggestions = type.options.map { it.title }
                            ) { newValue ->
                                viewModel.onFieldValueChanged(feField, newValue)
                            }
                        }

                        is FEField.FieldType.Callback -> {
                            SGTextField(
                                modifier = Modifier
                                    .padding(vertical = grid_x1)
                                    .fillMaxWidth(),
                                label = stringResource(id = feField.labelId),
                                value = feField.value.value,
                                readOnly = true,
                                onValueChange = {},
                                onClicked = {
                                    viewModel.onCallbackFieldTap(feField)
                                },
                                trailingIcon = if (feField.allowClear) {
                                    {
                                        IconButton(onClick = {
                                            viewModel.onClearField(feField)
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
                                label = stringResource(id = feField.labelId),
                                value = feField.value.value,
                                hint = stringResource(feField.hintId),
                                enable = feField.isEnable,
                                visualTransformation = if (feField.type is FEField.FieldType.Currency) {
                                    currencyVisualTransformation
                                } else VisualTransformation.None,
                                keyboardType = if (type is FEField.FieldType.Number || type is FEField.FieldType.Currency) KeyboardType.Number else KeyboardType.Text,
                                outputFormatter = if (feField.type is FEField.FieldType.Currency) {
                                    { outputCurrencyFormatter.format(it) }
                                } else null
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
                    enabled = viewModel.allowProceed()
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
                viewState.calendarState?.targetField,
                it.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
            )
            viewModel.onToggleCalendar(null)
        },
        date = viewState.calendarState?.targetField?.value?.value?.toLocalDate(
            DATE_ONLY_DEFAULT_PATTERN
        ),
        isVisible = showCalendar,
        boundary = viewModel.calendarSelectionRange()
    )

    if (viewState.success) {
        SGActionDialog(
            title = actionTitle,
            text = actionSubtitle,
            onDismissRequest = { },
            primaryButtonText = stringResource(id = R.string.generic_back_to_home)
        ) {
            viewModel.onBack()
        }
    }

    if (viewState.showBottomSheet) {
        SGModalBottomSheet(
            modifier = Modifier.padding(grid_x2),
            sheetState = bottomSheetState,
            onDismiss = { viewModel.toggleBottomSheet(null) }
        ) {
            when (val bs = viewState.bottomSheetState) {
                is TransactionEditorViewState.CategorySelectionBottomSheetState -> {
                    CategorySelectionBottomSheet(
                        onSelected = { id, name ->
                            viewModel.onCategorySelected(id, name, bs.feField)
                            viewModel.toggleBottomSheet(null)
                        },
                        transactionCategories = viewState.transactionCategories
                    )
                }

                is TransactionEditorViewState.AccountSelectionBottomSheetState -> {
                    AccountSelectionBottomSheet(accountGroups = viewState.accountGroups) {
                        viewModel.onSelectAccount(it, bs.feField)
                        viewModel.toggleBottomSheet(null)
                    }
                }

                is TransactionEditorViewState.CurrencySelectionBottomSheetState -> {
                    GeneralSelectionBottomSheet(items = viewState.currencyList, name = { it.name }) {
                        viewModel.onCurrencySelected(it, bs.feField)
                        viewModel.toggleBottomSheet(null)
                    }
                }

                is TransactionEditorViewState.TagSelectionBottomSheetState -> {
                    GeneralSelectionBottomSheet(items = viewState.tagList, name = { it.name }) {
                        viewModel.onTagSelected(it, bs.feField)
                        viewModel.toggleBottomSheet(null)
                    }
                }

                is TransactionEditorViewState.PeriodSelectionBottomSheetState -> {
                    GeneralSelectionBottomSheet(items = viewModel.scheduledPeriodType, name = { it.name }) { res ->
                        viewModel.onPeriodSelected(res, bs.feField)
                        viewModel.toggleBottomSheet(null)
                    }
                }

                else -> {}
            }
        }
    }
    SGSnackBar(snackBarHostState = snackbarHostState, snackBarType = SnackBarType.Error)
}

@Composable
private fun CategorySelectionBottomSheet(
    onSelected: (String, String) -> Unit,
    transactionCategories: TransactionCategories?
) {
    val list = remember {
        mutableStateListOf(transactionCategories?.items)
    }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(list.size) {
        horizontalScrollState.animateScrollBy(500f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        FEHeading2(text = "Category")
        Row(
            modifier = Modifier
                .padding(top = grid_x2)
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
        ) {
            list.forEach {
                CategoryList(
                    categoryList = it ?: listOf(),
                    onCategorySelected = {
                        val children = it.childNodes
                        if (children.isEmpty()) onSelected(
                            it.categoryId.toString(),
                            it.categoryName
                        )
                        else {
                            if (it.level < list.size) {
                                list.removeRange(it.level, list.size)
                            }
                            list.add(it.childNodes)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryList(
    categoryList: List<TransactionCategories.Node>,
    onCategorySelected: (TransactionCategories.Node) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        categoryList.forEach {
            Box(
                modifier = Modifier
                    .width(grid_x20)
                    .border(width = 0.5.dp, color = Color.Black, shape = RectangleShape)
                    .clickable {
                        onCategorySelected(it)
                    }
                    .padding(grid_x2)
            ) {
                Row {
                    FEBody2(
                        modifier = Modifier.weight(1f),
                        text = it.categoryName
                    )
                    if (!it.isLeaf) SGIcons.NextArrow()
                }
            }
        }
    }
}

@Composable
private fun AccountSelectionBottomSheet(
    accountGroups: List<AccountGroup>,
    onSelectAccount: (AccountGroup.Account) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedGroup by remember {
        mutableStateOf<AccountGroup?>(null)
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        FEHeading2(text = selectedGroup?.let { it.accountGroupName } ?: "Account")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState)
        ) {

            val selectedGroupAccounts = selectedGroup?.accounts
            if (selectedGroupAccounts != null) {
                selectedGroupAccounts.forEach {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectAccount(it)
                            }
                            .padding(grid_x1),
                        contentAlignment = Alignment.Center
                    ) {
                        FEBody2(text = it.accountName)
                    }
                }
            } else {
                accountGroups.forEach {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGroup = it
                            }
                            .padding(grid_x1),
                        contentAlignment = Alignment.Center
                    ) {
                        FEBody2(text = it.accountGroupName)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> GeneralSelectionBottomSheet(
    items: List<T>,
    name: (T) -> String,
    onSelectItem: (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        FEHeading2(text = "Select Tag")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
        ) {
            items.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectItem(it)
                        }
                        .padding(grid_x2),
                    contentAlignment = Alignment.Center
                ) {
                    FEBody2(text = name(it))
                }
            }
        }
    }
}