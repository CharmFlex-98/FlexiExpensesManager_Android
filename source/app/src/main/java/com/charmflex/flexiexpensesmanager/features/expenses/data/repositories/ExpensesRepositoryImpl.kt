package com.charmflex.flexiexpensesmanager.features.expenses.data.repositories

import com.charmflex.flexiexpensesmanager.features.expenses.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.expenses.domain.repositories.ExpensesRepository
import com.charmflex.flexiexpensesmanager.features.expenses.provider.FakeExpensesDataProvider
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class ExpensesRepositoryImpl @Inject constructor(
    private val expensesDataProvider: FakeExpensesDataProvider
) : ExpensesRepository{
    override suspend fun createNewExpenses() {
        delay(1000)
    }

    override suspend fun getHistory(): List<ExpensesData> {
        return expensesDataProvider.getData()
    }
}