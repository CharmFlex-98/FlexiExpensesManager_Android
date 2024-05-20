package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import javax.inject.Inject

internal class SubmitTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend fun submitExpenses(
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>
    ): Result<Unit> {
        return resultOf {
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
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float
    ): Result<Unit> {
        return resultOf {
            transactionRepository.addNewIncome(
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
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float
    ): Result<Unit> {
        return resultOf {
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