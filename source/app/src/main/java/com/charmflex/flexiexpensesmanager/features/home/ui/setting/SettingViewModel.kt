package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupManager
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TagRoutes
import com.charmflex.flexiexpensesmanager.core.utils.FEFileProvider
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class SettingViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val transactionBackupManager: TransactionBackupManager,
    private val FEFileProvider: FEFileProvider,
) : ViewModel() {

    private val _viewState = MutableStateFlow(SettingViewState())
    val viewState = _viewState.asStateFlow()

    var snackBarState = mutableStateOf<SnackBarState>(SnackBarState.None)
        private set

    init {
        _viewState.update {
            it.copy(
                isLoading = false,
                items = getSettingActionable()
            )
        }
    }

    fun onCreatedCompleted(fileName: String) {
        viewModelScope.launch {
            transactionBackupManager.share(fileName)
        }
    }

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
            SettingAction.Tag -> {
                routeNavigator.navigateTo(TagRoutes.addNewTagDestination())
            }
            SettingAction.Export -> {
                viewModelScope.launch {
                    toggleLoader(true)
                    resultOf {
                        transactionBackupManager.write("test_export.xlsx")
                    }.fold(
                        onSuccess = {
                            Log.d("test", "done exporting")
                            toggleLoader(false)
                            snackBarState.value = SnackBarState.Success("Done exporting")
                            onCreatedCompleted("test_export.xlsx")
                        },
                        onFailure = {
                            Log.d("test", "fail exporting")
                            toggleLoader(false)
                            snackBarState.value = SnackBarState.Error("Error exporting: ${it.message}")
                        }
                    )
                }
            }
            SettingAction.Import -> {
                routeNavigator.navigateTo(BackupRoutes.IMPORT_SETTING)
            }
        }
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private fun getSettingActionable(): List<SettingActionable> {
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
            ),
            SettingActionable(
                title = "Set tag",
                action = SettingAction.Tag
            ),
            SettingActionable(
                title = "Export",
                action = SettingAction.Export
            ),
            SettingActionable(
                title = "Import",
                action = SettingAction.Import
            )
        )
    }
}

internal data class SettingViewState(
    val isLoading: Boolean = false,
    val items: List<SettingActionable> = listOf(),
)

internal data class SettingActionable(
    val title: String,
    val action: SettingAction
)
internal enum class SettingAction {
    EXPENSES_CAT, INCOME_CAT, ACCOUNT, PRIMARY_CURRENCY, SECONDARY_CURRENCY, Tag, Export, Import
}