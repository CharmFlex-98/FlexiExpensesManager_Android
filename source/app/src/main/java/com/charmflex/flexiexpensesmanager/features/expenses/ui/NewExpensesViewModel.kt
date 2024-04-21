package com.charmflex.flexiexpensesmanager.features.expenses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.expenses.domain.repositories.ExpensesRepository
import com.charmflex.flexiexpensesmanager.features.expenses.provider.NewExpensesContentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class NewExpensesViewModel @Inject constructor(
    private val contentProvider: NewExpensesContentProvider,
    private val routeNavigator: RouteNavigator,
    private val repository: ExpensesRepository

) : ViewModel() {
    private val _viewState = MutableStateFlow(NewExpensesViewState())
    val viewState = _viewState.asStateFlow()

    init {
        _viewState.update {
            it.copy(
                fields = contentProvider.getContent()
            )
        }
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

internal data class NewExpensesViewState(
    val fields: List<FEField> = listOf(),
    val errors: Map<String, String>? = null,
    val calendarState: CalendarState = CalendarState(),
    val isLoading: Boolean = false,
    val success: Boolean = false
) {
    val allowProceed: Boolean
        get() = fields.firstOrNull { it.value.isEmpty() } == null && errors == null

    data class CalendarState(
        val isVisible: Boolean = false,
        val targetField: FEField? = null
    )
}