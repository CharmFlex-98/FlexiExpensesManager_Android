package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.expenses.provider.FakeExpensesDataProvider
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ExpensesData
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val expensesDataProvider: FakeExpensesDataProvider,
    private val transactionDao: TransactionDao
) : TransactionRepository{
    override suspend fun addNewExpenses(
        fromAccountId: Int,
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    ) {
        val transaction = TransactionEntity(
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
}