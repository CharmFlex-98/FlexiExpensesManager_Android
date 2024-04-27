package com.charmflex.flexiexpensesmanager.core.di

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.ResourceProvider

internal interface MainInjector {
    fun routeNavigator(): RouteNavigator
    fun resourcesProvider(): ResourceProvider

}