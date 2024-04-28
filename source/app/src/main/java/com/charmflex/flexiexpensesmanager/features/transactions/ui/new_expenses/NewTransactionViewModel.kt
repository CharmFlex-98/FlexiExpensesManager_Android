package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.account.domain.model.Account
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.NewTransactionContentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class NewTransactionViewModel @Inject constructor(
    private val contentProvider: NewTransactionContentProvider,
    private val routeNavigator: RouteNavigator,
    private val repository: TransactionRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow(NewTransactionViewState())
    val viewState = _viewState.asStateFlow()
    var transactionType = TransactionType.values()
        private set
    private val _currentTransactionType = MutableStateFlow(TransactionType.EXPENSES)
    val currentTransactionType = _currentTransactionType.asStateFlow()

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
        viewModelScope.launch {
            resultOf {
                repository.createNewExpenses()
            }.fold(
                onSuccess = {
                    toggleLoader(false)
                    _viewState.update {
                        it.copy(
                            success = true
                        )
                    }
                },
                onFailure = {
                    toggleLoader(false)
                }
            )
        }
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