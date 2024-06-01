package com.charmflex.flexiexpensesmanager.features.home.destination

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.charmflex.flexiexpensesmanager.core.di.AppComponent
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeViewModel

internal class HomeDestinationBuilder : DestinationBuilder {
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }
    override fun NavGraphBuilder.buildGraph() {
        navigation(
            route = HomeRoutes.ROOT,
            startDestination = HomeRoutes.HOME
        ) {
            homeScreen()
        }
    }

    private fun NavGraphBuilder.homeScreen() {
        composable(
            route = HomeRoutes.HOME
        ) {
            val homeViewModel: HomeViewModel = getViewModel {
                appComponent.homeViewModel()
            }
            val shouldRefresh = it.savedStateHandle.get<Boolean>(HomeRoutes.Args.HOME_REFRESH) ?: false

            HomeScreen(
                homeViewModel = homeViewModel,
                shouldRefresh = shouldRefresh,
                appComponent = appComponent
            )
        }
    }
}