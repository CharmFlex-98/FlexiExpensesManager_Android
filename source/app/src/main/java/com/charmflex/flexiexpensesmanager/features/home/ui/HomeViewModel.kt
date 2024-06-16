package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.lifecycle.ViewModel
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
) : ViewModel() {
    private val _homeItemsRefreshable: MutableList<HomeItemRefreshable> = mutableListOf()

    fun initHomeRefreshable(vararg items: HomeItemRefreshable) {
        _homeItemsRefreshable.addAll(items)
    }

    fun createNewExpenses() {
        routeNavigator.navigateTo(TransactionRoute.newTransactionDestination())
    }

    // Make sure only call this when needed
    fun notifyRefresh() {
        _homeItemsRefreshable.forEach {
            it.refreshHome()
        }
    }
}

internal interface HomeItemRefreshable {
    fun refreshHome()
}