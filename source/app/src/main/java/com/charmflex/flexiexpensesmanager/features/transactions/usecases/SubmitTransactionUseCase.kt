package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import javax.inject.Inject

internal class SubmitTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend fun submitExpenses(
        fromAccountId: Int,
        amount: Int,
        categoryId: Int,
        transactionDate: String,
    ): Result<Unit> {
        return resultOf {
            transactionRepository.addNewExpenses(
                fromAccountId,
                amount,
                categoryId,
                transactionDate
            )
        }
    }
}