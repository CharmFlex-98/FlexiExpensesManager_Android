package com.charmflex.flexiexpensesmanager.features.home.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.features.account.AccountHiderService
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeItemRefreshable
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper.AccountHomeUIMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Maybe can remove the mapper?
internal class AccountHomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountHomeUIMapper: AccountHomeUIMapper,
    private val currencyFormatter: CurrencyFormatter,
    private val userCurrencyRepository: UserCurrencyRepository,
    private val routeNavigator: RouteNavigator,
    private val accountHiderService: AccountHiderService
) : ViewModel(), HomeItemRefreshable {
    private var job: Job = SupervisorJob()
        get() {
            if (field.isCancelled) field = SupervisorJob()
            return field
        }

    private val _viewState = MutableStateFlow(AccountHomeViewState())
    val viewState = _viewState.asStateFlow()

    private val _dateFilter = MutableStateFlow<DateFilter>(DateFilter.All)
    val dateFilter = _dateFilter.asStateFlow()

    init {
        observeDateFilterChanged()
        load()
    }

    private fun observeDateFilterChanged() {
        viewModelScope.launch {
            dateFilter.drop(1).collectLatest {
                load()
            }
        }
    }

    fun onDateFilterChanged(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }

    private fun load() {
        toggleLoading(true)
        val startDate = _dateFilter.value.getStartDate()
        val endDate = _dateFilter.value.getEndDate()
        viewModelScope.launch(job) {
            val mainCurrency = userCurrencyRepository.getPrimaryCurrency()
            accountRepository.getAccountsSummary(startDate = startDate, endDate = endDate).collectLatest { summary ->
                _viewState.update {
                    val summary = accountHomeUIMapper.map(summary to mainCurrency)
                    val totalAsset = summary.map { it.balanceInCent }.reduceOrNull { acc, l -> acc + l }
                    it.copy(
                        accountsSummary = summary,
                        totalAsset = currencyFormatter.format(totalAsset ?: 0, userCurrencyRepository.getPrimaryCurrency()),
                        hideInfo = accountHiderService.isAccountHidden()
                    )
                }
                toggleLoading(false)
            }
        }
    }

    override fun refreshHome() {
        load()
    }

    fun onAccountClick(accountSummaryUI: AccountHomeViewState.AccountGroupSummaryUI.AccountSummaryUI) {
        val args = mapOf(AccountRoutes.Args.ACCOUNT_DETAIL_DATE_FILTER to _dateFilter.value)
        routeNavigator.navigateTo(AccountRoutes.accountDetailDestination(accountSummaryUI.accountId), args)
    }

    fun toggleHideInfo() {
        val shouldHide = !_viewState.value.hideInfo
        viewModelScope.launch {
            accountHiderService.toggleHideAccount(shouldHide)
            _viewState.update {
                it.copy(
                    hideInfo = shouldHide
                )
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
    val totalAsset: String = "",
    val isLoading: Boolean = false,
    val hideInfo: Boolean = false
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