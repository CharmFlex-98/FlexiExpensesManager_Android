package com.charmflex.flexiexpensesmanager.features.home.ui

import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
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
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.history.TransactionHistoryHomeScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.history.TransactionHomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map.ExpensesHeatMapPlugin
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart.ExpensesPieChartDashboardPlugin
import com.charmflex.flexiexpensesmanager.features.home.ui.setting.SettingScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.setting.SettingViewModel
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavItem
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavigationBar
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.example.compose.FlexiExpensesManagerTheme

@Composable
internal fun HomeScreen(
    appComponent: AppComponent,
    refreshOnHomeShown: Boolean
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

    // Dashboard
    val dashboardViewModel = getViewModel {
        appComponent.dashBoardViewModel().apply {
            initPlugins(
                dashboardPlugins
            )
        }
    }

    // Transaction History
    val transactionHomeViewModel = getViewModel {
        appComponent.expensesHistoryViewModel()
    }

    // Account Home
    val accountHomeViewModel = getViewModel {
        appComponent.accountHomeViewModel()
    }

    // Setting
    val settingViewModel = getViewModel {
        appComponent.settingViewModel()
    }

    val homeViewModel: HomeViewModel = getViewModel {
        appComponent.homeViewModel().apply {
            initHomeRefreshable(dashboardViewModel, transactionHomeViewModel, accountHomeViewModel)
        }
    }

    if (refreshOnHomeShown) {
        homeViewModel.notifyRefresh()
    }

    HomeBody(
        homeViewModel,
        dashboardViewModel,
        transactionHomeViewModel,
        accountHomeViewModel,
        settingViewModel
    )
}

@Composable
private fun HomeBody(
    homeViewModel: HomeViewModel,
    dashboardViewModel: DashboardViewModel,
    transactionHomeViewModel: TransactionHomeViewModel,
    accountHomeViewModel: AccountHomeViewModel,
    settingViewModel: SettingViewModel
) {
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
                DashboardScreen(dashboardViewModel)
            }
            composable(
                route = HomeRoutes.DETAIL,
            ) {
                TransactionHistoryHomeScreen(transactionHomeViewModel = transactionHomeViewModel)
            }
            composable(
                route = HomeRoutes.ACCOUNTS,
            ) {
                AccountHomeScreen(viewModel = accountHomeViewModel)
            }
            composable(
                route = HomeRoutes.SETTING,
            ) {
                SettingScreen(viewModel = settingViewModel) {
                    homeViewModel.notifyRefresh()
                }
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