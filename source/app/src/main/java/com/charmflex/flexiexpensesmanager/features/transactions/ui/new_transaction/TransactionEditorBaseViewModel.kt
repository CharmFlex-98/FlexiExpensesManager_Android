package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.core.utils.unwrapResult
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.CurrencyRate
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyUseCase
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_AMOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_CATEGORY
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_CURRENCY
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_DATE
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_FROM_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_NAME
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_RATE
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_SCHEDULER_PERIOD
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_TAG
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_TO_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_UPDATE_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_UPDATE_ACCOUNT_TYPE
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

internal abstract class TransactionEditorBaseViewModel(
    private val contentProvider: TransactionEditorContentProvider,
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator,
    private val transactionCategoryRepository: TransactionCategoryRepository,
    private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val tagRepository: TagRepository,
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


    // Must be called by child
    fun initialise() {
        onUpdateUserCurrencyOptions()
        observeTransactionTagOptions()
        observeAccountsUpdate()

        if (dataId != null) {
            loadData(dataId)
        } else {
            onTransactionTypeChanged(_currentTransactionType.value)
        }
    }

    private fun onUpdateUserCurrencyOptions() {
        viewModelScope.launch {
            val all = unwrapResult (
                getCurrencyUseCase.getAll()
            )
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
                onFieldValueChanged(it, transaction.amountInCent.toString())
            }
            getField(TRANSACTION_DATE)?.let {
                onFieldValueChanged(it, transaction.transactionDate)
            }
            getField(TRANSACTION_CURRENCY)?.let {
                onFieldValueChanged(it, transaction.currency)
            }
            getField(TRANSACTION_RATE)?.let {
                onFieldValueChanged(it, transaction.rate.toString())
            }
            _viewState.update {
                it.copy(
                    currencyCode = transaction.currency
                )
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
        val updatedNames = if (tagNames.isNullOrBlank()) "#${tag.name}" else tagNames + " #${tag.name}"

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
            account.accountId.toString()
        )
        val currencyRate = viewState.value.currencyList.firstOrNull {
            it.name == account.currency
        }
        val currencyField = viewState.value.fields.firstOrNull {
            it.id == TRANSACTION_CURRENCY
        }

        if (currencyRate == null || currencyField == null) return

        onCurrencySelected(currencyRate, currencyField)
    }

    fun onTransactionTypeChanged(transactionType: TransactionType): Job {
        _currentTransactionType.update { transactionType }
        return viewModelScope.launch {
            val fields = contentProvider.getContent(transactionType)
            val currency = unwrapResult(getCurrencyUseCase.primary())
            val updatedFields = fields.map {
                if (it.id == TRANSACTION_CURRENCY) {
                    return@map it.copy(
                        valueItem = FEField.Value(it.valueItem.id, currency?.name ?: "")
                    )
                }
                if (it.id == TRANSACTION_RATE) {
                    return@map it.copy(
                        valueItem = FEField.Value(it.valueItem.id, currency?.rate?.toString() ?: "1")
                    )
                } else it
            }

            _viewState.update {
                it.copy(
                    fields = updatedFields,
                    currencyCode = currency?.name ?: it.currencyCode
                )
            }

            updateCategories(transactionType)
        }
    }


    fun onFieldValueChanged(field: FEField?, newValue: String, id: String? = null) {
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

    fun onCurrencySelected(currencyRate: CurrencyRate, targetField: FEField?) {
        onFieldValueChanged(targetField, currencyRate.name)
        onFieldValueChanged(
            _viewState.value.fields.firstOrNull { it.id == TRANSACTION_RATE },
            currencyRate.rate.toString()
        )
        _viewState.update {
            it.copy(
                currencyCode = currencyRate.name
            )
        }
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
        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.valueItem?.value
        val fromAccountId =
            fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }?.valueItem?.id
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val categoryId =
            fields.firstOrNull { it.id == TRANSACTION_CATEGORY }?.valueItem?.id
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.valueItem?.value
        val currency = fields.firstOrNull { it.id == TRANSACTION_CURRENCY }?.valueItem?.value
        val rate = fields.firstOrNull { it.id == TRANSACTION_RATE }?.valueItem?.value?.toFloatOrNull()
        val tagIds = fields.firstOrNull { it.id == TRANSACTION_TAG }?.valueItem?.id
        if (name == null || amount == null || categoryId == null || date == null || fromAccountId == null || currency == null || rate == null) {
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
            rate = rate,
            tagIds = if (tagIds.isNullOrBlank()) listOf() else tagIds.split(", ").map { it.toInt() },
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
        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.valueItem?.value
        val toAccountId =
            fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }?.valueItem?.id
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val categoryId =
            fields.firstOrNull { it.id == TRANSACTION_CATEGORY }?.valueItem?.id
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.valueItem?.value
        val currency = fields.firstOrNull { it.id == TRANSACTION_CURRENCY }?.valueItem?.value
        val rate = fields.firstOrNull { it.id == TRANSACTION_RATE }?.valueItem?.value?.toFloatOrNull()
        val tagIds = fields.firstOrNull { it.id == TRANSACTION_TAG }?.valueItem?.id
        if (name == null || amount == null || categoryId == null || date == null || toAccountId == null || currency == null || rate == null) {
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
            currency = currency,
            rate = rate,
            tagIds = if (tagIds.isNullOrBlank()) listOf() else tagIds.split(", ").map { it.toInt() },
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
        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.valueItem?.value
        val fromAccountId =
            fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }?.valueItem?.id
        val toAccountId =
            fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }?.valueItem?.id
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.valueItem?.value
        val currency = fields.firstOrNull { it.id == TRANSACTION_CURRENCY }?.valueItem?.value
        val rate = fields.firstOrNull { it.id == TRANSACTION_RATE }?.valueItem?.value?.toFloatOrNull()
        val tagIds = fields.firstOrNull { it.id == TRANSACTION_TAG }?.valueItem?.id
        if (name == null || amount == null || fromAccountId == null || date == null || toAccountId == null || currency == null || rate == null) {
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
            currency = currency,
            rate = rate,
            tagIds = if (tagIds.isNullOrBlank()) listOf() else tagIds.split(", ").map { it.toInt() },
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
        val currency = fields.firstOrNull { it.id == TRANSACTION_CURRENCY }?.valueItem?.value
        val rate = fields.firstOrNull { it.id == TRANSACTION_RATE }?.valueItem?.value?.toFloatOrNull()
        val updateType = fields.firstOrNull { it.id == TRANSACTION_UPDATE_ACCOUNT_TYPE }?.valueItem?.value?.let {
            UpdateAccountType.fromString(it)
        }
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.valueItem?.value

        if (amount == null || currency == null || rate == null || updateType == null) return

        val accountId =
            fields.firstOrNull { it.id == TRANSACTION_UPDATE_ACCOUNT }?.valueItem?.id
        if (accountId == null) return

        submitUpdate(
            dataId,
            accountId.toInt(),
            updateType == UpdateAccountType.INCREMENT,
            transactionDate = LocalDate.now().toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN),
            currency = currency,
            rate = rate.toFloat(),
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
    val amountInCent: Long,
    val currency: String,
    val rate: Float,
    val transactionDate: String,
    val transactionCategory: Transaction.TransactionCategory?,
    val tags: List<Tag>
)


internal data class TransactionEditorViewState(
    val fields: List<FEField> = listOf(),
    val errors: Map<String, String>? = null,
    val calendarState: CalendarState? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val transactionCategories: TransactionCategories? = null,
    val accountGroups: List<AccountGroup> = listOf(),
    val bottomSheetState: BottomSheetState? = null,
    val currencyCode: String = "MYR",
    val currencyList: List<CurrencyRate> = listOf(),
    val tagList: List<Tag> = listOf()
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