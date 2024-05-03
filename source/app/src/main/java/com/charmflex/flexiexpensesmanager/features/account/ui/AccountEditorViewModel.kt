package com.charmflex.flexiexpensesmanager.features.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
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

    private val _viewState = MutableStateFlow(AccountEditorViewState())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collectLatest { accGroups ->
                toggleLoader(true)
                _viewState.update {
                    it.copy(
                        accountGroups = accGroups
                    )
                }
                toggleLoader(false)
            }
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
                    type = it.selectedAccountGroup?.let { AccountEditorViewState.EditorState.Type.ACCOUNT }
                        ?: AccountEditorViewState.EditorState.Type.SUBGROUP
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
                    routeNavigator.pop()
                },
                onFailure = {}
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
                        routeNavigator.pop()
                    },
                    onFailure = {}
                )
            }
        }
    }
}

internal data class AccountEditorViewState(
    val isLoading: Boolean = false,
    val accountGroups: List<AccountGroup> = listOf(),
    val selectedAccountGroup: AccountGroup? = null,
    val editorState: EditorState = EditorState()
) {
    val isSubGroupEditor get() = editorState.type == EditorState.Type.SUBGROUP && editorState.isEditorOpened
    val isAccountEditor get() = editorState.type == EditorState.Type.ACCOUNT && editorState.isEditorOpened

    data class EditorState(
        val isEditorOpened: Boolean = false,
        val value: String = "",
        val type: Type = Type.SUBGROUP
    ) {

        enum class Type {
            SUBGROUP, ACCOUNT
        }
    }
}