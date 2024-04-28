package com.charmflex.flexiexpensesmanager.features.transactions.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity

@Dao
internal interface TransactionDao {
    @Query("SELECT * FROM TransactionTypeEntity")
    suspend fun getAllTransactionTypes(): List<TransactionTypeEntity>
}