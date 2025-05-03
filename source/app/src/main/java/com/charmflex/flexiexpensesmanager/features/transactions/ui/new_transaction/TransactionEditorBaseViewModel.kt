package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.RateExchangeManager
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.service.CurrencyService
import com.charmflex.flexiexpensesmanager.features.transactions.provider.PRIMARY_CURRENCY_RATE
import com.charmflex.flexiexpensesmanager.features.transactions.provider.PRIMARY_TRANSACTION_AMOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_AMOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_CATEGORY
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_CURRENCY
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_DATE
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_FROM_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_NAME
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_SCHEDULER_PERIOD
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_TAG
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_TO_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_UPDATE_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_UPDATE_ACCOUNT_TYPE
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

internal abstract class TransactionEditorBaseViewModel(
    private val contentProvider: TransactionEditorContentProvider,
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator,
    private val transactionCategoryRepository: TransactionCategoryRepository,
    private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
    private val tagRepository: TagRepository,
    private val currencyService: CurrencyService,
    private val currencyFormatter: CurrencyFormatter,
    private val rateExchangeManager: RateExchangeManager,
    private val userCurrencyRepository: UserCurrencyRepository,
    private val dataId: Long?
) : ViewModel(), TransactionRecordable {
    private val _viewState = MutableStateFlow(TransactionEditorViewState())
    val viewState = _viewState.asStateFlow()
    open val transactionType =
        TransactionType.entries.toList()
    private val _currentTransactionType = MutableStateFlow(TransactionType.EXPENSES)
    val currentTransactionType = _currentTransactionType.asStateFlow()
    val snackBarState = mutableStateOf<SnackBarState>(SnackBarState.None)

    val scheduledPeriodType = SchedulerPeriod.entries.filter { it != SchedulerPeriod.UNKNOWN }
    val updateAccountType = UpdateAccountType.entries

    private val _resetMonetary: MutableSharedFlow<Unit> = MutableSharedFlow(extraBufferCapacity = 1)

    private val _currencyExchangeViewState: MutableSharedFlow<CurrencyExchangeViewState> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val currencyExchangeViewStateFromFields: Flow<CurrencyExchangeViewState> =
        currencyViewStateFromFieldsFlow()
    val combinedCurrencyExchangeViewState: StateFlow<CurrencyExchangeViewState> =
        merge(_currencyExchangeViewState, currencyExchangeViewStateFromFields)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                CurrencyExchangeViewState.empty()
            )
    private lateinit var _primaryCurrency: String


    // Must be called by child
    fun initialise() {
        onUpdateUserCurrencyOptions()
        observeTransactionTagOptions()
        observeAccountsUpdate()
//        observeCurrencyRate()
        updatePrimaryCurrency()
        if (dataId != null) {
            loadData(dataId)
        } else {
            onTransactionTypeChanged(_currentTransactionType.value)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun currencyViewStateFromFieldsFlow(): Flow<CurrencyExchangeViewState> {
        return _resetMonetary.transformLatest { it ->
            if (_currentTransactionType.value == TransactionType.EXPENSES) {
                expensesFieldsFlow(_viewState.value)
            } else if (_currentTransactionType.value == TransactionType.TRANSFER) {
                transferFieldsFlow(_viewState.value)
            } else if (_currentTransactionType.value == TransactionType.INCOME) {
                incomeFieldsFlow(_viewState.value)
            }
        }
    }

    private suspend fun FlowCollector<CurrencyExchangeViewState>.expensesFieldsFlow(it: TransactionEditorViewState) {
        val accountField = it.fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }
        val account = accountField?.let {
            if (it.valueItem.id.isBlank()) return@let null
            accountRepository.getAccountById(it.valueItem.id.toInt())
        }
        val transactionCurrencyField = it.fields.firstOrNull { it.id == TRANSACTION_CURRENCY }
        val primaryCurrency = _primaryCurrency

        val transactionAmountField = it.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }

        // Common check
        if (transactionAmountField?.valueItem?.value.isNullOrBlank() || transactionCurrencyField?.valueItem?.value.isNullOrBlank()) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        // Transaction to Primary
        val transactionCurrency = transactionCurrencyField!!.valueItem.value
        val primaryRate =
            currencyService.getCurrencyRate(primaryCurrency, transactionCurrency)?.rate
        if (primaryRate == null) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        val primaryCurrencyViewState =
            if (transactionCurrency != primaryCurrency) CurrencyViewState(
                type = CurrencyViewState.Type.TRANSACTION,
                transactionCurrency,
                primaryCurrency,
                transactionAmountField!!.valueItem.value,
                rateExchangeManager.convertTo(
                    transactionAmountField.valueItem.value.toLong(),
                    primaryCurrency,
                    transactionCurrencyField.valueItem.value,
                    primaryRate
                ),
                currencyFormatter.formatTo(
                    transactionAmountField.valueItem.value.toLong(),
                    primaryCurrency,
                    transactionCurrencyField.valueItem.value,
                    primaryRate
                ),
                primaryRate.toString()
            ) else null

        // Transaction to Account
        if (account == null) {
            emit(CurrencyExchangeViewState(null, primaryCurrencyViewState))
            return
        }
        val accountRate =
            currencyService.getCurrencyRate(account.currency, transactionCurrency)?.rate
        if (accountRate == null) {
            emit(CurrencyExchangeViewState(null, primaryCurrencyViewState))
            return
        }


        val accountCurrencyViewState =
            if (transactionCurrency != account.currency) CurrencyViewState(
                type = CurrencyViewState.Type.ACCOUNT,
                transactionCurrency,
                account.currency,
                transactionAmountField!!.valueItem.value,
                rateExchangeManager.convertTo(
                    transactionAmountField.valueItem.value.toLong(),
                    account.currency,
                    transactionCurrency,
                    accountRate
                ),
                currencyFormatter.formatTo(
                    transactionAmountField.valueItem.value.toLong(),
                    account.currency,
                    transactionCurrency,
                    accountRate
                ),
                accountRate.toString()
            ) else null

        emit(CurrencyExchangeViewState(accountCurrencyViewState, primaryCurrencyViewState))
    }

    private suspend fun FlowCollector<CurrencyExchangeViewState>.transferFieldsFlow(it: TransactionEditorViewState) {
        val fromAccountField = it.fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }
        val fromAccount = fromAccountField?.let {
            if (it.valueItem.id.isBlank()) return@let null
            accountRepository.getAccountById(it.valueItem.id.toInt())
        }
        val toAccountField = it.fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }
        val toAccount = toAccountField?.let {
            if (it.valueItem.id.isBlank()) return@let null
            accountRepository.getAccountById(it.valueItem.id.toInt())
        }
        val transferAmount = it.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value

        val fromAccountCurrency = fromAccount?.currency
        val toAccountCurrency = toAccount?.currency

        // Common check
        if (transferAmount == null) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        if (fromAccountCurrency == null || toAccountCurrency == null) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        if (transferAmount.isBlank()) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        if (fromAccountCurrency == toAccountCurrency) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        val rate = currencyService.getCurrencyRate(toAccountCurrency, fromAccountCurrency)?.rate
        if (rate == null) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        val convertedAmount = rateExchangeManager.convertTo(
            transferAmount.toLong(),
            toAccountCurrency,
            fromAccountCurrency,
            rate.toFloat()
        )
        val convertedAmountFormatted = currencyFormatter.formatTo(
            transferAmount.toLong(),
            toAccountCurrency,
            fromAccountCurrency,
            rate.toFloat()
        )
        emit(
            CurrencyExchangeViewState(
                transactionCurrencyViewState = CurrencyViewState(
                    type = CurrencyViewState.Type.TRANSACTION,
                    fromCurrency = fromAccountCurrency,
                    toCurrency = toAccountCurrency,
                    fromCurrencyAmount = transferAmount,
                    toCurrencyAmount = convertedAmount,
                    toCurrencyAmountFormatted = convertedAmountFormatted,
                    rate = rate.toString()
                )
            )
        )
    }

    private suspend fun FlowCollector<CurrencyExchangeViewState>.incomeFieldsFlow(it: TransactionEditorViewState) {
        val toAccountField = it.fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }
        val toAccount = toAccountField?.let {
            if (it.valueItem.id.isBlank()) return@let null
            accountRepository.getAccountById(it.valueItem.id.toInt())
        }
        val incomeAmount = it.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value

        val toAccountCurrency = toAccount?.currency

        // Common check
        if (incomeAmount.isNullOrBlank() || toAccountCurrency.isNullOrBlank()) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        val rate = currencyService.getCurrencyRate(
            _primaryCurrency,
            toAccountCurrency
        )?.rate
        if (rate == null) {
            emit(CurrencyExchangeViewState.empty())
            return
        }

        val convertedAmount = rateExchangeManager.convertTo(
            incomeAmount.toLong(),
            _primaryCurrency,
            toAccountCurrency,
            rate.toFloat()
        )
        val convertedAmountFormatted = currencyFormatter.formatTo(
            incomeAmount.toLong(),
            _primaryCurrency,
            toAccountCurrency,
            rate.toFloat()
        )
        emit(
            CurrencyExchangeViewState(
                transactionCurrencyViewState = CurrencyViewState(
                    type = CurrencyViewState.Type.TRANSACTION,
                    fromCurrency = toAccountCurrency,
                    toCurrency = _primaryCurrency,
                    fromCurrencyAmount = incomeAmount,
                    toCurrencyAmount = convertedAmount,
                    toCurrencyAmountFormatted = convertedAmountFormatted,
                    rate = rate.toString()
                )
            )
        )
    }

    fun onCurrencyViewTapped(state: CurrencyViewState?) {
        onCurrencyViewToggle(true, state)
    }

    fun onCurrencyViewClosed(state: CurrencyViewState?) {
        onCurrencyViewToggle(false, state)
    }

    fun onCurrencyExchangeRateChanged(newRate: String, currencyViewState: CurrencyViewState) {
        val transactionAmount =
            _viewState.value.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        if (transactionAmount.isNullOrBlank()) return

        val newCurrencyViewState: CurrencyViewState
        if (newRate.isBlank()) {
            newCurrencyViewState = currencyViewState.copy(toCurrencyAmountFormatted = "", rate = "")
        } else {
            val newAmount = rateExchangeManager.convertTo(
                transactionAmount.toLong(),
                currencyViewState.toCurrency,
                currencyViewState.fromCurrency,
                rate = newRate.toFloat()
            )
            val newAmountFormatted = currencyFormatter.formatTo(
                transactionAmount.toLong(),
                currencyViewState.toCurrency,
                currencyViewState.fromCurrency,
                rate = newRate.toFloat()
            )
            newCurrencyViewState = currencyViewState.copy(
                toCurrencyAmount = newAmount,
                toCurrencyAmountFormatted = newAmountFormatted,
                rate = newRate
            )
        }


        when (currencyViewState.type) {
            CurrencyViewState.Type.ACCOUNT -> {
                val res =
                    combinedCurrencyExchangeViewState.value.copy(accountCurrencyViewState = newCurrencyViewState)
                _currencyExchangeViewState.tryEmit(res)
            }

            CurrencyViewState.Type.TRANSACTION -> {
                val res =
                    combinedCurrencyExchangeViewState.value.copy(transactionCurrencyViewState = newCurrencyViewState)
                _currencyExchangeViewState.tryEmit(res)
            }
        }
    }

    fun onCurrencyExchangeAmountChanged(newAmount: String, currencyViewState: CurrencyViewState) {
        val transactionAmount =
            _viewState.value.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        if (transactionAmount.isNullOrBlank()) return

        val newCurrencyViewState: CurrencyViewState
        if (newAmount.isBlank()) {
            newCurrencyViewState = currencyViewState.copy(
                toCurrencyAmount = "",
                toCurrencyAmountFormatted = "",
                rate = ""
            )
        } else {
            val newRate = newAmount.toFloat() / transactionAmount.toFloat()
            val toCurrencyAmountFormatted =
                currencyFormatter.format(newAmount.toLong(), currencyViewState.toCurrency)
            newCurrencyViewState = currencyViewState.copy(
                toCurrencyAmount = newAmount,
                toCurrencyAmountFormatted = toCurrencyAmountFormatted,
                rate = newRate.toString()
            )
        }

        when (currencyViewState.type) {
            CurrencyViewState.Type.ACCOUNT -> {
                val res =
                    combinedCurrencyExchangeViewState.value.copy(accountCurrencyViewState = newCurrencyViewState)
                _currencyExchangeViewState.tryEmit(res)
            }

            CurrencyViewState.Type.TRANSACTION -> {
                val res =
                    combinedCurrencyExchangeViewState.value.copy(transactionCurrencyViewState = newCurrencyViewState)
                _currencyExchangeViewState.tryEmit(res)
            }
        }
    }

    private fun onCurrencyViewToggle(show: Boolean, state: CurrencyViewState?) {
        if (state == null) return
        val newState = state.copy(showDialog = show)
        when (state.type) {
            CurrencyViewState.Type.ACCOUNT -> {
                val res =
                    combinedCurrencyExchangeViewState.value.copy(accountCurrencyViewState = newState)
                _currencyExchangeViewState.tryEmit(res)
            }

            CurrencyViewState.Type.TRANSACTION -> {
                val res =
                    combinedCurrencyExchangeViewState.value.copy(transactionCurrencyViewState = newState)
                _currencyExchangeViewState.tryEmit(res)
            }
        }
    }

    private fun onUpdateUserCurrencyOptions() {
        viewModelScope.launch {
            val all = currencyService.getAllCurrencies()
            _viewState.update {
                it.copy(
                    currencyList = all
                )
            }
        }
    }

    private fun observeTransactionTagOptions() {
        viewModelScope.launch {
            tagRepository.getAllTags().collectLatest { tagList ->
                _viewState.update {
                    it.copy(
                        tagList = tagList
                    )
                }
            }
        }
    }

    private fun observeAccountsUpdate() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collectLatest { accGroup ->
                _viewState.update {
                    it.copy(
                        accountGroups = accGroup
                    )
                }
            }
        }
    }

    private fun updatePrimaryCurrency() {
        viewModelScope.launch {
            _primaryCurrency = userCurrencyRepository.getPrimaryCurrency()
        }
    }

    fun onConfirmed() {
        viewModelScope.launch {
            when (_currentTransactionType.value) {
                TransactionType.EXPENSES -> onSubmitExpenses()
                TransactionType.INCOME -> onSubmitIncome()
                TransactionType.TRANSFER -> onSubmitTransfer()
                TransactionType.UPDATE_ACCOUNT -> onSubmitUpdateAccount()
            }
        }
    }

    private fun loadData(id: Long) {
        viewModelScope.launch {
            toggleLoader(true)
            val transaction = loadTransaction(id) ?: return@launch
            val type = TransactionType.fromString(transaction.transactionTypeCode) ?: return@launch

            val job =
                onTransactionTypeChanged(type)

            job.join()

            val accountFrom = transaction.transactionAccountFrom
            val accountTo = transaction.transactionAccountTo
            val category = transaction.transactionCategory
            val tags = transaction.tags

            accountFrom?.let { onSelectAccount(it, getField(TRANSACTION_FROM_ACCOUNT)) }
            accountTo?.let { onSelectAccount(it, getField(TRANSACTION_TO_ACCOUNT)) }
            category?.let {
                onCategorySelected(
                    it.id.toString(),
                    it.name,
                    getField(TRANSACTION_CATEGORY)
                )
            }
            tags.forEach {
                onTagSelected(it, getField(TRANSACTION_TAG))
            }
            getField(TRANSACTION_NAME)?.let {
                onFieldValueChanged(it, transaction.transactionName)
            }
            getField(TRANSACTION_AMOUNT)?.let {
                onFieldValueChanged(it, transaction.minorUnitAmount.toString())
            }
            getField(TRANSACTION_CURRENCY)?.let {
                onFieldValueChanged(it, transaction.currency)
            }
            getField(TRANSACTION_DATE)?.let {
                onFieldValueChanged(it, transaction.transactionDate)
            }
            _viewState.update {
                it.copy(
                    currencyCode = transaction.currency
                )
            }

            delay(1000)

            when (type) {
                TransactionType.EXPENSES -> {
                    combinedCurrencyExchangeViewState.value.transactionCurrencyViewState?.let {
                        onCurrencyExchangeAmountChanged(
                            transaction.primaryMinorUnitAmount.toString(),
                            it
                        )
                    }
                    combinedCurrencyExchangeViewState.value.accountCurrencyViewState?.let {
                        onCurrencyExchangeAmountChanged(
                            transaction.accountMinorUnitAmount.toString(),
                            it
                        )
                    }
                }

                TransactionType.INCOME -> {
                    combinedCurrencyExchangeViewState.value.transactionCurrencyViewState?.let {
                        onCurrencyExchangeAmountChanged(
                            transaction.primaryMinorUnitAmount.toString(),
                            it
                        )
                    }
                }

                TransactionType.TRANSFER -> {
                    combinedCurrencyExchangeViewState.value.transactionCurrencyViewState?.let {
                        onCurrencyExchangeAmountChanged(
                            transaction.accountMinorUnitAmount.toString(),
                            it
                        )
                    }
                }

                else -> {}
            }


            toggleLoader(false)
        }
    }

    fun onCategorySelected(id: String, category: String, targetField: FEField?) {
        onFieldValueChanged(targetField, category, id)
    }


    fun onTagSelected(tag: Tag, targetField: FEField?) {
        val initialIds = targetField?.valueItem?.id
        val updatedIds =
            if (initialIds.isNullOrBlank()) tag.id.toString() else initialIds + ", ${tag.id}"

        val tagNames = targetField?.valueItem?.value
        val updatedNames =
            if (tagNames.isNullOrBlank()) "#${tag.name}" else tagNames + " #${tag.name}"

        onFieldValueChanged(
            targetField,
            updatedNames,
            updatedIds
        )
    }

    fun onSelectAccount(account: AccountGroup.Account, targetField: FEField?) {
        onFieldValueChanged(
            targetField,
            account.accountName,
            account.accountId.toString(),
        )

        when (_currentTransactionType.value) {
            TransactionType.UPDATE_ACCOUNT, TransactionType.INCOME -> {
                viewModelScope.launch {
                    val account = accountRepository.getAccountById(account.accountId)
                    _viewState.update {
                        it.copy(
                            currencyCode = account.currency,
                        )
                    }
                }
            }

            TransactionType.TRANSFER -> {
                if (targetField?.id == TRANSACTION_FROM_ACCOUNT) {
                    viewModelScope.launch {
                        val account = accountRepository.getAccountById(account.accountId)
                        _viewState.update {
                            it.copy(
                                currencyCode = account.currency,
                            )
                        }
                    }
                }
            }

            else -> {}
        }

        _resetMonetary.tryEmit(Unit)
    }

    fun onTransactionTypeChanged(transactionType: TransactionType): Job {
        _currentTransactionType.value = transactionType
        return viewModelScope.launch {
            updateFields()
            updateCategories(transactionType)
        }
    }

    private fun updateFields() {
        val fields = contentProvider.getContent(_currentTransactionType.value)
        val currency = when (_currentTransactionType.value) {
            TransactionType.UPDATE_ACCOUNT -> {
                null
            }

            else -> _primaryCurrency
        }
        val updatedFields = fields.map {
            if (it.id == TRANSACTION_CURRENCY) {
                return@map it.copy(
                    valueItem = FEField.Value(it.valueItem.id, currency ?: "")
                )
            } else it
        }

        _viewState.update {
            it.copy(
                fields = updatedFields,
                currencyCode = currency ?: ""
            )
        }
    }


    fun onFieldValueChanged(
        field: FEField?,
        newValue: String,
        id: String? = null,
        updateMonetary: Boolean = false
    ) {
        if (field == null) return

        val updatedFields = _viewState.value.fields.map { item ->
            if (item.id == field.id) {
                item.copy(
                    valueItem = FEField.Value(
                        id = id ?: "",
                        value = newValue
                    )
                )
            } else item
        }

        _viewState.update {
            it.copy(
                fields = updatedFields,
            )
        }

        if (updateMonetary) _resetMonetary.tryEmit(Unit)
    }

    private fun getField(fieldId: String): FEField? {
        return _viewState.value.fields.firstOrNull { it.id == fieldId }
    }

    private fun toggleLoader(toggle: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = toggle
            )
        }
    }

    fun currencyVisualTransformationBuilder(): CurrencyVisualTransformation.Builder {
        return currencyVisualTransformationBuilder
    }

    fun currencyFormatter(): CurrencyFormatter {
        return currencyFormatter
    }

    fun resetErrorState() {
        snackBarState.value = SnackBarState.None
    }

    fun onClearField(feField: FEField) {
        onFieldValueChanged(feField, "", "")
    }

    private suspend fun updateCategories(transactionType: TransactionType) {
        coroutineScope {
            val categories =
                transactionCategoryRepository.getCategories(transactionType.name).firstOrNull()
            _viewState.update {
                it.copy(
                    transactionCategories = categories
                )
            }
        }
    }

    fun onBack() {
        routeNavigator.pop()
    }

    fun onCallbackFieldTap(field: FEField) {
        when (field.id) {
            TRANSACTION_TAG -> toggleBottomSheet(
                TransactionEditorViewState.TagSelectionBottomSheetState(
                    field
                )
            )

            TRANSACTION_CURRENCY -> toggleBottomSheet(
                TransactionEditorViewState.CurrencySelectionBottomSheetState(
                    field
                )
            )

            TRANSACTION_DATE -> onToggleCalendar(field)

            TRANSACTION_CATEGORY -> toggleBottomSheet(
                TransactionEditorViewState.CategorySelectionBottomSheetState(
                    field
                )
            )

            TRANSACTION_FROM_ACCOUNT, TRANSACTION_TO_ACCOUNT -> toggleBottomSheet(
                TransactionEditorViewState.AccountSelectionBottomSheetState(field)
            )

            TRANSACTION_SCHEDULER_PERIOD -> toggleBottomSheet(
                TransactionEditorViewState.PeriodSelectionBottomSheetState(field),
            )

            TRANSACTION_UPDATE_ACCOUNT_TYPE -> toggleBottomSheet(
                TransactionEditorViewState.UpdateTypeSelectionBottomSheetState(field)
            )

            TRANSACTION_UPDATE_ACCOUNT -> toggleBottomSheet(
                TransactionEditorViewState.AccountSelectionBottomSheetState(field)
            )
        }
    }

    // If pass a null, mean to toggle off calendar.
    fun onToggleCalendar(field: FEField?) {
        _viewState.update {
            it.copy(
                calendarState = field?.let { TransactionEditorViewState.CalendarState(it) }
            )
        }
    }

    fun toggleBottomSheet(bottomSheetState: TransactionEditorViewState.BottomSheetState?) {
        _viewState.update {
            it.copy(
                bottomSheetState = bottomSheetState
            )
        }
    }

    fun onCurrencySelected(currency: String, targetField: FEField?) {
        onFieldValueChanged(targetField, currency)
        _viewState.update {
            it.copy(
                currencyCode = currency,
            )
        }
        _resetMonetary.tryEmit(Unit)
    }

    // TODO: Refactor so that this doesn't need to be here
    open fun onPeriodSelected(period: SchedulerPeriod, targetField: FEField?) {
        onFieldValueChanged(targetField, period.name, period.name)
    }

    abstract fun calendarSelectionRange(): ClosedRange<LocalDate>

    private fun handleFailure(throwable: Throwable) {
        toggleLoader(false)
        snackBarState.value = SnackBarState.Error(throwable.message)
    }

    private fun handleSuccess() {
        toggleLoader(false)
        _viewState.update {
            it.copy(
                success = true
            )
        }
    }

    private suspend fun onSubmitExpenses() {
        val fields = _viewState.value.fields
        val accountCurrencyViewState =
            combinedCurrencyExchangeViewState.value.accountCurrencyViewState
        val transactionCurrencyViewState =
            combinedCurrencyExchangeViewState.value.transactionCurrencyViewState

        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.valueItem?.value
        val fromAccountId =
            fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }?.valueItem?.id
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val categoryId =
            fields.firstOrNull { it.id == TRANSACTION_CATEGORY }?.valueItem?.id
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.valueItem?.value
        val currency = fields.firstOrNull { it.id == TRANSACTION_CURRENCY }?.valueItem?.value
        val primaryAmount = transactionCurrencyViewState?.toCurrencyAmount ?: amount ?: "0"
        val accountAmount = accountCurrencyViewState?.toCurrencyAmount ?: amount ?: "0"

        val tagIds = fields.firstOrNull { it.id == TRANSACTION_TAG }?.valueItem?.id
        if (name == null || amount == null || categoryId == null || date == null || fromAccountId == null || currency == null) {
            handleFailure(Exception("Something wrong"))
            toggleLoader(false)
            return
        }

        submitExpenses(
            id = dataId,
            name = name,
            fromAccountId = fromAccountId.toInt(),
            amount = amount.toLong(),
            categoryId = categoryId.toInt(),
            transactionDate = date,
            currency = currency,
            accountMinorUnitAmount = accountAmount.toLong(),
            primaryMinorUnitAmount = primaryAmount.toLong(),
            tagIds = if (tagIds.isNullOrBlank()) listOf() else tagIds.split(", ")
                .map { it.toInt() },
        ).fold(
            onSuccess = {
                handleSuccess()
            },
            onFailure = {
                handleFailure(it)
            }
        )
    }

    private suspend fun onSubmitIncome() {
        val fields = _viewState.value.fields
        val transactionCurrencyViewState =
            combinedCurrencyExchangeViewState.value.transactionCurrencyViewState

        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.valueItem?.value
        val toAccountId =
            fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }?.valueItem?.id
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val categoryId =
            fields.firstOrNull { it.id == TRANSACTION_CATEGORY }?.valueItem?.id
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.valueItem?.value
        val primaryAmount = transactionCurrencyViewState?.toCurrencyAmount ?: amount ?: "0"
        val tagIds = fields.firstOrNull { it.id == TRANSACTION_TAG }?.valueItem?.id
        if (name == null || amount == null || categoryId == null || date == null || toAccountId == null) {
            handleFailure(Exception("Something wrong"))
            toggleLoader(false)
            return
        }

        submitIncome(
            id = dataId,
            name = name,
            toAccountId = toAccountId.toInt(),
            amount = amount.toLong(),
            categoryId = categoryId.toInt(),
            transactionDate = date,
            currency = transactionCurrencyViewState?.fromCurrency ?: "",
            primaryMinorUnitAmount = primaryAmount.toLong(),
            tagIds = if (tagIds.isNullOrBlank()) listOf() else tagIds.split(", ")
                .map { it.toInt() },
        ).fold(
            onSuccess = {
                handleSuccess()
            },
            onFailure = {
                handleFailure(it)
            }
        )
    }

    private suspend fun onSubmitTransfer() {
        val fields = _viewState.value.fields
        val transactionCurrencyExchangeViewState =
            combinedCurrencyExchangeViewState.value.transactionCurrencyViewState

        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.valueItem?.value
        val fromAccountId =
            fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }?.valueItem?.id
        val toAccountId =
            fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }?.valueItem?.id
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.valueItem?.value
        val accountAmount = transactionCurrencyExchangeViewState?.toCurrencyAmount ?: amount ?: "0"

        val tagIds = fields.firstOrNull { it.id == TRANSACTION_TAG }?.valueItem?.id
        if (name == null || amount == null || fromAccountId == null || date == null || toAccountId == null) {
            handleFailure(Exception("Something wrong"))
            toggleLoader(false)
            return
        }

        submitTransfer(
            id = dataId,
            name = name,
            fromAccountId = fromAccountId.toInt(),
            toAccountId = toAccountId.toInt(),
            amount = amount.toLong(),
            transactionDate = date,
            currency = transactionCurrencyExchangeViewState?.fromCurrency ?: "",
            accountMinorUnitAmount = accountAmount.toLong(),
            tagIds = if (tagIds.isNullOrBlank()) listOf() else tagIds.split(", ")
                .map { it.toInt() },
        ).fold(
            onSuccess = {
                handleSuccess()
            },
            onFailure = {
                handleFailure(it)
            }
        )
    }

    private suspend fun onSubmitUpdateAccount() {
        val fields = _viewState.value.fields
        val updateType =
            fields.firstOrNull { it.id == TRANSACTION_UPDATE_ACCOUNT_TYPE }?.valueItem?.value?.let {
                UpdateAccountType.fromString(it)
            }
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val accountId =
            fields.firstOrNull { it.id == TRANSACTION_UPDATE_ACCOUNT }?.valueItem?.id

        if (amount == null || accountId == null || updateType == null) {
            handleFailure(Exception("Something wrong"))
            toggleLoader(false)
            return
        }

        submitUpdate(
            dataId,
            accountId.toInt(),
            updateType == UpdateAccountType.INCREMENT,
            transactionDate = LocalDate.now().toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN),
            amount = amount.toLong()
        ).fold(
            onSuccess = {
                handleSuccess()
            },
            onFailure = {
                handleFailure(it)
            }
        )
    }

    open fun allowProceed(): Boolean {
        return _viewState.value.fields
            .filter { it.id != TRANSACTION_TAG }
            .firstOrNull { it.valueItem.value.isEmpty() } == null && _viewState.value.errors == null
    }
}

