package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData

internal interface TransactionRepository {
    suspend fun createNewExpenses()
    suspend fun getHistory(): List<ExpensesData>
}