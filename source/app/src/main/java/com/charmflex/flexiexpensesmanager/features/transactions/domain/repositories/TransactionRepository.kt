package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction

internal interface TransactionRepository {
    suspend fun addNewExpenses(
        name: String,
        fromAccountId: Int,
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    )
    suspend fun getHistory(): List<ExpensesData>

    suspend fun getTransactions(
        startDate: String? = null,
        endDate: String? = null,
        offset: Int = 0
    ): List<Transaction>
}