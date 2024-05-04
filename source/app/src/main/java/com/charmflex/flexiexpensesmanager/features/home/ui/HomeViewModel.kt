package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private val _refresh = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val refresh = _refresh.asSharedFlow()

    fun createNewExpenses() {
        routeNavigator.navigateTo(TransactionRoute.NEW_RECORD)
    }

    fun notifyObservers() {
        _refresh.tryEmit(true)
    }
}