package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.backup.ui.ImportedData
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ImportTransaction
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

    suspend fun editExpenses(
        id: Long,
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

    suspend fun editIncome(
        id: Long,
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

    suspend fun editTransfer(
        id: Long,
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float
    )

    suspend fun addAllImportTransactions(transactionData: List<ImportTransaction>)

    fun getAllTransactions(
        startDate: String? = null,
        endDate: String? = null,
        tagFilter: List<Int> = listOf()
    ) : Flow<List<Transaction>>

    fun getTransactions(
        startDate: String? = null,
        endDate: String? = null,
        offset: Int = 0,
        limit: Int = 0,
        accountIdFilter: Int? = null,
        tagFilter: List<Int> = listOf()
    ): Flow<List<Transaction>>

    suspend fun getTransactionById(transactionId: Long): Transaction

    suspend fun deleteTransactionById(transactionId: Long)
}