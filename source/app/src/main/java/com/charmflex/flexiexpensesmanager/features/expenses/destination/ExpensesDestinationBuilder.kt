package com.charmflex.flexiexpensesmanager.features.expenses.destination

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.ExpensesRoute
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.expenses.di.ExpensesComponent
import com.charmflex.flexiexpensesmanager.features.expenses.ui.NewExpensesScreen
import javax.inject.Inject

internal class ExpensesDestinationBuilder @Inject constructor(
    private val appContext: Context
) : DestinationBuilder{
    private val expensesComponent by lazy { ExpensesComponent.build(appContext = appContext) }
    override fun NavGraphBuilder.buildGraph() {
        newExpensesScreen()
    }

    private fun NavGraphBuilder.newExpensesScreen() {
        composable(
            route = ExpensesRoute.NEW_RECORD,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            val viewModel = getViewModel { expensesComponent.newExpensesViewModel() }
            NewExpensesScreen(viewModel = viewModel)
        }
    }
}