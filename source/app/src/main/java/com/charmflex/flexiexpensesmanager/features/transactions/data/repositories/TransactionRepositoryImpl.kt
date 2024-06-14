package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.tag.data.entities.TagEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionTagDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTagEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.mapper.TransactionMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ImportTransaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionMapper: TransactionMapper,
    private val transactionDao: TransactionDao,
    private val transactionTagDao: TransactionTagDao
) : TransactionRepository {
    override suspend fun addNewExpenses(
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>
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
            rate = rate,
        )
        transactionTagDao.insertTransactionAndTransactionTag(transaction, tagIds)
    }

    override suspend fun editExpenses(
        id: Long,
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>
    ) {
        val transaction = TransactionEntity(
            id = id,
            transactionName = name,
            accountFromId = fromAccountId,
            amountInCent = amount,
            categoryId = categoryId,
            transactionDate = transactionDate,
            transactionTypeCode = "EXPENSES",
            accountToId = null,
            currency = currency,
            rate = rate,
        )
        transactionTagDao.updateTransactionAndTransactionTags(transaction, tagIds)
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

    override suspend fun editIncome(
        id: Long,
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float
    ) {
        val transaction = TransactionEntity(
            id = id,
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
        transactionTagDao.updateTransaction(transaction)
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

    override suspend fun editTransfer(
        id: Long,
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float
    ) {
        val transaction = TransactionEntity(
            id = id,
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
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun addAllImportTransactions(transactionData: List<ImportTransaction>) {
        val transactionTags = transactionData.map {
            it.tagIds
        }
        val transactionEntities = transactionData.map {
            TransactionEntity(
                transactionName = it.transactionName,
                accountFromId = it.transactionAccountFrom,
                accountToId = it.transactionAccountTo,
                transactionTypeCode = it.transactionTypeCode,
                amountInCent = it.amountInCent,
                transactionDate = it.transactionDate,
                categoryId = it.transactionCategoryId,
                currency = it.currency,
                rate = it.rate
            )
        }
        transactionTagDao.insertAllTransactionsAndTransactionTags(transactionEntities, transactionTags)
    }

    override fun getAllTransactions(
        startDate: String?,
        endDate: String?,
        tagFilter: List<Int>
    ): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions(
            startDate, endDate, tagFilter = tagFilter
        ).map {
            it.map {
                transactionMapper.map(it)
            }
        }
    }

    override fun getTransactions(
        startDate: String?,
        endDate: String?,
        offset: Long,
        limit: Int,
        accountIdFilter: Int?,
        tagFilter: List<Int>
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactions(
            startDate, endDate, offset, limit, accountIdFilter = accountIdFilter, tagFilter = tagFilter
        ).map {
            it.map {
                transactionMapper.map(it)
            }
        }
    }

    override fun getTransactionById(transactionId: Long): Flow<Transaction> {
        val res = transactionDao.getTransactionById(transactionId)
        return res.map {
            transactionMapper.map(it)
        }
    }

    override suspend fun deleteTransactionById(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }

    override suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }
}