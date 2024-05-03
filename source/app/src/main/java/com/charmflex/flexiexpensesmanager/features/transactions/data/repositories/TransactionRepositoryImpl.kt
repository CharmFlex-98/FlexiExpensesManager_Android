package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.expenses.provider.FakeExpensesDataProvider
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val expensesDataProvider: FakeExpensesDataProvider,
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override suspend fun addNewExpenses(
        name: String,
        fromAccountId: Int,
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    ) {
        val transaction = TransactionEntity(
            transactionName = name,
            accountFromId = fromAccountId,
            amountInCent = amount,
            categoryId = categoryId,
            transactionDate = transactionDate,
            transactionTypeCode = "EXPENSES",
            accountToId = null
        )
        transactionDao.insertTransaction(transaction = transaction)
    }

    override suspend fun getHistory(): List<ExpensesData> {
        return expensesDataProvider.getData()
    }

    override suspend fun getTransactions(
        startDate: String?,
        endDate: String?,
        offset: Int
    ): List<Transaction> {
        return transactionDao.getTransactions(
            startDate, endDate, offset
        ).map {
            Transaction(
                transactionId = it.transactionId,
                transactionName = it.transactionName,
                accountToId = it.accountToId,
                amountInCent = it.amountInCent,
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                transactionDate = it.transactionDate,
                transactionTypeCode = it.transactionTypeCode,
                tags = listOf()
            )
        }
    }


}