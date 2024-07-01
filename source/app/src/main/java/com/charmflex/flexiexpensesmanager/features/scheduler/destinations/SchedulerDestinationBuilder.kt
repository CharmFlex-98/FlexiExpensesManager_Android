package com.charmflex.flexiexpensesmanager.features.scheduler.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.SchedulerRoutes.SCHEDULER_LIST
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.scheduler.ui.schedulerList.SchedulerListScreen

internal class SchedulerDestinationBuilder : DestinationBuilder {
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }
    override fun NavGraphBuilder.buildGraph() {
        schedulerListScreen()
    }

    private fun NavGraphBuilder.schedulerListScreen() {
        composable(
            route = SCHEDULER_LIST
        ) {
            val viewModel = getViewModel {
                appComponent.schedulerListViewModel()
            }
            SchedulerListScreen(transactionSchedulerListViewModel = viewModel)
        }
    }
}