package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionDomainInput
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

// TODO: Consider to migrate to TransactionInput

internal interface TransactionRepository {
    suspend fun addTransaction(
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        transactionDate: String,
        currency: String,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerId: Int?
    )

    suspend fun editTransaction(
        id: Long,
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        transactionDate: String,
        currency: String,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerId: Int?
    )

    suspend fun insertAllTransactions(transactions: List<TransactionDomainInput>)


    fun getTransactions(
        startDate: String? = null,
        endDate: String? = null,
        offset: Long = 0,
        limit: Int = -1,
        accountIdFilter: Int? = null,
        tagFilter: List<Int> = listOf()
    ): Flow<List<Transaction>>

    fun getTransactionById(transactionId: Long): Flow<Transaction>

    suspend fun deleteTransactionById(transactionId: Long)

    suspend fun deleteAllTransactions()
}