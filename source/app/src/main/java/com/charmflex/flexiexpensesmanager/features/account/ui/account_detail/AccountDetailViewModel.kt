package com.charmflex.flexiexpensesmanager.features.account.ui.account_detail

import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountDetailViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    routeNavigator: RouteNavigator,
    private val accountId: Int,
    mapper: TransactionHistoryMapper,
    private val transactionRepository: TransactionRepository,
    dateFilter: DateFilter?
) : TransactionHistoryViewModel(mapper, routeNavigator) {
    class Factory @Inject constructor(
        private val accountRepository: AccountRepository,
        private val routeNavigator: RouteNavigator,
        private val transactionRepository: TransactionRepository,
        private val mapper: TransactionHistoryMapper
    ) {
        fun create(accountId: Int, dateFilter: DateFilter?): AccountDetailViewModel {
            return AccountDetailViewModel(accountRepository, routeNavigator, accountId, mapper, transactionRepository, dateFilter)
        }
    }

    private val _dateFilter: MutableStateFlow<DateFilter> = MutableStateFlow(dateFilter ?: DateFilter.Monthly(0))
    val dateFilter = _dateFilter.asStateFlow()

    private val _accountDetailViewState: MutableStateFlow<AccountDetailViewState> = MutableStateFlow(AccountDetailViewState())
    val accountDetailViewState = _accountDetailViewState.asStateFlow()

    init {
        observeDateFilter()
        refresh()
    }

    private fun loadAccountState() {
        viewModelScope.launch(job) {
            // TODO: More logic here for graph maybe?
            val account = accountRepository.getAccountById(accountId)
            _accountDetailViewState.update {
                it.copy(
                    title = account.accountName
                )
            }
        }
    }

    override fun getTransactionListFlow(offset: Long): Flow<List<Transaction>> {
        val startDate = _dateFilter.value.getStartDate()
        val endDate = _dateFilter.value.getEndDate()
        return transactionRepository.getTransactions(startDate = startDate, endDate = endDate, offset = offset, limit = 100, accountIdFilter = accountId)
    }

    override fun refresh() {
        super.refresh()
        loadAccountState()
    }

    private fun observeDateFilter() {
        viewModelScope.launch {
            dateFilter.drop(1).collectLatest {
                refresh()
            }
        }
    }

    fun onDateFilterChanged(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }
}

internal data class AccountDetailViewState(
    val title: String = ""
)