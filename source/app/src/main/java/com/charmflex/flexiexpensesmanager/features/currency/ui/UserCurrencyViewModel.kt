package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.features.currency.usecases.CurrencyRate
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class UserCurrencyViewModel @Inject constructor(
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private val _viewState = MutableStateFlow(UserCurrencyViewState())
    val viewState = _viewState.asStateFlow()

    fun initialise() {
        toggleLoader(true)
        viewModelScope.launch {
            getCurrencyUseCase.secondary().fold(
                onSuccess = { res ->
                    _viewState.update {
                        it.copy(
                            currencyList = res,
                            isLoading = false
                        )
                    }
                },
                onFailure = {
                    toggleLoader(false)
                }
            )
        }
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    fun onAddButtonTap() {
        routeNavigator.navigateTo(CurrencyRoutes.currencySettingDestination(CurrencyRoutes.Args.CURRENCY_TYPE_SECONDARY))
    }
}

internal data class UserCurrencyViewState(
    val currencyList: List<CurrencyRate> = listOf(),
    val isLoading: Boolean = false
)