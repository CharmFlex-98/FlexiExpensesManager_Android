package com.charmflex.flexiexpensesmanager.features.scheduler

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.ScheduledTransaction
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository.TransactionSchedulerRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionDomainInput
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

internal const val MAX_SCHEDULED_TRANSACTION_INSERTION_BATCH = 50

internal interface ScheduledTransactionHandler {
    suspend fun onScheduled(schedulerId: Long, schedulerPeriod: SchedulerPeriod)
    suspend fun update()
}

internal class ScheduledTransactionHandlerImpl @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val transactionSchedulerRepository: TransactionSchedulerRepository
) : ScheduledTransactionHandler {

    // This will add all transactions when scheduled is done first time.
    // Will add regardless the transaction under same scheduler exists.
    // Thus, only "add scheduler" should be supported, but not "edit scheduler"
    override suspend fun onScheduled(schedulerId: Long, schedulerPeriod: SchedulerPeriod) {
        val input = transactionSchedulerRepository.getTransactionSchedulerById(schedulerId)
        input?.let {
            handleUpdateData(it, MAX_SCHEDULED_TRANSACTION_INSERTION_BATCH)
        }
    }


    // Update when needed (Maybe every home screen?)
    override suspend fun update() {
        val allSchedulers = transactionSchedulerRepository.getAllTransactionSchedulers().firstOrNull() ?: emptyList()

        allSchedulers.forEach {
            handleUpdateData(it)
        }
    }

    private suspend fun handleUpdateData(data: ScheduledTransaction, maxBatchInsertionCount: Int = Int.MAX_VALUE) {
        val updateState = createUpdateState(data)
        if (updateState.transactionDomainInputs.isNotEmpty() && updateState.transactionDomainInputs.size <= maxBatchInsertionCount) {
            transactionRepository.insertAllTransactions(updateState.transactionDomainInputs)
            transactionSchedulerRepository.updateScheduler(
                id = data.id.toLong(),
                name = data.transactionName,
                fromAccountId = data.accountFrom?.accountId,
                toAccountId = data.accountTo?.accountId,
                transactionType = data.transactionType,
                amount = data.minorUnitAmount,
                categoryId = data.category?.id,
                startUpdateDate = data.startUpdateDate,
                nextUpdateDate = updateState.nextDate.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN),
                currency = data.currency,
                accountMinorUnitAmount = data.accountMinorUnitAmount,
                primaryMinorUnitAmount = data.primaryMinorUnitAmount,
                tagIds = data.tags.map { it.id },
                schedulerPeriod = data.schedulerPeriod
            )
        }
    }

    private fun createUpdateState(
        scheduledTransaction: ScheduledTransaction,
    ) : ScheduleTransactionUpdateState {
        val toInsert = mutableListOf<TransactionDomainInput>()
        val startDate = scheduledTransaction.nextUpdateDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)!!
        var nextDate = startDate
        while (nextDate <= LocalDate.now()) {
            toInsert.add(
                scheduledTransaction.toTransactionDomainInput(
                    nextDate.toStringWithPattern(
                        DATE_ONLY_DEFAULT_PATTERN
                    ), scheduledTransaction.id
                )
            )
            nextDate = when (scheduledTransaction.schedulerPeriod) {
                SchedulerPeriod.DAILY -> nextDate.plusDays(1)
                SchedulerPeriod.MONTHLY -> nextDate.plusMonths(1)
                SchedulerPeriod.YEARLY -> nextDate.plusYears(1)
                // TODO: Hmm...To never schedule again...
                else -> nextDate.plusYears(100)
            }
        }

        return ScheduleTransactionUpdateState(
            transactionDomainInputs = toInsert,
            nextDate = nextDate
        )
    }
}

private data class ScheduleTransactionUpdateState(
    val transactionDomainInputs: List<TransactionDomainInput>,
    val nextDate: LocalDate
)