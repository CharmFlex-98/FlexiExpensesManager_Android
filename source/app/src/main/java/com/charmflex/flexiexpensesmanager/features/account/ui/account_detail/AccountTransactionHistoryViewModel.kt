package com.charmflex.flexiexpensesmanager.features.account.ui.account_detail

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AccountTransactionHistoryViewModel @Inject constructor(
    mapper: TransactionHistoryMapper,
    routeNavigator: RouteNavigator,
    private val transactionRepository: TransactionRepository,
    private val accountIdFilter: Int
) : TransactionHistoryViewModel(mapper, routeNavigator) {

    init {
        observeTransactionList()
    }

    class Factory @Inject constructor(
        private val mapper: TransactionHistoryMapper,
        private val transactionRepository: TransactionRepository,
        private val routeNavigator: RouteNavigator,
    ) {
        fun create(accountIdFilter: Int): AccountTransactionHistoryViewModel {
            return AccountTransactionHistoryViewModel(
                mapper, routeNavigator, transactionRepository, accountIdFilter
            )
        }
    }

    override fun getTransactionListFlow(offset: Long): Flow<List<Transaction>> {
        return transactionRepository.getTransactions(offset = offset, limit = 100, accountIdFilter = accountIdFilter)
    }
}