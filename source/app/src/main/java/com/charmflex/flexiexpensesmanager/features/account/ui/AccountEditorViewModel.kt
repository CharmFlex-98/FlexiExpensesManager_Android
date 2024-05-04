package com.charmflex.flexiexpensesmanager.features.account.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountEditorViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {

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
        if (_viewState.value.selectedAccountGroup == null) {
            routeNavigator.pop()
        } else {
            _viewState.update {
                it.copy(
                    selectedAccountGroup = null,
                    editorState = AccountEditorViewState.EditorState(isEditorOpened = false)
                )
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
                editorState = it.editorState.copy(
                    isEditorOpened = open,
                    type = it.selectedAccountGroup?.let { AccountEditorViewState.Type.ACCOUNT }
                        ?: AccountEditorViewState.Type.ACCOUNT_GROUP,
                    value = if (!open) "" else it.editorState.value
                )
            )
        }
    }

    fun updateEditorValue(newValue: String) {
        _viewState.update {
            it.copy(
                editorState = it.editorState.copy(
                    value = newValue
                )
            )
        }
    }

    fun addNewItem() {
        if (_viewState.value.isSubGroupEditor) addNewSubGroup()
        else if (_viewState.value.isAccountEditor) addNewAccount()
    }

    private fun addNewSubGroup() {
        val name = _viewState.value.editorState.value
        viewModelScope.launch {
            resultOf {
                accountRepository.addAccountGroup(name)
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
        val name = _viewState.value.editorState.value
        val selectedAccountGroupId = _viewState.value.selectedAccountGroup?.accountGroupId
        selectedAccountGroupId?.let {
            viewModelScope.launch {
                resultOf {
                    accountRepository.addAccount(name, it)
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
    }
}

internal data class AccountEditorViewState(
    val isLoading: Boolean = false,
    val accountGroups: List<AccountGroup> = listOf(),
    val selectedAccountGroup: AccountGroup? = null,
    val editorState: EditorState = EditorState(),
    val deleteDialogState: DeleteDialogState? = null
) {
    val isSubGroupEditor get() = editorState.type == Type.ACCOUNT_GROUP && editorState.isEditorOpened
    val isAccountEditor get() = editorState.type == Type.ACCOUNT && editorState.isEditorOpened

    data class EditorState(
        val isEditorOpened: Boolean = false,
        val value: String = "",
        val type: Type = Type.ACCOUNT_GROUP
    )

    enum class Type {
        ACCOUNT_GROUP, ACCOUNT
    }

    data class DeleteDialogState(
        val id: Int,
        val type: Type
    )
}