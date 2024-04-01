package com.charmflex.flexiexpensesmanager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigatorListener
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AuthRoutes
import com.charmflex.flexiexpensesmanager.features.auth.destination.AuthDestinationBuilder
import com.example.compose.FlexiExpensesManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val routeNavigator = (application as MainComponentProvider).getMainComponent().routeNavigator()

        // Test

        setContent {
            val navController = rememberNavController()

            RouteNavigatorListener(routeNavigator = routeNavigator, navController = navController)

            FlexiExpensesManagerTheme {
                NavHost(navController = navController, startDestination = AuthRoutes.ROOT) {
                    createDestinations(applicationContext).forEach {
                        with(it) { buildGraph() }
                    }
                }
            }
        }
    }

    private fun createDestinations(appContext: Context): List<DestinationBuilder> {
        return listOf(
            AuthDestinationBuilder(appContext = appContext)
        )
    }
}