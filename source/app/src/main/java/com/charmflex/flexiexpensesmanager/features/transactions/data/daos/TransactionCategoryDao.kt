package com.charmflex.flexiexpensesmanager.features.transactions.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionCategoryEntity

@Dao
internal interface TransactionCategoryDao {

    @Query(
        "SELECT * FROM TransactionCategoryEntity" +
                " WHERE is_deleted = 0 and transaction_type_code = :transactionTypeCode"
    )
    suspend fun getCategories(transactionTypeCode: String): List<TransactionCategoryEntity>
}