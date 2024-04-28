package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionConfigRepository
import javax.inject.Inject

internal class TransactionConfigRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionConfigRepository {
    override suspend fun getAllTransactionType(): List<TransactionType> {
        val res = transactionDao.getAllTransactionTypes()
        return res.map {
            TransactionType(
                it.id,
                it.code
            )
        }
    }

}