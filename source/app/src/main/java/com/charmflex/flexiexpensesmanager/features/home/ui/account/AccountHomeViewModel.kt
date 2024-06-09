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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Maybe can remove the mapper?
internal class AccountHomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountHomeUIMapper: AccountHomeUIMapper,
    private val currencyFormatter: CurrencyFormatter,
    private val userCurrencyRepository: UserCurrencyRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(AccountHomeViewState())
    val viewState = _viewState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            accountRepository.getAccountsSummary().firstOrNull()?.let { summary ->
                toggleLoading(true)
                _viewState.update {
                    val summary = accountHomeUIMapper.map(summary)
                    val totalAsset = summary.map { it.balanceInCent }.reduceOrNull { acc, l -> acc + l }
                    it.copy(
                        accountsSummary = summary,
                        totalAsset = currencyFormatter.format(totalAsset ?: 0, userCurrencyRepository.getPrimaryCurrency())
                    )
                }
                toggleLoading(false)
            }
        }
    }

    fun refresh() {
        load()
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
    val totalAsset: String = "",
    val isLoading: Boolean = false,
) {
    internal data class AccountGroupSummaryUI(
        val accountGroupName: String,
        val accountsSummary: List<AccountSummaryUI>,
        val balance: String,
        val balanceInCent: Long,
    ) {
        data class AccountSummaryUI(
            val accountId: Int,
            val accountName: String,
            val balance: String,
            val balanceInCent: Long,
        )
    }
}