package com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository

import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.ScheduledTransaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

internal interface TransactionSchedulerRepository {
    suspend fun addScheduler(
        name: String,
        fromAccountId: Int?,
        toAccountId: Int?,
        categoryId: Int?,
        transactionType: TransactionType,
        amount: Long,
        startUpdateDate: String,
        currency: String,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerPeriod: SchedulerPeriod
    ) : Long

    suspend fun updateScheduler(
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
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerPeriod: SchedulerPeriod
    )


    fun getAllTransactionSchedulers(): Flow<List<ScheduledTransaction>>
//
    suspend fun getTransactionSchedulerById(id: Long): ScheduledTransaction?
//
//    suspend fun deleteTransactionSchedulerById(id: Long)
//
//    suspend fun deleteAllTransactionSchedulers()


    suspend fun removeSchedulerById(id: Int)
}