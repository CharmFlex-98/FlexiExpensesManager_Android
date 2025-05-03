package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionTagDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.mapper.TransactionMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionDomainInput
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionMapper: TransactionMapper,
    private val transactionDao: TransactionDao,
    private val transactionTagDao: TransactionTagDao
) : TransactionRepository {
    override suspend fun addTransaction(
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        transactionDate: String,
        currency: String,
        accountCurrencyRate: Float,
        primaryCurrencyRate: Float?,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerId: Int?
    ) {
        val transaction = TransactionEntity(
            transactionName = name,
            minorUnitAmount = amount,
            transactionDate = transactionDate,
            transactionTypeCode = transactionType.name,
            accountToId = toAccountId,
            accountFromId = fromAccountId,
            categoryId = categoryId,
            currency = currency,
            accountCurrencyRate = accountCurrencyRate,
            primaryCurrencyRate = primaryCurrencyRate,
            accountMinorUnitAmount = accountMinorUnitAmount,
            primaryMinorUnitAmount = primaryMinorUnitAmount,
            schedulerId = schedulerId?.toLong()
        )
        transactionTagDao.insertTransactionAndTransactionTag(transaction, tagIds)
    }

    override suspend fun editTransaction(
        id: Long,
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        transactionDate: String,
        currency: String,
        accountCurrencyRate: Float,
        primaryCurrencyRate: Float?,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerId: Int?,
    ) {
        val transaction = TransactionEntity(
            id = id,
            transactionName = name,
            minorUnitAmount = amount,
            transactionDate = transactionDate,
            transactionTypeCode = transactionType.name,
            accountToId = toAccountId,
            accountFromId = fromAccountId,
            categoryId = categoryId,
            currency = currency,
            accountCurrencyRate = accountCurrencyRate,
            primaryCurrencyRate = primaryCurrencyRate,
            accountMinorUnitAmount = accountMinorUnitAmount,
            primaryMinorUnitAmount = primaryMinorUnitAmount,
            schedulerId = schedulerId?.toLong()
        )
        transactionTagDao.updateTransactionAndTransactionTags(transaction, tagIds)
    }

    override suspend fun insertAllTransactions(transactions: List<TransactionDomainInput>) {
        val transactionTags = transactions.map {
            it.tagIds
        }
        val transactionEntities = transactions.map {
            TransactionEntity(
                transactionName = it.transactionName,
                accountFromId = it.transactionAccountFrom,
                accountToId = it.transactionAccountTo,
                transactionTypeCode = it.transactionTypeCode,
                minorUnitAmount = it.amountInCent,
                transactionDate = it.transactionDate,
                categoryId = it.transactionCategoryId,
                currency = it.currency,
                accountCurrencyRate = it.rate,
                primaryCurrencyRate = it.primaryCurrencyRate,
                accountMinorUnitAmount = it.accountMinorUnitAmount,
                primaryMinorUnitAmount = it.primaryMinorUnitAmount,
                schedulerId = it.schedulerId?.toLong()
            )
        }
        transactionTagDao.insertAllTransactionsAndTransactionTags(
            transactionEntities,
            transactionTags
        )
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
            startDate = startDate,
            endDate = endDate,
            offset = offset,
            limit = limit,
            accountIdFilter = accountIdFilter,
            tagFilter = tagFilter
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