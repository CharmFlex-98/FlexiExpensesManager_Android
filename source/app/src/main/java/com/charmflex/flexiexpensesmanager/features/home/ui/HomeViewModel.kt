package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.lifecycle.ViewModel
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.ExpensesRoute
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
) : ViewModel() {

    fun createNewExpenses() {
        routeNavigator.navigateTo(ExpensesRoute.NEW_RECORD)
    }
}