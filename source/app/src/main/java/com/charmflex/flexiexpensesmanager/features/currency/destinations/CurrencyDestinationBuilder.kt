package com.charmflex.flexiexpensesmanager.features.currency.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.FEHorizontalEnterFromEnd
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.currency.ui.CurrencySettingScreen
import com.charmflex.flexiexpensesmanager.features.currency.ui.UserCurrencyScreen

internal class CurrencyDestinationBuilder : DestinationBuilder {
    private val appComponent = AppComponentProvider.instance.getAppComponent()
    override fun NavGraphBuilder.buildGraph() {
        userSetCurrencyList()
        currencySetting()
    }

    private fun NavGraphBuilder.userSetCurrencyList() {
        composable(
            CurrencyRoutes.USER_CURRENCY,
            enterTransition = FEHorizontalEnterFromEnd
        ) {
            val viewModel = getViewModel {
                appComponent.userCurrencyViewModel()
            }

            UserCurrencyScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.currencySetting() {
        composable(
            CurrencyRoutes.SECONDARY_SETTING,
            enterTransition = FEHorizontalEnterFromEnd
        ) {
            val viewModel = getViewModel {
                appComponent.currencySettingViewModel()
            }

            CurrencySettingScreen(viewModel = viewModel)
        }
    }
}