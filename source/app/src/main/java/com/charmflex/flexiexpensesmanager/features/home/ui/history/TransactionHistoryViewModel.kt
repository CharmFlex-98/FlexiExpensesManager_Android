package com.charmflex.flexiexpensesmanager.features.home.ui.history

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModelParent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TransactionHistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    mapper: TransactionHistoryMapper,
    routeNavigator: RouteNavigator,
) : TransactionHistoryViewModelParent(mapper, routeNavigator) {

    init {
        observeTransactionList()
    }

    override fun getTransactionListFlow(offset: Long): Flow<List<Transaction>> {
        return transactionRepository.getTransactions(offset = offset, limit = 100)
    }
}