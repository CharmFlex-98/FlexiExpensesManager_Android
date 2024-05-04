package com.charmflex.flexiexpensesmanager.features.home.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountHomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(AccountHomeViewState())
    val viewState = _viewState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            accountRepository.getAccountsSummary().collectLatest { summary ->
                toggleLoading(true)
                _viewState.update {
                    it.copy(
                        accountsSummary = summary
                    )
                }
                toggleLoading(false)
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}

internal data class AccountHomeViewState(
    val accountsSummary: List<AccountGroupSummary> = listOf(),
    val isLoading: Boolean = false
) {
    val balance get() = accountsSummary.map { it.balance }.reduce { acc, i -> acc + i }
}