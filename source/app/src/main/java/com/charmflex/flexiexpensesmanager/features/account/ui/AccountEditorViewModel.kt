package com.charmflex.flexiexpensesmanager.features.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountEditorViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator
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
                        selectedAccountGroup = it.selectedAccountGroup?.let { accGroup -> accGroups.firstOrNull { it.accountGroupId == accGroup.accountGroupId } }
                    )
                }
                toggleLoader(false)
            }
        }
    }

    fun initFlow(importFixAccountName: String?) {
        _flowType =
            if (importFixAccountName != null) AccountEditorFlow.ImportFix(importFixAccountName) else AccountEditorFlow.Default
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

    fun deleteItem() {
        val id = _viewState.value.deleteDialogState?.id ?: return
        val type = _viewState.value.deleteDialogState?.type ?: return

        when (type) {
            AccountEditorViewState.Type.ACCOUNT -> deleteAccount(id)
            AccountEditorViewState.Type.ACCOUNT_GROUP -> deleteAccountGroup(id)
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
        if (_viewState.value.isAccountEditor || _viewState.value.isAccountGroupEditor) {
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
        val shouldToggleAccountEditor = _viewState.value.selectedAccountGroup != null
        _viewState.update {
            it.copy(
                editorState = if (open) {
                    if (shouldToggleAccountEditor) AccountEditorViewState.AccountEditorState(
                        accountName = when (val flow = _flowType) {
                            is AccountEditorFlow.ImportFix -> flow.name
                            else -> ""
                        }
                    )
                    else AccountEditorViewState.AccountGroupEditorState()
                } else null
            )
        }
    }

    fun updateAccountName(newValue: String) {
        _viewState.update {
            it.copy(
                editorState = when (val vs = it.editorState) {
                    is AccountEditorViewState.AccountEditorState -> vs.copy(accountName = newValue)
                    is AccountEditorViewState.AccountGroupEditorState -> vs.copy(accountGroupName = newValue)
                    else -> vs
                }
            )
        }
    }

    fun updateInitialAmount(newValue: String) {
        _viewState.update {
            it.copy(
                editorState = when (val vs = it.editorState) {
                    is AccountEditorViewState.AccountEditorState -> vs.copy(initialValue = newValue)
                    else -> vs
                }
            )
        }
    }

    fun addNewItem() {
        if (_viewState.value.isAccountGroupEditor) addNewSubGroup()
        else if (_viewState.value.isAccountEditor) addNewAccount()
    }

    private fun addNewSubGroup() {
        val accountGroupEditor =
            _viewState.value.editorState as? AccountEditorViewState.AccountGroupEditorState
                ?: return

        viewModelScope.launch {
            resultOf {
                accountRepository.addAccountGroup(accountGroupEditor.accountGroupName)
            }.fold(
                onSuccess = {
                    _snackBarState.emit(SnackBarState.Success("Add success!"))
                    toggleEditor(false)
                },
                onFailure = {
                    _snackBarState.emit(SnackBarState.Success("Add failed!"))
                    toggleEditor(false)
                }
            )
        }

    }

    private fun addNewAccount() {
        val accountEditorState =
            _viewState.value.editorState as? AccountEditorViewState.AccountEditorState
                ?: return
        val selectedAccountGroupId = _viewState.value.selectedAccountGroup?.accountGroupId ?: return

        viewModelScope.launch {
            resultOf {
                accountRepository.addAccount(
                    accountEditorState.accountName,
                    selectedAccountGroupId,
                    accountEditorState.initialValue.toLong()
                )
            }.fold(
                onSuccess = {
                    _snackBarState.emit(SnackBarState.Success("Add success!"))
                    if (_flowType is AccountEditorFlow.ImportFix) {
                        routeNavigator.popWithArguments(
                            mapOf(BackupRoutes.Args.UPDATE_IMPORT_DATA to true)
                        )
                    }
                },
                onFailure = {
                    _snackBarState.emit(SnackBarState.Success("Add failed!"))
                    toggleEditor(false)
                }
            )
        }
    }
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
    val deleteDialogState: DeleteDialogState? = null
) {
    val isAccountGroupEditor get() = editorState is AccountGroupEditorState
    val isAccountEditor get() = editorState is AccountEditorState

    sealed interface EditorState
    data class AccountGroupEditorState(
        val accountGroupName: String = ""
    ) : EditorState

    data class AccountEditorState(
        val accountName: String = "",
        val initialValue: String = "0",
    ) : EditorState

    enum class Type {
        ACCOUNT_GROUP, ACCOUNT
    }

    data class DeleteDialogState(
        val id: Int,
        val type: Type
    )
}