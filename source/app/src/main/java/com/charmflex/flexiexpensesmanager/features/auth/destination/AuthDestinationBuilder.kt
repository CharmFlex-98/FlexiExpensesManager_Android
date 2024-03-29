package com.charmflex.flexiexpensesmanager.features.auth.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AuthRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreen
import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel

class AuthDestinationBuilder : DestinationBuilder{
    override fun NavGraphBuilder.buildGraph() {
        navigation(
            startDestination = AuthRoutes.LANDING,
            route = AuthRoutes.ROOT
        ) {
            LandingDestination()
            // LOGIN
            // REGISTER
        }
    }

    private fun NavGraphBuilder.LandingDestination() {
        composable(
            route = AuthRoutes.LANDING
        ) {
            val landingScreenViewModel: LandingScreenViewModel = getViewModel { LandingScreenViewModel() }
            LandingScreen(landingScreenViewModel = landingScreenViewModel)
        }
    }
}