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
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    ): Result<Unit> {
        return resultOf {
            transactionRepository.addNewExpenses(
                name,
                fromAccountId,
                amount,
                categoryId,
                transactionDate
            )
        }
    }

    suspend fun submitIncome(
        name: String,
        toAccountId: Int,
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    ): Result<Unit> {
        return resultOf {
            transactionRepository.addNewIncome(
                name,
                toAccountId,
                amount,
                categoryId,
                transactionDate,
            )
        }
    }

    suspend fun submitTransfer(
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Int,
        transactionDate: String,
    ): Result<Unit> {
        return resultOf {
            transactionRepository.addNewTransfer(
                name,
                fromAccountId,
                toAccountId,
                amount,
                transactionDate,
            )
        }
    }
}