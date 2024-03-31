package com.charmflex.flexiexpensesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.core.dependency_injection.MainComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigatorListener
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AuthRoutes
import com.charmflex.flexiexpensesmanager.features.auth.destination.AuthDestinationBuilder
import com.charmflex.flexiexpensesmanager.ui_common.Money3DAnimation
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x30
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
                    createDestinations(navController = navController).forEach {
                        with(it) { buildGraph() }
                    }
                }
            }
        }
    }

    private fun createDestinations(navController: NavController): List<DestinationBuilder> {
        return listOf(
            AuthDestinationBuilder()
        )
    }
}