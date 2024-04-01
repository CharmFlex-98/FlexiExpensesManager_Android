package com.charmflex.flexiexpensesmanager.dependency_injection.modules.navigation

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigatorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface NavigationModule {
    companion object {
        @Provides
        @Singleton
        fun providesRouteNavigator(): RouteNavigator {
            return RouteNavigatorImpl()
        }
    }
}