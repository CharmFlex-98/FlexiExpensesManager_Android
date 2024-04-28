package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData

internal interface TransactionRepository {
    suspend fun addNewExpenses(
        fromAccountId: Int,
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    )
    suspend fun getHistory(): List<ExpensesData>

}