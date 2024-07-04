package com.charmflex.flexiexpensesmanager.features.budget.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BudgetRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.budget.ui.setting.BudgetSettingScreen
import com.charmflex.flexiexpensesmanager.features.budget.ui.setting.BudgetSettingViewModel
import com.charmflex.flexiexpensesmanager.features.budget.ui.stats.BudgetDetailScreen
import com.charmflex.flexiexpensesmanager.features.budget.ui.stats.BudgetDetailViewModel

internal class BudgetDestinationBuilder : DestinationBuilder {
    private val appComponent = AppComponentProvider.instance.getAppComponent()

    override fun NavGraphBuilder.buildGraph() {
        budgetSetting()
        budgetDetail()
    }

    private fun NavGraphBuilder.budgetSetting() {
        composable(BudgetRoutes.budgetSettingRoute) {
            val viewModel: BudgetSettingViewModel = getViewModel {
                appComponent.budgetSettingViewModel()
            }
            BudgetSettingScreen(budgetSettingViewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.budgetDetail() {
        composable(BudgetRoutes.budgetDetailRoute) {
            val viewModel: BudgetDetailViewModel = getViewModel {
                appComponent.budgetDetailViewModel()
            }
            BudgetDetailScreen(viewModel = viewModel)
        }
    }
}