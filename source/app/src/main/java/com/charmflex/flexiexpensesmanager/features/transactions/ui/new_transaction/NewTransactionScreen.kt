package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_AMOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_FROM_ACCOUNT
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading2
import com.charmflex.flexiexpensesmanager.ui_common.FEMetaData1
import com.charmflex.flexiexpensesmanager.ui_common.SGActionDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGAutoCompleteTextField
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGDatePicker
import com.charmflex.flexiexpensesmanager.ui_common.SGDialog
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGModalBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGSmallPrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGSmallSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.SupportingText
import com.charmflex.flexiexpensesmanager.ui_common.SupportingTextType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x20
import com.charmflex.flexiexpensesmanager.ui_common.grid_x4
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun TransactionEditorScreen(
    viewModel: TransactionEditorBaseViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val currencyExchangeViewState by viewModel.combinedCurrencyExchangeViewState.collectAsState()
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
    val showDialogCurrencyState = currencyExchangeViewState.showDialogState

    LaunchedEffect(key1 = Unit) {
        delay(500)
        initLoader = false
    }

    LaunchedEffect(Unit) {
        viewModel.onRefreshOptions()
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
        screenName = "NewTransactionScreen",
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
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
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
                    val suffixText: String? = when (feField.id) {
                        TRANSACTION_AMOUNT -> {
                            currencyExchangeViewState.transactionCurrencyViewState?.toCurrencyAmountFormatted
                        }

                        TRANSACTION_FROM_ACCOUNT -> {
                            currencyExchangeViewState.accountCurrencyViewState?.toCurrencyAmountFormatted
                        }

                        else -> null
                    }
                    val supportingText = when (feField.id) {
                        TRANSACTION_AMOUNT -> {
                            currencyExchangeViewState.transactionCurrencyViewState?.rate

                        }


                        TRANSACTION_FROM_ACCOUNT -> {
                            currencyExchangeViewState.accountCurrencyViewState?.rate
                        }

                        else -> null
                    }
                    val onSuffixTextClicked = {
                        when (feField.id) {
                            TRANSACTION_AMOUNT -> {
                                viewModel.onCurrencyViewTapped(currencyExchangeViewState.transactionCurrencyViewState)
                            }

                            TRANSACTION_FROM_ACCOUNT -> {
                                viewModel.onCurrencyViewTapped(currencyExchangeViewState.accountCurrencyViewState)
                            }

                            else -> {}
                        }
                    }
                    when (val type = feField.type) {
                        is FEField.FieldType.Callback -> {
                            SGTextField(
                                modifier = Modifier
                                    .padding(vertical = grid_x1)
                                    .fillMaxWidth(),
                                label = stringResource(id = feField.labelId),
                                value = feField.valueItem.value,
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
                                } else null,
                                suffixText = suffixText,
                                onSuffixTextClicked = onSuffixTextClicked,
                                supportingText = supportingText?.let {
                                    SupportingText(
                                        it,
                                        SupportingTextType.INFO
                                    )
                                }
                            )
                        }

                        else -> {
                            SGTextField(
                                modifier = Modifier
                                    .padding(vertical = grid_x1)
                                    .fillMaxWidth(),
                                label = stringResource(id = feField.labelId),
                                value = feField.valueItem.value,
                                hint = stringResource(feField.hintId),
                                enable = feField.isEnable,
                                visualTransformation = if (feField.type is FEField.FieldType.Currency) {
                                    currencyVisualTransformation
                                } else VisualTransformation.None,
                                keyboardType = if (type is FEField.FieldType.Number || type is FEField.FieldType.Currency) KeyboardType.Number else KeyboardType.Text,
                                outputFormatter = if (feField.type is FEField.FieldType.Currency) {
                                    { outputCurrencyFormatter.format(it) }
                                } else null,
                                suffixText = suffixText,
                                onSuffixTextClicked = onSuffixTextClicked,
                                supportingText = supportingText?.let {
                                    SupportingText(
                                        it,
                                        SupportingTextType.INFO
                                    )
                                }
                            ) { newValue ->
                                viewModel.onFieldValueChanged(
                                    feField,
                                    newValue,
                                    updateMonetary = feField.shouldResetMonetary()
                                )
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
                it.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN),
            )
            viewModel.onToggleCalendar(null)
        },
        date = viewState.calendarState?.targetField?.valueItem?.value?.toLocalDate(
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

    if (showDialogCurrencyState != null) {
        val currencyVisualTransformation2 = remember(showDialogCurrencyState.toCurrency) {
            viewModel.currencyVisualTransformationBuilder()
                .create(showDialogCurrencyState.toCurrency)
        }
        SGDialog(
            title = "Exchange Rate",
            subtitle = "",
            onDismissRequest = {}
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.End
            ) {
                SGTextField(
                    modifier = Modifier,
                    keyboardType = KeyboardType.Number,
                    value = showDialogCurrencyState.toCurrencyAmount,
                    label = "Amount",
                    hint = "Amount",
                    visualTransformation = currencyVisualTransformation2,
                    outputFormatter = { outputCurrencyFormatter.format(it) },
                    onValueChange = {
                        viewModel.onCurrencyExchangeAmountChanged(it, showDialogCurrencyState)
                    })
                SGTextField(
                    modifier = Modifier,
                    value = showDialogCurrencyState.rate,
                    label = "Rate",
                    hint = "Rate",
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        viewModel.onCurrencyExchangeRateChanged(it, showDialogCurrencyState)
                    })
                Spacer(modifier = Modifier.size(grid_x4))
                SGSmallPrimaryButton(text = "DONE") {
                    viewModel.onCurrencyViewClosed(showDialogCurrencyState)
                }
            }
        }
    }

    if (viewState.showBottomSheet) {
        SGModalBottomSheet(
            modifier = Modifier.padding(grid_x2),
            sheetState = bottomSheetState,
            onDismiss = { viewModel.toggleBottomSheet(null) }
        ) {
            Column {
                viewState.bottomSheetState?.takeIf { it.editable }?.let {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        SGSmallPrimaryButton(text = "EDIT") {
                            viewModel.onBottomSheetOptionsEdit(it)
                        }
                    }
                }

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
                        GeneralSelectionBottomSheet(
                            title = stringResource(id = R.string.currency_selection_bottomsheet_title),
                            items = viewState.currencyList,
                            name = { it }) {
                            viewModel.onCurrencySelected(it, bs.feField)
                            viewModel.toggleBottomSheet(null)
                        }
                    }

                    is TransactionEditorViewState.TagSelectionBottomSheetState -> {
                        GeneralSelectionBottomSheet(
                            title = stringResource(id = R.string.tag_selection_bottomsheet_title),
                            items = viewState.tagList,
                            name = { "#${it.name}" }) {
                            viewModel.onTagSelected(it, bs.feField)
                            viewModel.toggleBottomSheet(null)
                        }
                    }

                    is TransactionEditorViewState.PeriodSelectionBottomSheetState -> {
                        GeneralSelectionBottomSheet(
                            title = stringResource(id = R.string.scheduler_period_selection_bottomsheet_title),
                            items = viewModel.scheduledPeriodType,
                            name = { it.name }) { res ->
                            viewModel.onPeriodSelected(res, bs.feField)
                            viewModel.toggleBottomSheet(null)
                        }
                    }

                    is TransactionEditorViewState.UpdateTypeSelectionBottomSheetState -> {
                        GeneralSelectionBottomSheet(
                            title = stringResource(id = R.string.update_account_type_selection_bottomsheet_title),
                            items = viewModel.updateAccountType,
                            name = { it.name }) { selected ->
                            viewModel.onFieldValueChanged(bs.feField, selected.name)
                            viewModel.toggleBottomSheet(null)
                        }
                    }

                    else -> {}
                }
            }

        }
    }
    SGSnackBar(snackBarHostState = snackbarHostState, snackBarType = SnackBarType.Error)
}

@Composable
internal fun CategorySelectionBottomSheet(
    onSelected: (String, String) -> Unit,
    transactionCategories: TransactionCategories?,
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
                        onSelected(
                            it.categoryId.toString(),
                            it.categoryName
                        )
                    },
                    onToggleChildren = {
                        if (it.children.isEmpty().not()) {
                            if (it.level < list.size) {
                                list.removeRange(it.level, list.size)
                            }
                            list.add(it.children)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryList(
    categoryList: List<TransactionCategories.BasicCategoryNode>,
    onCategorySelected: (TransactionCategories.BasicCategoryNode) -> Unit,
    onToggleChildren: (TransactionCategories.BasicCategoryNode) -> Unit
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FEMetaData1(
                        modifier = Modifier.weight(1f),
                        text = it.categoryName
                    )
                    if (!it.isLeaf) {
                        IconButton(
                            modifier = Modifier.size(grid_x2),
                            onClick = { onToggleChildren(it) }) {
                            SGIcons.NextArrow()
                        }
                    }
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
    title: String,
    items: List<T>,
    name: (T) -> String,
    onSelectItem: (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        FEHeading2(text = title)
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