package com.charmflex.flexiexpensesmanager.features.account.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.ResourcesProvider
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.core.utils.unwrapResult
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.service.CurrencyService
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyUseCase
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountEditorViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator,
    private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
    private val currencyUseCase: GetCurrencyUseCase,
    private val resourcesProvider: ResourcesProvider,
    private val currencyService: CurrencyService
) : ViewModel() {
    private lateinit var _flowType: AccountEditorFlow

    private val _snackBarState = MutableStateFlow<SnackBarState>(SnackBarState.None)
    val snackBarState = _snackBarState.asStateFlow()

    private val _viewState = MutableStateFlow(AccountEditorViewState())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collectLatest { accGroups ->
                toggleLoader(true)
                _viewState.update {
                    it.copy(
                        accountGroups = accGroups,
                        selectedAccountGroup = it.selectedAccountGroup?.let { accGroup -> accGroups.firstOrNull { it.accountGroupId == accGroup.accountGroupId } },
                        primaryCurrencyCode = unwrapResult(currencyUseCase.primary())?.name ?: ""
                    )
                }
                toggleLoader(false)
            }
        }
    }

    fun getCurrencyVisualTransformer(currencyCode: String): CurrencyVisualTransformation {
        return currencyVisualTransformationBuilder.create(currencyCode)
    }

    fun initFlow(importFixAccountName: String?) {
        _flowType =
            if (importFixAccountName != null) AccountEditorFlow.ImportFix(importFixAccountName) else AccountEditorFlow.Default
    }

    fun resetSnackBarState() {
        _snackBarState.value = SnackBarState.None
    }

    fun resetBottomSheetState() {
        _viewState.update {
            it.copy(
                bottomSheetState = null
            )
        }
    }

    private fun deleteAccountGroup(id: Int) {
        viewModelScope.launch {
            resultOf {
                accountRepository.deleteAccountGroup(id)
            }.fold(
                onSuccess = {
                    _snackBarState.emit(SnackBarState.Success("Delete success"))
                },
                onFailure = {
                    _snackBarState.emit(SnackBarState.Error("Error delete it"))
                }
            )
        }
    }

    private fun deleteAccount(id: Int) {
        viewModelScope.launch {
            resultOf {
                accountRepository.deleteAccount(id)
            }.fold(
                onSuccess = {
                    _snackBarState.emit(SnackBarState.Success("Delete success"))
                },
                onFailure = {
                    _snackBarState.emit(SnackBarState.Error("Error delete it"))
                }
            )
        }
    }

    fun deleteItem(itemId: Int, type: AccountEditorViewState.Type) {
        when (type) {
            AccountEditorViewState.Type.ACCOUNT -> deleteAccount(itemId)
            AccountEditorViewState.Type.ACCOUNT_GROUP -> deleteAccountGroup(itemId)
        }
    }

    fun launchDeleteDialog(id: Int, type: AccountEditorViewState.Type) {
        _viewState.update {
            it.copy(
                deleteDialogState = AccountEditorViewState.DeleteDialogState(
                    id = id,
                    type = type
                )
            )
        }
    }

    fun closeDialog() {
        _viewState.update {
            it.copy(
                deleteDialogState = null
            )
        }
    }

    fun back() {
        if (_viewState.value.isAccountEditor) {
            _viewState.update {
                it.copy(
                    editorState = null
                )
            }
        } else if (_viewState.value.selectedAccountGroup != null) {
            _viewState.update {
                it.copy(
                    selectedAccountGroup = null
                )
            }
        } else routeNavigator.pop()
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    fun selectAccountGroup(accountGroup: AccountGroup) {
        _viewState.update {
            it.copy(
                selectedAccountGroup = accountGroup
            )
        }
    }

    fun toggleEditor(open: Boolean) {
        _viewState.update {
            it.copy(
                editorState = if (open && _viewState.value.selectedAccountGroup != null) {
                    AccountEditorViewState.AccountEditorState(
                        accountName = when (val flow = _flowType) {
                            is AccountEditorFlow.ImportFix -> flow.name
                            else -> ""
                        },
                        currency = viewState.value.primaryCurrencyCode
                    )
                } else null
            )
        }
    }

    fun updateAccountName(newValue: String) {
        _viewState.update {
            it.copy(
                editorState = when (val vs = it.editorState) {
                    is AccountEditorViewState.AccountEditorState -> vs.copy(accountName = newValue)
                    else -> vs
                }
            )
        }
    }

    fun updateInitialAmount(newValue: String) {
        _viewState.update {
            it.copy(
                editorState = when (val vs = it.editorState) {
                    is AccountEditorViewState.AccountEditorState -> vs.copy(amount = newValue)
                    else -> vs
                }
            )
        }
    }

    fun addNewItem() {
        addNewAccount()
    }

    fun onTapField(tapFieldType: TapFieldType) {
        when (tapFieldType) {
            TapFieldType.CurrencyField -> {
                viewModelScope.launch {
                    val currencyCodes = currencyService.getAllCurrencies()
                    _viewState.update {
                        it.copy(
                            bottomSheetState = BottomSheetState.CurrencySelectionState(
                                currencyCodes = currencyCodes.toSet()
                            )
                        )
                    }
                }
            }
        }
    }

    fun onBottomSheetItemSelected(item: String) {
        when (_viewState.value.bottomSheetState) {
            is BottomSheetState.CurrencySelectionState -> {
                val accountEditorState = _viewState.value.editorState as? AccountEditorViewState.AccountEditorState
                accountEditorState?.let {
                    _viewState.update {
                        it.copy(
                            editorState = accountEditorState.copy(currency = item)
                        )
                    }
                }
            }
        }
        resetBottomSheetState()
    }


//    private fun addNewSubGroup() {
//        val accountGroupEditor =
//            _viewState.value.editorState as? AccountEditorViewState.AccountGroupEditorState
//                ?: return
//
//        viewModelScope.launch {
//            resultOf {
//                accountRepository.addAccountGroup(accountGroupEditor.accountGroupName)
//            }.fold(
//                onSuccess = {
//                    _snackBarState.emit(SnackBarState.Success("Add success!"))
//                    toggleEditor(false)
//                },
//                onFailure = {
//                    _snackBarState.emit(SnackBarState.Success("Add failed!"))
//                    toggleEditor(false)
//                }
//            )
//        }
//
//    }

    private fun addNewAccount() {
        val accountEditorState =
            _viewState.value.editorState as? AccountEditorViewState.AccountEditorState
                ?: return
        val selectedAccountGroupId = _viewState.value.selectedAccountGroup?.accountGroupId ?: return
        val currency = accountEditorState.currency;

        viewModelScope.launch {
            resultOf {
                accountRepository.addAccount(
                    accountEditorState.accountName,
                    selectedAccountGroupId,
                    accountEditorState.amount.toLong(),
                    currency,
                )
            }.fold(
                onSuccess = {
                    _snackBarState.emit(
                        SnackBarState.Success(
                            resourcesProvider.getString(
                                R.string.add_account_success_snackbar
                            )
                        )
                    )
                    if (_flowType is AccountEditorFlow.ImportFix) {
                        routeNavigator.popWithArguments(
                            mapOf(BackupRoutes.Args.UPDATE_IMPORT_DATA to true)
                        )
                    } else {
                        toggleEditor(false)
                    }
                },
                onFailure = {
                    Log.d("test", it.toString())
                    _snackBarState.emit(SnackBarState.Error(resourcesProvider.getString(R.string.add_account_failure_snackbar)))
                }
            )
        }
    }
}

