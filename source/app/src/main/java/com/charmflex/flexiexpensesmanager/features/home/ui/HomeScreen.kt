package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.features.home.ui.detail.ExpensesDetailScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.SummaryScreen
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavItem
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavigationBar
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.example.compose.FlexiExpensesManagerTheme

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel
) {
    val bottomNavController = rememberNavController()
    SGScaffold(
        bottomBar = { HomeScreenBottomNavigationBar(bottomBarNavController = bottomNavController) }
    ) {
        NavHost(
            navController = bottomNavController,
            startDestination = HomeRoutes.SUMMARY
        ) {
            composable(
                route = HomeRoutes.SUMMARY
            ) {
                SummaryScreen()
            }
            composable(
                route = HomeRoutes.DETAIL
            ) {
                ExpensesDetailScreen()
            }
        }
    }
}


@Composable
@Preview
fun HomeScreenPreview() {
    FlexiExpensesManagerTheme {
//        HomeScreen()
    }
}

@Composable
fun HomeScreenBottomNavigationBar(bottomBarNavController: NavController) {
    val item = remember { bottomBarItem() }
    SGBottomNavigationBar(
        items = item,
        isSelected = {
            bottomBarNavController.currentBackStackEntry?.destination?.route == it
        }
    ) {
        bottomBarNavController.navigate(it.route) {
            popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                saveState = true
            }
            // Restore state when re-selecting a previously backstack pop by popUpTo
            restoreState = true

            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
        }
    }
}

internal fun bottomBarItem(): List<SGBottomNavItem> {
    return listOf(
        SGBottomNavItem(
            index = 0,
            titleId = R.string.home_bar_item_summary,
            iconId = R.drawable.ic_pie_chart,
            route = HomeRoutes.SUMMARY
        ),
        SGBottomNavItem(
            index = 1,
            titleId = R.string.home_bar_item_details,
            iconId = R.drawable.ic_wallet,
            route = HomeRoutes.DETAIL
        )
    )
}