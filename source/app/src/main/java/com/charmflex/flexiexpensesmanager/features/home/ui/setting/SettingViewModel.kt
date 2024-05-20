package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import androidx.lifecycle.ViewModel
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import javax.inject.Inject

internal class SettingViewModel @Inject constructor(
    val routeNavigator: RouteNavigator,
) : ViewModel() {

    fun onTap(settingAction: SettingAction) {
        when (settingAction) {
            SettingAction.EXPENSES_CAT -> {
                routeNavigator.navigateTo(CategoryRoutes.editorDestination(TransactionType.EXPENSES))
            }
            SettingAction.INCOME_CAT -> {
                routeNavigator.navigateTo(CategoryRoutes.editorDestination(TransactionType.INCOME))
            }
            SettingAction.ACCOUNT -> {
                routeNavigator.navigateTo(AccountRoutes.editorDestination())
            }
            SettingAction.PRIMARY_CURRENCY -> {
                routeNavigator.navigateTo(CurrencyRoutes.currencySettingDestination(CurrencyRoutes.Args.CURRENCY_TYPE_MAIN))
            }
            SettingAction.SECONDARY_CURRENCY -> {
                routeNavigator.navigateTo(CurrencyRoutes.USER_SECONDARY_CURRENCY)
            }
        }
    }

    fun getSettingActionable(): List<SettingActionable> {
        return listOf(
            SettingActionable(
                title = "Set expenses category",
                action = SettingAction.EXPENSES_CAT
            ),
            SettingActionable(
                title = "Set income category",
                action = SettingAction.INCOME_CAT
            ),
            SettingActionable(
                title = "Set account",
                action = SettingAction.ACCOUNT
            ),
            SettingActionable(
                title = "Set primary currency",
                action = SettingAction.PRIMARY_CURRENCY
            ),
            SettingActionable(
                "Set secondary currency",
                action = SettingAction.SECONDARY_CURRENCY
            )
        )
    }
}

internal data class SettingActionable(
    val title: String,
    val action: SettingAction
)
internal enum class SettingAction {
    EXPENSES_CAT, INCOME_CAT, ACCOUNT, PRIMARY_CURRENCY, SECONDARY_CURRENCY
}