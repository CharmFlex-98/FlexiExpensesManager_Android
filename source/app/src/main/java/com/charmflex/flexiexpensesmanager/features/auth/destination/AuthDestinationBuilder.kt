package com.charmflex.flexiexpensesmanager.features.auth.destination

import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.charmflex.flexiexpensesmanager.core.di.AppComponent
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AuthRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreen
import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel

internal class AuthDestinationBuilder : DestinationBuilder{
    private val appComponent: AppComponent by lazy { AppComponentProvider.instance.getAppComponent() }

    override fun NavGraphBuilder.buildGraph() {
        navigation(
            startDestination = AuthRoutes.LANDING,
            route = AuthRoutes.ROOT
        ) {
            landingDestination()
            // LOGIN
            // REGISTER
        }
    }

    private fun NavGraphBuilder.landingDestination() {
        composable(
            route = AuthRoutes.LANDING
        ) {
            val landingScreenViewModel: LandingScreenViewModel = getViewModel { appComponent.landingScreenViewModel() }
            LandingScreen(landingScreenViewModel = landingScreenViewModel)
        }
    }
}