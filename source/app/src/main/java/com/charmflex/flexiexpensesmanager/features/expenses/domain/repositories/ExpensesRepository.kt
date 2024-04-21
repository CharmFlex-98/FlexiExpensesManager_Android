package com.charmflex.flexiexpensesmanager.features.expenses.domain.repositories

internal interface ExpensesRepository {
    suspend fun createNewExpenses()
}