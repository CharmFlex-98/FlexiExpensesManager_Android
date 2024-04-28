package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionConfigRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GetTransactionTypeUseCase @Inject constructor(
    private val transactionConfigRepository: TransactionConfigRepository,
) {
    suspend operator fun invoke(): List<TransactionType> {
        return transactionConfigRepository.getAllTransactionType()
    }
}