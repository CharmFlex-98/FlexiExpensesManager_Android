package com.charmflex.flexiexpensesmanager.features.currency.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider
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
            CurrencyRoutes.USER_SECONDARY_CURRENCY,
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
            CurrencyRoutes.currencySettingRoute,
            arguments = listOf(
                navArgument(
                    CurrencyRoutes.Args.CURRENCY_TYPE
                ) {
                    nullable = false
                    type = NavType.StringType
                }
            ),
            enterTransition = FEHorizontalEnterFromEnd
        ) {
            val type = it.arguments?.getString(CurrencyRoutes.Args.CURRENCY_TYPE) ?: ""
            val viewModel = getViewModel {
                appComponent.currencySettingViewModel().apply {
                    this.initialise(type)
                }
            }

            CurrencySettingScreen(viewModel = viewModel)
        }
    }
}