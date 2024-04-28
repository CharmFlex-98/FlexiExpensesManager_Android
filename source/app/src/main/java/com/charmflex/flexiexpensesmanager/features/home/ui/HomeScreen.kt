package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.di.AppComponent
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.history.ExpensesHistoryScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_heat_map.ExpensesHeatMapPlugin
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_pie_chart.ExpensesPieChartDashboardPlugin
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavItem
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavigationBar
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.example.compose.FlexiExpensesManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    appComponent: AppComponent
) {
    val homeViewmodel = getViewModel {
        appComponent.homeViewModel()
    }
    val expensesPieChartViewModel = getViewModel {
        appComponent.expensesPieChartViewModel()
    }
    val expensesHeatMapViewModel = getViewModel {
        appComponent.expensesHeatMapViewModel()
    }
    val dashboardPlugins = listOf(
        ExpensesPieChartDashboardPlugin(expensesPieChartViewModel),
        ExpensesHeatMapPlugin(expensesHeatMapViewModel)
    )

    val bottomNavController = rememberNavController()
    SGScaffold(
        modifier = Modifier.padding(grid_x2),
        bottomBar = { HomeScreenBottomNavigationBar(bottomBarNavController = bottomNavController) },
        floatingActionButton = {
            FloatingActionButton(onClick = homeViewmodel::createNewExpenses) {
                SGIcons.Add()
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        NavHost(
            navController = bottomNavController,
            startDestination = HomeRoutes.SUMMARY
        ) {
            composable(
                route = HomeRoutes.SUMMARY,
            ) {
                DashboardScreen(plugins = dashboardPlugins)
            }
            composable(
                route = HomeRoutes.DETAIL,
            ) {
                val viewModel = getViewModel {
                    appComponent.expensesHistoryViewModel()
                }
                ExpensesHistoryScreen(expensesHistoryViewModel = viewModel)
            }
            composable(
                route = HomeRoutes.ACCOUNTS,
            ) {
                val viewModel = getViewModel {
                    appComponent.accountHomeViewModel()
                }
                AccountHomeScreen(viewModel = viewModel)
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
    val currentBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val item = remember { bottomBarItem() }
    SGBottomNavigationBar(
        tonalElevation = 0.dp,
        items = item,
        isSelected = {
            currentBackStackEntry?.destination?.route == it
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
            route = HomeRoutes.SUMMARY,
        ),
        SGBottomNavItem(
            index = 1,
            titleId = R.string.home_bar_item_details,
            iconId = R.drawable.ic_wallet,
            route = HomeRoutes.DETAIL
        ),
        SGBottomNavItem(
            index = 2,
            titleId = R.string.home_bar_item_accounts,
            iconId = R.drawable.ic_wallet,
            route = HomeRoutes.ACCOUNTS
        )
    )
}