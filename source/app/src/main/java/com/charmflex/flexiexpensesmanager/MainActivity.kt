package com.charmflex.flexiexpensesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigatorListener
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AuthRoutes
import com.charmflex.flexiexpensesmanager.features.account.destinations.AccountDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.auth.destination.AuthDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.category.category.destinations.CategoryDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.currency.destinations.CurrencyDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.transactions.destination.TransactionDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.home.destination.HomeDestinationBuilder
import com.example.compose.FlexiExpensesManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val routeNavigator =
            (application as AppComponentProvider).getAppComponent().routeNavigator()

        // Test

        setContent {
            val navController = rememberNavController()

            RouteNavigatorListener(routeNavigator = routeNavigator, navController = navController)

            FlexiExpensesManagerTheme {
                NavHost(navController = navController, startDestination = AuthRoutes.ROOT) {
                    createDestinations().forEach {
                        with(it) { buildGraph() }
                    }
                }
            }
        }
    }
}

private fun createDestinations(): List<DestinationBuilder> {
    return listOf(
        AuthDestinationBuilder(),
        HomeDestinationBuilder(),
        TransactionDestinationBuilder(),
        CategoryDestinationBuilder(),
        AccountDestinationBuilder(),
        CurrencyDestinationBuilder()
    )
}