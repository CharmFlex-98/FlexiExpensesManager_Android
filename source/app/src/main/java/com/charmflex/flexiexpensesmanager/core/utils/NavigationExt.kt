package com.charmflex.flexiexpensesmanager.core.utils

import androidx.navigation.NavController

fun NavController.navigateTo(route: String, args: Map<String, Any>?) {
    args?.let {
        this.currentBackStackEntry?.savedStateHandle?.let { savedStateHandler ->
            for (arg in args) {
                savedStateHandler[arg.key] = arg.value
            }
        }
    }
    navigate(route) {
        launchSingleTop = true
    }
}

fun NavController.navigateAndPopUpTo(route: String, popUpToRouteInclusive: String? = null) {
    navigate(route) {
        launchSingleTop = true

        if (popUpToRouteInclusive != null) {
            popUpTo(popUpToRouteInclusive) {
                inclusive = true
            }
        }

    }
}

fun NavController.popWithArgs(data: Map<String, Any>) {
    data.let { args ->
        this.previousBackStackEntry?.savedStateHandle?.let { savedStateHandler ->
            for (arg in args) {
                savedStateHandler[arg.key] = arg.value
            }
        }
    }

    popBackStack()
}