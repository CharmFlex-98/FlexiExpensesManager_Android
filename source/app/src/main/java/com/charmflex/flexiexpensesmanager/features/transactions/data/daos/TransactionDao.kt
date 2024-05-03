package com.charmflex.flexiexpensesmanager.features.transactions.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.responses.TransactionResponse

@Dao
internal interface TransactionDao {
    @Query("SELECT * FROM TransactionTypeEntity")
    suspend fun getAllTransactionTypes(): List<TransactionTypeEntity>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query(
        "SELECT t.id as transaction_id," +
            "t.transaction_name," +
            "t.account_from_id," +
            "t.account_to_id," +
            "t.transaction_type_code," +
            "t.amount_in_cent," +
            "t.transaction_date," +
            "t.category_id," +
            "tc.name as category_name FROM TransactionEntity t" +
            " INNER JOIN TransactionCategoryEntity tc ON t.category_id = tc.id" +
            " WHERE (:startDate IS NULL OR transaction_date >= :startDate)" +
            " AND (:endDate IS NULL OR transaction_date <= :endDate)" +
            " ORDER BY transaction_date DESC" +
            " LIMIT :limit OFFSET :offset")
    suspend fun getTransactions(
        startDate: String?,
        endDate: String?,
        offset: Int,
        limit: Int = 100
    ) : List<TransactionResponse>
}