internal interface BottomSheetState {
    val title: String
    data class CurrencySelectionState(
        override val title: String = "Select Currency",
        val currencyCodes: Set<String> = setOf()
    ) : BottomSheetState
}

internal sealed interface TapFieldType {
    data object CurrencyField : TapFieldType
}

internal sealed interface AccountEditorFlow {
    object Default : AccountEditorFlow
    data class ImportFix(
        val name: String
    ) : AccountEditorFlow
}

internal data class AccountEditorViewState(
    val isLoading: Boolean = false,
    val accountGroups: List<AccountGroup> = listOf(),
    val selectedAccountGroup: AccountGroup? = null,
    val editorState: EditorState? = null,
    val deleteDialogState: DeleteDialogState? = null,
    val primaryCurrencyCode: String = "",
    val bottomSheetState: BottomSheetState? = null
) {
    val isAccountEditor get() = editorState is AccountEditorState

    sealed interface EditorState
//    data class AccountGroupEditorState(
//        val accountGroupName: String = ""
//    ) : EditorState

    data class AccountEditorState(
        val accountName: String = "",
        val amount: String = "0",
        val currency: String = ""
    ) : EditorState

    enum class Type {
        ACCOUNT_GROUP, ACCOUNT
    }

    data class DeleteDialogState(
        val id: Int,
        val type: Type
    )
}