package com.charmflex.flexiexpensesmanager.features.home.ui.account

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper.AccountHomeUIMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountHomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountHomeUIMapper: AccountHomeUIMapper
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
                        accountsSummary = accountHomeUIMapper.map(summary)
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
    val accountsSummary: List<AccountGroupSummaryUI> = listOf(),
    val isLoading: Boolean = false
) {
    internal data class AccountGroupSummaryUI(
        val accountGroupName: String,
        val accountsSummary: List<AccountSummaryUI>,
        val balance: String,
        val textColor: Color
    ) {
        data class AccountSummaryUI(
            val accountId: Int,
            val accountName: String,
            val balance: String,
            val textColor: Color
        )
    }
}