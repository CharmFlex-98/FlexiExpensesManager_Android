package com.charmflex.flexiexpensesmanager.features.expenses.domain.repositories

import com.charmflex.flexiexpensesmanager.features.expenses.domain.model.ExpensesData

internal interface ExpensesRepository {
    suspend fun createNewExpenses()
    suspend fun getHistory(): List<ExpensesData>
}