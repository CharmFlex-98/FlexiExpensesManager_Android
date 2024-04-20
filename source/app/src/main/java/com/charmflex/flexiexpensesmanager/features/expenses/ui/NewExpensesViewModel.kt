package com.charmflex.flexiexpensesmanager.features.expenses.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class NewExpensesViewModel @Inject constructor(
    private val contentProvider: NewExpensesContentProvider,
    private val routeNavigator: RouteNavigator
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

    fun onFieldValueChanged(field: NewExpensesField, newValue: String) {
        val updatedFields = _viewState.value.fields.map { item ->
            if (item.key == field.key) {
                item.copy(
                    value = newValue
                )
            } else item
        }

        _viewState.update {
            it.copy(
                fields = updatedFields
            )
        }
    }

    fun onBack() {
        routeNavigator.pop()
    }
}

internal data class NewExpensesViewState(
    val fields: List<NewExpensesField> = listOf(),
    val error: Map<String, String>? = null
)

internal data class NewExpensesField(
    val key: String,
    @StringRes
    val labelId: Int,
    @StringRes
    val hintId: Int,
    val value: String = "",
    val type: FieldType,
    val isEnable: Boolean = true
) {
    enum class FieldType {
        TEXT, NUMBER, SELECTION
    }
}