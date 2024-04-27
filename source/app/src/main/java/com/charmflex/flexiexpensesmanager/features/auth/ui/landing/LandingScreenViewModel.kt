package com.charmflex.flexiexpensesmanager.features.auth.ui.landing

import androidx.lifecycle.ViewModel
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import javax.inject.Inject

class LandingScreenViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
): ViewModel() {

    fun onGuestLogin() {
        routeNavigator.navigateTo(HomeRoutes.ROOT)
    }
}