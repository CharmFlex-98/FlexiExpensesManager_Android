package com.charmflex.flexiexpensesmanager.features.home.ui.history

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeItemRefreshable
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TransactionHomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    mapper: TransactionHistoryMapper,
    routeNavigator: RouteNavigator,
) : TransactionHistoryViewModel(mapper, routeNavigator), HomeItemRefreshable {

    init {
        refresh()
    }

    override fun refreshHome() {
        refresh()
    }

    override fun getDBTransactionListFlow(offset: Long): Flow<List<Transaction>> {
        return transactionRepository.getTransactions(offset = offset, limit = 100)
    }

    override suspend fun filter(dbData: List<Transaction>): List<Transaction> {
        return dbData
    }

    override suspend fun onReceivedFilteredData(data: List<Transaction>, clearOldList: Boolean) {}
}