internal data class TransactionEditorDataUI(
    val transactionName: String,
    val transactionAccountFrom: AccountGroup.Account?,
    val transactionAccountTo: AccountGroup.Account?,
    val transactionTypeCode: String,
    val minorUnitAmount: Long,
    val accountMinorUnitAmount: Long,
    val primaryMinorUnitAmount: Long,
    val currency: String,
    val transactionDate: String,
    val transactionCategory: Transaction.TransactionCategory?,
    val tags: List<Tag>
)

internal data class CurrencyExchangeViewState(
    val accountCurrencyViewState: CurrencyViewState? = null,
    val transactionCurrencyViewState: CurrencyViewState? = null,
) {
    val showDialog: Boolean
        get() {
            return accountCurrencyViewState?.showDialog == true || transactionCurrencyViewState?.showDialog == true
        }
    val showDialogState: CurrencyViewState?
        get() {
            return if (accountCurrencyViewState?.showDialog == true) accountCurrencyViewState
            else if (transactionCurrencyViewState?.showDialog == true) transactionCurrencyViewState
            else null
        }

    companion object {
        fun empty(): CurrencyExchangeViewState {
            return CurrencyExchangeViewState(null, null)
        }
    }
}

internal fun FEField.shouldResetMonetary(): Boolean {
    return listOf(
        TRANSACTION_AMOUNT,
        PRIMARY_TRANSACTION_AMOUNT,
        PRIMARY_CURRENCY_RATE,
        TRANSACTION_FROM_ACCOUNT,
        TRANSACTION_TO_ACCOUNT,
        TRANSACTION_CURRENCY
    ).contains(this.id)
}

