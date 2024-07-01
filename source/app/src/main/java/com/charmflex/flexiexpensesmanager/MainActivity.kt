package com.charmflex.flexiexpensesmanager

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigatorListener
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AuthRoutes
import com.charmflex.flexiexpensesmanager.features.account.destinations.AccountDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.auth.destination.AuthDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.backup.destination.BackupDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.category.category.destinations.CategoryDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.currency.destinations.CurrencyDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.transactions.destination.TransactionDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.home.destination.HomeDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.scheduler.destinations.SchedulerDestinationBuilder
import com.charmflex.flexiexpensesmanager.features.session.SessionManager
import com.charmflex.flexiexpensesmanager.features.session.SessionState
import com.charmflex.flexiexpensesmanager.features.tag.destination.TagDestinationBuilder
import com.example.compose.FlexiExpensesManagerTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (application as AppComponentProvider).getAppComponent()
        val routeNavigator = appComponent.routeNavigator()
        appComponent.sessionManager().updateSessionState(SessionState.Start)

        // Test

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()

            BackHandler {
                if (navController.popBackStack().not()) {
                    (context as? Activity)?.finish()
                }
            }

            RouteNavigatorListener(routeNavigator = routeNavigator, navController = navController)

            FlexiExpensesManagerTheme {
                NavHost(navController = navController, startDestination = AuthRoutes.ROOT) {
                    createDestinations(navController).forEach {
                        with(it) { buildGraph() }
                    }
                }
            }
        }
    }
}

private fun createDestinations(navController: NavController): List<DestinationBuilder> {
    return listOf(
        AuthDestinationBuilder(),
        HomeDestinationBuilder(),
        TransactionDestinationBuilder(),
        CategoryDestinationBuilder(navController),
        AccountDestinationBuilder(navController),
        CurrencyDestinationBuilder(),
        TagDestinationBuilder(),
        BackupDestinationBuilder(),
        SchedulerDestinationBuilder()
    )
}