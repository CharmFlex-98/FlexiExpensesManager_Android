package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.NewTransactionContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_AMOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_CATEGORY
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_DATE
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_FROM_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_NAME
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_TO_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.SubmitTransactionUseCase
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class NewTransactionViewModel @Inject constructor(
    private val contentProvider: NewTransactionContentProvider,
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator,
    private val transactionCategoryRepository: TransactionCategoryRepository,
    private val submitTransactionUseCase: SubmitTransactionUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(NewTransactionViewState())
    val viewState = _viewState.asStateFlow()
    var transactionType = TransactionType.values()
        private set
    private val _currentTransactionType = MutableStateFlow(TransactionType.EXPENSES)
    val currentTransactionType = _currentTransactionType.asStateFlow()
    val snackBarState = mutableStateOf<SnackBarState>(SnackBarState.None)

    init {
        initContent(_currentTransactionType.value)

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

    fun resetErrorState() {
        snackBarState.value = SnackBarState.None
    }

    fun initContent(transactionType: TransactionType = TransactionType.EXPENSES) {
        _currentTransactionType.update { transactionType }
        viewModelScope.launch {
            val fields = contentProvider.getContent(transactionType)
            val categories = transactionCategoryRepository.getAllCategories(transactionType.name).firstOrNull()
            categories?.let { cats ->
                _viewState.update {
                    it.copy(
                        fields = fields,
                        transactionCategories = cats
                    )
                }
            }
        }
    }

    fun onFieldValueChanged(field: FEField?, newValue: String, id: String? = null) {
        if (field == null) return

        val updatedFields = _viewState.value.fields.map { item ->
            if (item.id == field.id) {
                item.copy(
                    value = FEField.Value(
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

    private fun updateErrors() {
        // Check error
    }

    fun onConfirmed() {
        toggleLoader(true)
        if (isSubmissionValid()) {
            viewModelScope.launch {
                when (_currentTransactionType.value) {
                    TransactionType.EXPENSES -> submitExpenses()
                    else -> {}
                }
            }
        }
    }

    private fun isSubmissionValid(): Boolean {
        // TODO: Need to implement logic
        return true
    }

    private suspend fun submitExpenses() {
        val fields = _viewState.value.fields
        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.value?.value
        val fromAccount = fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }?.value?.id?.toIntOrNull()
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.value?.value
        val categoryId = fields.firstOrNull { it.id == TRANSACTION_CATEGORY }?.value?.id?.toIntOrNull()
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.value?.value

        if (name == null || amount == null || categoryId == null || date == null || fromAccount == null) return

        viewModelScope.launch {
            when (_currentTransactionType.value) {
                TransactionType.EXPENSES -> {
                    submitTransactionUseCase.submitExpenses(
                        name = name,
                        fromAccountId = fromAccount.toInt(),
                        amount = amount.toInt(),
                        categoryId = categoryId,
                        transactionDate = date
                    ).fold(
                        onSuccess = {
                            handleSuccess()
                        },
                        onFailure = {
                            handleFailure(it)
                        }
                    )
                }

                else -> {}
            }
        }
    }

    private fun handleSuccess() {
        toggleLoader(false)
        _viewState.update {
            it.copy(
                success = true
            )
        }
    }

    fun showAccountsByGroupId(groupId: Int): List<AccountGroup.Account> {
        return _viewState.value.getAccountsByGroupId(groupId)
    }

    private fun handleFailure(throwable: Throwable) {
        toggleLoader(false)
        snackBarState.value = SnackBarState.Error(throwable.message)
    }

    private fun toggleLoader(toggle: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = toggle
            )
        }
    }

    fun onBack(refreshHome: Boolean) {
        if (!refreshHome) routeNavigator.pop()
        else {
            val data = mapOf(HomeRoutes.Args.HOME_REFRESH to true)
            routeNavigator.popWithArguments(data = data)
        }
    }

    fun onCallbackFieldTap(field: FEField) {
        when (field.id) {
            TRANSACTION_DATE -> onToggleCalendar(field)
            TRANSACTION_CATEGORY -> toggleBottomSheet(NewTransactionViewState.CategorySelectionBottomSheetState(field))
            TRANSACTION_FROM_ACCOUNT, TRANSACTION_TO_ACCOUNT -> toggleBottomSheet(NewTransactionViewState.AccountSelectionBottomSheetState(field))
        }
    }

    // If pass a null, mean to toggle off calendar.
    fun onToggleCalendar(field: FEField?) {
        _viewState.update {
            it.copy(
                calendarState = it.calendarState.copy(
                    isVisible = field != null,
                    targetField = field
                )
            )
        }
    }

    fun toggleBottomSheet(bottomSheetState: NewTransactionViewState.BottomSheetState?) {
        _viewState.update {
            it.copy(
                bottomSheetState = bottomSheetState
            )
        }
    }

    fun onCategorySelected(id: String, category: String) {
        onFieldValueChanged(_viewState.value.bottomSheetState?.feField, category, id)
    }

    fun onSelectAccount(account: AccountGroup.Account) {
        onFieldValueChanged(_viewState.value.bottomSheetState?.feField, account.accountName, account.accountId.toString())
    }
}

internal data class NewTransactionViewState(
    val fields: List<FEField> = listOf(),
    val errors: Map<String, String>? = null,
    val calendarState: CalendarState = CalendarState(),
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val transactionCategories: TransactionCategories? = null,
    val accountGroups: List<AccountGroup> = listOf(),
    val bottomSheetState: BottomSheetState? = null
) {
    val allowProceed: Boolean
        get() = fields.firstOrNull { it.value.value.isEmpty() } == null && errors == null

    fun getAccountsByGroupId(accountGroupId: Int): List<AccountGroup.Account> {
        return accountGroups.firstOrNull { it.accountGroupId == accountGroupId }?.accounts ?: listOf()
    }

    val showBottomSheet get() = bottomSheetState != null

    data class CalendarState(
        val isVisible: Boolean = false,
        val targetField: FEField? = null
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
}