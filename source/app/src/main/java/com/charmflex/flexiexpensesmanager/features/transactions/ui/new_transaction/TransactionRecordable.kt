package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod

internal interface TransactionRecordable {
    suspend fun loadTransaction(id: Long): TransactionEditorDataUI?
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
    ): Result<Unit>
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
    ): Result<Unit>
    suspend fun submitTransfer(
        id: Long?,
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>,
    ): Result<Unit>
    suspend fun submitUpdate(
        id: Long?,
        accountId: Int,
        isIncrement: Boolean,
        amount: Long,
        transactionDate: String,
    ) : Result<Unit>
    fun getType(): TransactionRecordableType
}

internal enum class TransactionRecordableType {
    NEW_TRANSACTION, EDIT_TRANSACTION, NEW_SCHEDULER, EDIT_SCHEDULER
}