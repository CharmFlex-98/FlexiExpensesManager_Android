package com.charmflex.flexiexpensesmanager.features.home.destination

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.auth.di.AuthComponent
import com.charmflex.flexiexpensesmanager.features.home.di.HomeComponent
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.detail.ExpensesDetailScreen

internal class HomeDestinationBuilder(
    private val appContext: Context
) : DestinationBuilder {
    private val homeComponent by lazy { HomeComponent.build(appContext = appContext) }
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
                homeComponent.homeViewModel()
            }

            HomeScreen(viewModel = homeViewModel)
        }
    }
}