internal data class TransactionEditorViewState(
    val fields: List<FEField> = listOf(),
    val errors: Map<String, String>? = null,
    val calendarState: CalendarState? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val transactionCategories: TransactionCategories? = null,
    val accountGroups: List<AccountGroup> = listOf(),
    val bottomSheetState: BottomSheetState? = null,
    val currencyCode: String = "",
    val currencyList: List<String> = listOf(),
    val tagList: List<Tag> = listOf(),
) {
    val showBottomSheet get() = bottomSheetState != null

    data class CalendarState(
        val targetField: FEField
    )

    sealed interface BottomSheetState {
        val feField: FEField
    }

    data class CategorySelectionBottomSheetState(
        override val feField: FEField
    ) : BottomSheetState

    data class AccountSelectionBottomSheetState(
        override val feField: FEField
    ) : BottomSheetState

    data class CurrencySelectionBottomSheetState(
        override val feField: FEField
    ) : BottomSheetState

    data class TagSelectionBottomSheetState(
        override val feField: FEField
    ) : BottomSheetState

    data class PeriodSelectionBottomSheetState(
        override val feField: FEField
    ) : BottomSheetState

    data class UpdateTypeSelectionBottomSheetState(
        override val feField: FEField
    ) : BottomSheetState
}

// TODO: Need to handle Locale
internal enum class UpdateAccountType {
    INCREMENT, DEDUCTION;

    companion object {
        fun fromString(type: String): UpdateAccountType? {
            return when (type) {
                INCREMENT.name -> INCREMENT
                DEDUCTION.name -> DEDUCTION
                else -> null
            }
        }
    }
}

internal data class CurrencyViewState(
    val type: Type,
    val fromCurrency: String,
    val toCurrency: String,
    val fromCurrencyAmount: String,
    val toCurrencyAmount: String,
    val toCurrencyAmountFormatted: String,
    val rate: String,
    val showDialog: Boolean = false
) {
    enum class Type {
        TRANSACTION, ACCOUNT
    }
}

private data class TriggerCurrencyRateResponse(
    val rate: Float,
    val primaryCurrencyRate: Float,
    val showPrimaryCurrencyRateFields: Boolean = false
)