package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import androidx.lifecycle.ViewModel
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import javax.inject.Inject

internal class SettingViewModel @Inject constructor(
    val routeNavigator: RouteNavigator,
) : ViewModel() {

    fun onTap(settingAction: SettingAction) {
        val type = when (settingAction) {
            SettingAction.EXPENSES_CAT -> {
                routeNavigator.navigateTo(CategoryRoutes.editorDestination(TransactionType.EXPENSES))
            }
            SettingAction.INCOME_CAT -> {
                routeNavigator.navigateTo(CategoryRoutes.editorDestination(TransactionType.INCOME))
            }
            SettingAction.ACCOUNT -> {
                routeNavigator.navigateTo(AccountRoutes.editorDestination())
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
            )
        )
    }
}

internal data class SettingActionable(
    val title: String,
    val action: SettingAction
)
internal enum class SettingAction {
    EXPENSES_CAT, INCOME_CAT, ACCOUNT
}