package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

internal interface TransactionRepository {
    suspend fun addNewExpenses(
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>
    )

    suspend fun addNewIncome(
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float
    )

    suspend fun addNewTransfer(
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float
    )

    fun getTransactions(
        startDate: String? = null,
        endDate: String? = null,
        offset: Int = 0,
        tagFilter: List<Int> = listOf()
    ): Flow<List<Transaction>>

    suspend fun getTransactionById(transactionId: Long): Transaction

    suspend fun deleteTransactionById(transactionId: Long)
}