package com.charmflex.flexiexpensesmanager.features.account.destinations

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.charmflex.flexiexpensesmanager.core.di.AppComponent
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.account.ui.AccountEditorScreen

internal class AccountDestinationBuilder : DestinationBuilder {
    private val appComponent = AppComponentProvider.instance.getAppComponent()
    override fun NavGraphBuilder.buildGraph() {
        accountEditor()
    }

    private fun NavGraphBuilder.accountEditor() {
        composable(AccountRoutes.EDITOR) {
            val viewModel = getViewModel {
                appComponent.accountEditorViewModel()
            }
            AccountEditorScreen(viewModel = viewModel)
        }
    }
}