package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupManager
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TagRoutes
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.backup.AppDataClearServiceType
import com.charmflex.flexiexpensesmanager.features.backup.AppDataService
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class SettingViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val transactionBackupManager: TransactionBackupManager,
    private val appDataService: AppDataService
) : ViewModel() {

    private val _onDataClearedEvent: MutableSharedFlow<OnDataCleared> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val onDataClearedEvent = _onDataClearedEvent.asSharedFlow()

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
                            snackBarState.value =
                                SnackBarState.Error("Error exporting: ${it.message}")
                        }
                    )
                }
            }

            SettingAction.Import -> {
                routeNavigator.navigateTo(BackupRoutes.IMPORT_SETTING)
            }

            SettingAction.RESET_DATA -> {
                _viewState.update {
                    it.copy(
                        dialogState = SettingDialogState.ResetDataDialogState(
                            selection = null
                        )
                    )
                }
            }
        }
    }

    fun toggleResetSelection(selection: SettingDialogState.ResetDataDialogState.ResetType) {
        val resetDataDialogState =
            _viewState.value.dialogState as? SettingDialogState.ResetDataDialogState
        resetDataDialogState?.let { dialogState ->
            _viewState.update {
                it.copy(
                    dialogState = dialogState.copy(
                        selection = selection
                    )
                )
            }
        }
    }

    fun closeDialog() {
        _viewState.update {
            it.copy(
                dialogState = null
            )
        }
    }

    fun onFactoryResetConfirmed() {
        val selection =
            (_viewState.value.dialogState as? SettingDialogState.ResetDataDialogState)?.selection
        closeDialog()

        selection?.let {
            when (it) {
                SettingDialogState.ResetDataDialogState.ResetType.TRANSACTION_ONLY -> clearAllTransactions()
                else -> clearAllData()
            }
        }
    }

    private fun clearAllTransactions() {
        viewModelScope.launch {
            toggleLoader(true)
            appDataService.clearAppData(AppDataClearServiceType.TRANSACTION_ONLY)
            toggleLoader(false)
            _onDataClearedEvent.tryEmit(OnDataCleared.Refresh)
        }
    }

    private fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoader(true)
            resultOf {
                appDataService.clearAppData(AppDataClearServiceType.ALL)
            }.fold(
                onSuccess = {
                    _onDataClearedEvent.tryEmit(OnDataCleared.Finish)
                },
                onFailure = {
                    // TODO
                }
            )
            toggleLoader(false)
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
            ),
            SettingActionable(
                title = "Reset Data",
                action = SettingAction.RESET_DATA
            )
        )
    }
}

internal data class SettingViewState(
    val isLoading: Boolean = false,
    val dialogState: SettingDialogState? = null,
    val items: List<SettingActionable> = listOf(),
)

internal sealed interface SettingDialogState {
    @get:StringRes
    val title: Int

    @get:StringRes
    val subtitle: Int

    data class ResetDataDialogState(
        override val title: Int = R.string.setting_factory_reset_title,
        override val subtitle: Int = R.string.setting_factory_reset_dialog_subtitle,
        val selection: ResetType?
    ) : SettingDialogState {
        enum class ResetType {
            TRANSACTION_ONLY, ALL
        }
    }
}

internal sealed interface OnDataCleared {
    object Refresh : OnDataCleared
    object Finish : OnDataCleared
}

internal data class SettingActionable(
    val title: String,
    val action: SettingAction
)

internal enum class SettingAction {
    EXPENSES_CAT, INCOME_CAT, ACCOUNT, PRIMARY_CURRENCY, SECONDARY_CURRENCY, Tag, Export, Import, RESET_DATA
}