package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType

internal interface TransactionConfigRepository {
    suspend fun getAllTransactionType(): List<TransactionType>
}