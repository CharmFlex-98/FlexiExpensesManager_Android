package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.charmflex.flexiexpensesmanager.features.home.ui.history.TransactionHistoryScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map.ExpensesHeatMapPlugin
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart.ExpensesPieChartDashboardPlugin
import com.charmflex.flexiexpensesmanager.features.home.ui.setting.SettingScreen
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavItem
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavigationBar
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.example.compose.FlexiExpensesManagerTheme

@Composable
internal fun HomeScreen(
    homeViewModel: HomeViewModel,
    shouldRefresh: Boolean = false,
    appComponent: AppComponent,
) {
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
        bottomBar = { HomeScreenBottomNavigationBar(bottomBarNavController = bottomNavController) },
        floatingActionButton = {
            FloatingActionButton(onClick = homeViewModel::createNewExpenses) {
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
                val viewModel = getViewModel {
                    appComponent.dashBoardViewModel().apply {
                        initPlugins(
                            dashboardPlugins
                        )
                    }
                }

                LaunchedEffect(key1 = shouldRefresh) {
                    if (shouldRefresh) viewModel.refresh()
                }
                DashboardScreen(viewModel)
            }
            composable(
                route = HomeRoutes.DETAIL,
            ) {
                val viewModel = getViewModel {
                    appComponent.expensesHistoryViewModelFactory().create(null)
                }
                LaunchedEffect(shouldRefresh) {
                    if (shouldRefresh) {
                        viewModel.refresh()
                    }
                }

                TransactionHistoryScreen(transactionHistoryViewModel = viewModel)
            }
            composable(
                route = HomeRoutes.ACCOUNTS,
            ) {
                val viewModel = getViewModel {
                    appComponent.accountHomeViewModel()
                }
                LaunchedEffect(key1 = shouldRefresh) {
                    if (shouldRefresh) {
                        viewModel.refresh()
                    }
                }
                AccountHomeScreen(viewModel = viewModel)
            }
            composable(
                route = HomeRoutes.SETTING,
            ) {
                val viewModel = getViewModel {
                    appComponent.settingViewModel()
                }
                SettingScreen(viewModel = viewModel)
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
        ),
        SGBottomNavItem(
            index = 3,
            titleId = R.string.home_bar_item_setting,
            iconId = R.drawable.icon_people,
            route = HomeRoutes.SETTING
        )
    )
}