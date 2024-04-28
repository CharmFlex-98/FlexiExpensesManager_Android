package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.expenses.provider.FakeExpensesDataProvider
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val expensesDataProvider: FakeExpensesDataProvider
) : TransactionRepository{
    override suspend fun createNewExpenses() {
        delay(1000)
    }

    override suspend fun getHistory(): List<ExpensesData> {
        return expensesDataProvider.getData()
    }
}