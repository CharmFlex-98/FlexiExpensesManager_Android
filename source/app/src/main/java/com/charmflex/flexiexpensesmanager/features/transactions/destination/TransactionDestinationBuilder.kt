package com.charmflex.flexiexpensesmanager.features.transactions.destination

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.FEHorizontalEnterFromEnd
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.TransactionEditorScreen
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail.TransactionDetailScreen
import com.charmflex.flexiexpensesmanager.ui_common.features.SettingEditorScreen

internal class TransactionDestinationBuilder : DestinationBuilder {
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }
    override fun NavGraphBuilder.buildGraph() {
        newTransactionScreen()
        transactionEditorScreen()
        newScheduledTransactionScreen()
        scheduledTransactionEditorScreen()
        transactionDetailScreen()
    }

    private fun NavGraphBuilder.newTransactionScreen() {
        composable(
            route = TransactionRoute.newTransaction,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            val viewModel = getViewModel {
                appComponent.transactionEditorViewModelFactory().create(null)
            }
//            TransactionEditorScreen(viewModel = viewModel)
            val f = FEField("", R.string.generic_cancel, R.string.generic_confirm, type = FEField.FieldType.Text)
            val field = remember { mutableStateOf(f) }
            SettingEditorScreen(
                fields = listOf(field.value),
                appBarTitle = { "Testing" },
                onTextFieldChanged = { newValue, fe ->
                    val newValue = FEField.Value("", newValue)
                    field.value = field.value.copy(valueItem = newValue)
                },
                onBack = { /*TODO*/ }) {
                viewModel.currentTransactionType
            }
        }
    }

    private fun NavGraphBuilder.newScheduledTransactionScreen() {
        composable(
            route = TransactionRoute.newScheduledTransaction,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            val viewModel = getViewModel {
                appComponent.schedulerEditorViewModelFactory().create(null)
            }
            TransactionEditorScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.transactionEditorScreen() {
        composable(
            route = TransactionRoute.transactionEditor,
            arguments = listOf(
                navArgument(TransactionRoute.Args.TRANSACTION_ID) {
                    type = NavType.LongType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            val transactionId = it.arguments?.getLong(TransactionRoute.Args.TRANSACTION_ID) ?: -1
            val viewModel = getViewModel {
                appComponent.transactionEditorViewModelFactory().create(transactionId)
            }
            TransactionEditorScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.scheduledTransactionEditorScreen() {
        composable(
            route = TransactionRoute.scheduledTransactionEditor,
            arguments = listOf(
                navArgument(TransactionRoute.Args.SCHEDULE_TRANSACTION_ID) {
                    type = NavType.LongType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            val scheduledTransactionId = it.arguments?.getLong(TransactionRoute.Args.SCHEDULE_TRANSACTION_ID) ?: -1
            val viewModel = getViewModel {
                appComponent.schedulerEditorViewModelFactory().create(scheduledTransactionId)
            }
            TransactionEditorScreen(viewModel = viewModel)
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
            enterTransition = FEHorizontalEnterFromEnd,
        ) {
            val transactionId = it.arguments?.getLong(TransactionRoute.Args.TRANSACTION_ID) ?: -1
            val viewModel = getViewModel {
                appComponent.transactionDetailViewModelFactory()
                    .create(transactionId = transactionId)
            }
            TransactionDetailScreen(viewModel = viewModel)
        }
    }
}