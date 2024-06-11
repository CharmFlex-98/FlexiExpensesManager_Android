package com.charmflex.flexiexpensesmanager.features.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.features.currency.usecases.UpdateCurrencyRateUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val updateCurrencyRateUseCase: UpdateCurrencyRateUseCase
) : ViewModel() {
    private val _refresh = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val refresh = _refresh.asSharedFlow()

    init {
        updateCurrencyRate()
    }

    private fun updateCurrencyRate() {
        viewModelScope.launch {
            updateCurrencyRateUseCase().fold(
                onSuccess = {},
                onFailure = {
                    Log.d("test", it.message ?: "")
                }
            )
        }
    }

    fun createNewExpenses() {
        routeNavigator.navigateTo(TransactionRoute.newTransactionDestination())
    }

    fun notifyObservers() {
        _refresh.tryEmit(true)
    }
}