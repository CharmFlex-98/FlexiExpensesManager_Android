package com.charmflex.flexiexpensesmanager.features.expenses.data.repositories

import com.charmflex.flexiexpensesmanager.features.expenses.domain.repositories.ExpensesRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class ExpensesRepositoryImpl @Inject constructor() : ExpensesRepository{
    override suspend fun createNewExpenses() {
        delay(1000)
    }
}