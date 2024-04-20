package com.charmflex.flexiexpensesmanager.features.home.ui

import android.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.features.home.ui.detail.ExpensesDetailScreen
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.SummaryScreen
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavItem
import com.charmflex.flexiexpensesmanager.ui_common.SGBottomNavigationBar
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x16
import com.charmflex.flexiexpensesmanager.ui_common.grid_x4
import com.charmflex.flexiexpensesmanager.ui_common.grid_x8
import com.example.compose.FlexiExpensesManagerTheme

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel
) {
    val bottomNavController = rememberNavController()
    SGScaffold(
        bottomBar = { HomeScreenBottomNavigationBar(bottomBarNavController = bottomNavController) },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::createNewExpenses) {
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
        )
    )
}