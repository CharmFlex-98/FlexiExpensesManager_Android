package com.charmflex.flexiexpensesmanager.features.scheduler.data.repositories

import com.charmflex.flexiexpensesmanager.features.scheduler.data.daos.ScheduledTransactionTagDao
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionEntity
import com.charmflex.flexiexpensesmanager.features.scheduler.data.mappers.ScheduledTransactionMapper
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.ScheduledTransaction
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository.TransactionSchedulerRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

private const val TransactionSchedulerFile = "transaction_scheduler_file.txt"

internal class TransactionSchedulerRepositoryImpl @Inject constructor(
    private val scheduledTransactionTagDao: ScheduledTransactionTagDao,
    private val mapper: ScheduledTransactionMapper
) : TransactionSchedulerRepository {
    override suspend fun addScheduler(
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        startUpdateDate: String,
        currency: String,
        rate: Float,
        primaryCurrencyRate: Float?,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerPeriod: SchedulerPeriod
    ) : Long {
        val scheduledTransaction = ScheduledTransactionEntity(
            transactionName = name,
            accountFromId = fromAccountId,
            accountToId = toAccountId,
            categoryId = categoryId,
            transactionType = transactionType.name,
            minorUnitAmount = amount,
            startUpdateDate = startUpdateDate,
            nextUpdateDate = startUpdateDate,
            currency = currency,
            accountCurrencyRate = rate,
            primaryCurrencyRate = primaryCurrencyRate,
            accountMinorUnitAmount = accountMinorUnitAmount,
            primaryMinorUnitAmount = primaryMinorUnitAmount,
            schedulerPeriod = schedulerPeriod.name
        )
        return scheduledTransactionTagDao.insertScheduledTransactionAndTags(scheduledTransaction, tagIds)
    }

    override suspend fun updateScheduler(
        id: Long,
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        startUpdateDate: String,
        nextUpdateDate: String,
        currency: String,
        rate: Float,
        primaryCurrencyRate: Float?,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerPeriod: SchedulerPeriod
    ) {
        val scheduledTransaction = ScheduledTransactionEntity(
            id = id,
            transactionName = name,
            accountFromId = fromAccountId,
            accountToId = toAccountId,
            categoryId = categoryId,
            transactionType = transactionType.name,
            minorUnitAmount = amount,
            startUpdateDate = startUpdateDate,
            nextUpdateDate = nextUpdateDate,
            currency = currency,
            accountCurrencyRate = rate,
            primaryCurrencyRate = primaryCurrencyRate,
            accountMinorUnitAmount = accountMinorUnitAmount,
            primaryMinorUnitAmount = primaryMinorUnitAmount,
            schedulerPeriod = schedulerPeriod.name
        )
        scheduledTransactionTagDao.updateScheduledTransactionAndTags(scheduledTransaction, tagIds)
    }

    override fun getAllTransactionSchedulers(): Flow<List<ScheduledTransaction>> {
        val res = scheduledTransactionTagDao.getAllTransactionScheduler()
        return res.map {
            it.map { item ->
                mapper.map(item)
            }
        }

    }

    override suspend fun getTransactionSchedulerById(id: Long): ScheduledTransaction? {
        return scheduledTransactionTagDao.getScheduledTransactionById(id)?.let {
            mapper.map(it)
        }
    }

    override suspend fun removeSchedulerById(id: Int) {
        scheduledTransactionTagDao.deleteSchedulerById(id.toLong())
    }
}

private suspend fun <T> catchException(default: T?, operation: suspend () -> T): T? {
    return try {
        operation()
    } catch (e: Exception) {
        default
    }
}