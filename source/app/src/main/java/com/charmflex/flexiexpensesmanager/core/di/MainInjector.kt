package com.charmflex.flexiexpensesmanager.core.di

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.network.NetworkClientBuilder
import com.charmflex.flexiexpensesmanager.core.tracker.EventTracker
import com.charmflex.flexiexpensesmanager.core.utils.ResourcesProvider

internal interface MainInjector {
    fun routeNavigator(): RouteNavigator
    fun eventTracker(): EventTracker
}