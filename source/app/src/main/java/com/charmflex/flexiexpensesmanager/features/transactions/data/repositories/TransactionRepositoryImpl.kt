package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import androidx.compose.runtime.currentRecomposeScope
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.expenses.provider.FakeExpensesDataProvider
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.mapper.TransactionMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val expensesDataProvider: FakeExpensesDataProvider,
    private val transactionMapper: TransactionMapper,
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override suspend fun addNewExpenses(
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float
    ) {
        val transaction = TransactionEntity(
            transactionName = name,
            accountFromId = fromAccountId,
            amountInCent = amount,
            categoryId = categoryId,
            transactionDate = transactionDate,
            transactionTypeCode = "EXPENSES",
            accountToId = null,
            currency = currency,
            rate = rate
        )
        transactionDao.insertTransaction(transaction = transaction)
    }

    override suspend fun addNewIncome(
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float
    ) {
        val transaction = TransactionEntity(
            transactionName = name,
            amountInCent = amount,
            categoryId = categoryId,
            transactionDate = transactionDate,
            transactionTypeCode = "INCOME",
            accountToId = toAccountId,
            accountFromId = null,
            currency = currency,
            rate = rate
        )
        transactionDao.insertTransaction(transaction = transaction)
    }

    override suspend fun addNewTransfer(
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float
    ) {
        val transaction = TransactionEntity(
            transactionName = name,
            amountInCent = amount,
            transactionDate = transactionDate,
            transactionTypeCode = "TRANSFER",
            accountToId = toAccountId,
            accountFromId = fromAccountId,
            categoryId = null,
            currency = currency,
            rate = rate
        )
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun getHistory(): List<ExpensesData> {
        return expensesDataProvider.getData()
    }

    override fun getTransactions(
        startDate: String?,
        endDate: String?,
        offset: Int
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactions(
            startDate, endDate, offset
        ).map {
            it.map {
                transactionMapper.map(it)
            }
        }
    }

    override suspend fun getTransactionById(transactionId: Long): Transaction {
        val res = transactionDao.getTransactionById(transactionId)
        return transactionMapper.map(res)
    }

    override suspend fun deleteTransactionById(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }


}