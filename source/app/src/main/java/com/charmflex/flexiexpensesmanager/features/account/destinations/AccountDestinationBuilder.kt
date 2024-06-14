package com.charmflex.flexiexpensesmanager.features.account.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.FEVerticalSlideUp
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.account.ui.AccountEditorScreen
import com.charmflex.flexiexpensesmanager.features.account.ui.account_detail.AccountDetailScreen

internal class AccountDestinationBuilder : DestinationBuilder {
    private val appComponent = AppComponentProvider.instance.getAppComponent()
    override fun NavGraphBuilder.buildGraph() {
        accountEditor()
        accountDetailScreen()
    }

    private fun NavGraphBuilder.accountEditor() {
        composable(
            AccountRoutes.EDITOR,
            arguments = listOf(
                navArgument(
                    AccountRoutes.Args.IMPORT_FIX_ACCOUNT_NAME,
                ) {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) {
            val importFixAccountName = it.arguments?.getString(AccountRoutes.Args.IMPORT_FIX_ACCOUNT_NAME)
            val viewModel = getViewModel {
                appComponent.accountEditorViewModel().apply {
                    initFlow(importFixAccountName)
                }
            }
            AccountEditorScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.accountDetailScreen() {
        composable(
            AccountRoutes.DETAIL,
            enterTransition = FEVerticalSlideUp,
            arguments = listOf(
                navArgument(
                    AccountRoutes.Args.ACCOUNT_ID,
                ) {
                    nullable = false
                    type = NavType.IntType
                }
            )
        ) {
            val accountId = it.arguments?.getInt(AccountRoutes.Args.ACCOUNT_ID) ?: -1
            val accountTransactionHistoryViewModel = getViewModel {
                appComponent.accountTransactionHistoryViewModelFactory().create(accountId)
            }
            val accountDetailViewModel = getViewModel {
                appComponent.accountDetailViewModelFactory().create(accountId = accountId)
            }
            AccountDetailScreen(accountTransactionHistoryViewModel, accountDetailViewModel)
        }
    }
}