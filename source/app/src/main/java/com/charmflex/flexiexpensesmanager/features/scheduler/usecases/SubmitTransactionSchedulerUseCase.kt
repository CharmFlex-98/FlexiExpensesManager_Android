package com.charmflex.flexiexpensesmanager.features.scheduler.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.scheduler.ScheduledTransactionHandler
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository.TransactionSchedulerRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionDomainInput
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.SubmitTransactionUseCase
import javax.inject.Inject

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
        transactionDate: String,
        currency: String,
        rate: Float,
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
                    startDate = transactionDate,
                    currency = currency,
                    rate = rate,
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
                    startDate = transactionDate,
                    currency = currency,
                    rate = rate,
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
        transactionDate: String,
        currency: String,
        rate: Float,
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
                    startDate = transactionDate,
                    currency = currency,
                    rate = rate,
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
                    startDate = transactionDate,
                    currency = currency,
                    rate = rate,
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
        transactionDate: String,
        currency: String,
        rate: Float,
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
                    startDate = transactionDate,
                    currency = currency,
                    rate = rate,
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
                    startDate = transactionDate,
                    currency = currency,
                    rate = rate,
                    tagIds = tagIds,
                    schedulerPeriod = schedulerPeriod
                )
                scheduledTransactionHandler.onScheduled(schedulerId, schedulerPeriod)
            }
        }
    }
}