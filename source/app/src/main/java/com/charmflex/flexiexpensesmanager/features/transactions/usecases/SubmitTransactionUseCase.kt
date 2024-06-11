package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import javax.inject.Inject

internal class SubmitTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
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
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionRepository.editExpenses(
                    it,
                    name,
                    fromAccountId,
                    amount,
                    categoryId,
                    transactionDate,
                    currency,
                    rate,
                    tagIds
                )
            } ?:
            transactionRepository.addNewExpenses(
                name,
                fromAccountId,
                amount,
                categoryId,
                transactionDate,
                currency,
                rate,
                tagIds
            )
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
        rate: Float
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionRepository.editIncome(
                    it,
                    name,
                    toAccountId,
                    amount,
                    categoryId,
                    transactionDate,
                    currency,
                    rate
                )
            } ?: transactionRepository.addNewIncome(
                name,
                toAccountId,
                amount,
                categoryId,
                transactionDate,
                currency,
                rate
            )

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
        rate: Float
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionRepository.editTransfer(
                    it,
                    name,
                    fromAccountId,
                    toAccountId,
                    amount,
                    transactionDate,
                    currency,
                    rate
                )
            } ?:
            transactionRepository.addNewTransfer(
                name,
                fromAccountId,
                toAccountId,
                amount,
                transactionDate,
                currency,
                rate
            )
        }
    }
}