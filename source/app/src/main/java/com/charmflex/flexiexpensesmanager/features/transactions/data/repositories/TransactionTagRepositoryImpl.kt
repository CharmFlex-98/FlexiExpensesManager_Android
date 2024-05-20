package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.tag.data.daos.TagDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionTagRepository
import javax.inject.Inject

internal class TransactionTagRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val tagDao: TagDao,
    private val transactionTagDao: TagDao
) : TransactionTagRepository{
    override fun insertTransactionTag() {
        TODO("Not yet implemented")
    }
}