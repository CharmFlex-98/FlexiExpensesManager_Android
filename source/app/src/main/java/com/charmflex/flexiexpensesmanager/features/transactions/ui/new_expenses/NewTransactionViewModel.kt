package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_expenses

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.NewTransactionContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_AMOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_CATEGORY
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_DATE
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_FROM_ACCOUNT
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_NAME
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.SubmitTransactionUseCase
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class NewTransactionViewModel @Inject constructor(
    private val contentProvider: NewTransactionContentProvider,
    private val routeNavigator: RouteNavigator,
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
        viewModelScope.launch {
            currentTransactionType.collectLatest {
                val content = contentProvider.getContent(it)
                _viewState.update {
                    it.copy(
                        fields = content
                    )
                }
            }
        }
    }

    fun resetErrorState() {
        snackBarState.value = SnackBarState.None
    }

    fun initContent(transactionType: TransactionType) {
        _currentTransactionType.update { transactionType }
    }

    fun onFieldValueChanged(field: FEField?, newValue: String) {
        if (field == null) return

        val updatedFields = _viewState.value.fields.map { item ->
            if (item == field) {
                item.copy(
                    value = newValue
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
        val name = fields.firstOrNull { it.id == TRANSACTION_NAME }?.value
        val fromAccount = fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }?.let { feField ->
            if (feField.type is FEField.FieldType.SingleItemSelection) {
                feField.type.options.first {
                    it.title == feField.value
                }.id
            } else ""
        }
        val amount = fields.firstOrNull { it.id == TRANSACTION_AMOUNT }?.value
        val categoryId = fields.firstOrNull { it.id == TRANSACTION_CATEGORY }?.let { feField ->
            if (feField.type is FEField.FieldType.SingleItemSelection) {
                feField.type.options.first {
                    it.title == feField.value
                }.id
            } else ""
        }
        val date = fields.firstOrNull { it.id == TRANSACTION_DATE }?.value

        if (name == null || amount == null || categoryId == null || date == null || fromAccount == null) return

        viewModelScope.launch {
            when (_currentTransactionType.value) {
                TransactionType.EXPENSES -> {
                    submitTransactionUseCase.submitExpenses(
                        fromAccountId = fromAccount.toInt(),
                        amount = amount.toInt(),
                        categoryId = categoryId.toInt(),
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

    fun onBack() {
        routeNavigator.pop()
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
}

internal data class NewTransactionViewState(
    val fields: List<FEField> = listOf(),
    val errors: Map<String, String>? = null,
    val calendarState: CalendarState = CalendarState(),
    val isLoading: Boolean = false,
    val success: Boolean = false,
) {
    val allowProceed: Boolean
        get() = fields.firstOrNull { it.value.isEmpty() } == null && errors == null

    data class CalendarState(
        val isVisible: Boolean = false,
        val targetField: FEField? = null
    )
}

internal enum class TransactionType {
    EXPENSES, INCOME, TRANSFER
}