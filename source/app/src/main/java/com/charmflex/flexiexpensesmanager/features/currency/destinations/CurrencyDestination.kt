package com.charmflex.flexiexpensesmanager.features.currency.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.currency.ui.CurrencySettingScreen

internal class CurrencyDestination : DestinationBuilder {
    private val appComponent = AppComponentProvider.instance.getAppComponent()
    override fun NavGraphBuilder.buildGraph() {
        currencySetting()
    }

    private fun NavGraphBuilder.currencySetting() {
        composable(CurrencyRoutes.SETTING) {
            val viewModel = getViewModel {
                appComponent.currencySettingViewModel()
            }

            CurrencySettingScreen(viewModel = viewModel)
        }
    }
}