package com.charmflex.flexiexpensesmanager.features.scheduler.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
import com.charmflex.flexiexpensesmanager.features.scheduler.ScheduledTransactionHandler
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository.TransactionSchedulerRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import javax.inject.Inject

// This UseCase contains insert and update
internal class SubmitTransactionSchedulerUseCase @Inject constructor(
    private val transactionSchedulerRepository: TransactionSchedulerRepository,
    private val scheduledTransactionHandler: ScheduledTransactionHandler,
) {
    suspend fun submitExpenses(
        id: Long?,
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        startUpdateDate: String,
        nextUpdateDate: String,
        currency: String,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerPeriod: SchedulerPeriod
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionSchedulerRepository.updateScheduler(
                    id = it,
                    name = name,
                    fromAccountId = fromAccountId,
                    toAccountId = null,
                    transactionType = TransactionType.EXPENSES,
                    amount = amount,
                    categoryId = categoryId,
                    startUpdateDate = startUpdateDate,
                    nextUpdateDate = nextUpdateDate,
                    currency = currency,
                    accountMinorUnitAmount = accountMinorUnitAmount,
                    primaryMinorUnitAmount = primaryMinorUnitAmount,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
            } ?: run {
                val schedulerId = transactionSchedulerRepository.addScheduler(
                    name = name,
                    fromAccountId = fromAccountId,
                    toAccountId = null,
                    transactionType = TransactionType.EXPENSES,
                    amount = amount,
                    categoryId = categoryId,
                    startUpdateDate = startUpdateDate,
                    currency = currency,
                    accountMinorUnitAmount = accountMinorUnitAmount,
                    primaryMinorUnitAmount = primaryMinorUnitAmount,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
                scheduledTransactionHandler.onScheduled(schedulerId, schedulerPeriod)
            }
        }
    }

    suspend fun submitIncome(
        id: Long?,
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        startUpdateDate: String,
        nextUpdateDate: String,
        currency: String,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
        schedulerPeriod: SchedulerPeriod
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionSchedulerRepository.updateScheduler(
                    id = it,
                    name = name,
                    fromAccountId = null,
                    toAccountId = toAccountId,
                    transactionType = TransactionType.INCOME,
                    amount = amount,
                    categoryId = categoryId,
                    startUpdateDate = startUpdateDate,
                    nextUpdateDate = nextUpdateDate,
                    currency = currency,
                    accountMinorUnitAmount = amount,
                    primaryMinorUnitAmount = primaryMinorUnitAmount,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
            } ?: run {
                val schedulerId = transactionSchedulerRepository.addScheduler(
                    name = name,
                    fromAccountId = null,
                    toAccountId = toAccountId,
                    transactionType = TransactionType.INCOME,
                    amount = amount,
                    categoryId = categoryId,
                    startUpdateDate = startUpdateDate,
                    currency = currency,
                    accountMinorUnitAmount = amount,
                    primaryMinorUnitAmount = primaryMinorUnitAmount,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
                scheduledTransactionHandler.onScheduled(schedulerId, schedulerPeriod)
            }

        }
    }

    suspend fun submitTransfer(
        id: Long?,
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        startUpdateDate: String,
        nextUpdateDate: String,
        currency: String,
        accountMinorUnitAmount: Long,
        schedulerPeriod: SchedulerPeriod,
        tagIds: List<Int>
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionSchedulerRepository.updateScheduler(
                    id = it,
                    name = name,
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    transactionType = TransactionType.TRANSFER,
                    amount = amount,
                    categoryId = null,
                    startUpdateDate = startUpdateDate,
                    nextUpdateDate = nextUpdateDate,
                    currency = currency,
                    accountMinorUnitAmount = accountMinorUnitAmount,
                    primaryMinorUnitAmount = 0,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
            } ?: kotlin.run {
                val schedulerId = transactionSchedulerRepository.addScheduler(
                    name = name,
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    transactionType = TransactionType.TRANSFER,
                    amount = amount,
                    categoryId = null,
                    startUpdateDate = startUpdateDate,
                    currency = currency,
                    accountMinorUnitAmount = accountMinorUnitAmount,
                    primaryMinorUnitAmount = 0,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
                scheduledTransactionHandler.onScheduled(schedulerId, schedulerPeriod)
            }
        }
    }
}