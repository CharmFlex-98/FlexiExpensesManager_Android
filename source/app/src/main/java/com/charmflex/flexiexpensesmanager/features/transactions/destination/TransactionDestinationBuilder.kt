package com.charmflex.flexiexpensesmanager.features.transactions.destination

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.NewExpensesScreen
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail.TransactionDetailScreen

internal class TransactionDestinationBuilder : DestinationBuilder{
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }
    override fun NavGraphBuilder.buildGraph() {
        newExpensesScreen()
        transactionDetailScreen()
    }

    private fun NavGraphBuilder.newExpensesScreen() {
        composable(
            route = TransactionRoute.NEW_RECORD,
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
            val viewModel = getViewModel { appComponent.newTransactionViewModel() }
            NewExpensesScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.transactionDetailScreen() {
        composable(
            route = TransactionRoute.transactionDetail,
            arguments = listOf(
                navArgument(
                    name = TransactionRoute.Args.TRANSACTION_ID,
                ) {
                    nullable = false
                    type = NavType.LongType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            val transactionId = it.arguments?.getLong(TransactionRoute.Args.TRANSACTION_ID) ?: -1
            val viewModel = getViewModel { appComponent.transactionDetailViewModelFactory().create(transactionId = transactionId) }
            TransactionDetailScreen(viewModel = viewModel)
        }
    }
}