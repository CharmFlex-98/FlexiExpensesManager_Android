package com.charmflex.flexiexpensesmanager.features.transactions.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionCategoryEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TransactionCategoryDao {

    @Query(
        "SELECT * FROM TransactionCategoryEntity"
    )
    fun getAllCategoriesIncludedDeleted(): Flow<List<TransactionCategoryEntity>>

    @Query(
        "SELECT * FROM TransactionCategoryEntity" +
                " WHERE is_deleted = 0 and transaction_type_code = :transactionTypeCode"
    )
    fun getCategories(transactionTypeCode: String): Flow<List<TransactionCategoryEntity>>

    @Query(
        "SELECT * FROM TransactionCategoryEntity" +
                " WHERE transaction_type_code = :transactionTypeCode"
    )
    fun getCategoriesIncludeDeleted(
        transactionTypeCode: String,
    ): Flow<List<TransactionCategoryEntity>>

    @Query(
        "SELECT * FROM TransactionCategoryEntity WHERE id = :categoryId"
    )
    fun getCategoryById(categoryId: Int): TransactionCategoryEntity

    @Insert
    suspend fun addCategory(categoryEntity: TransactionCategoryEntity)

    @Query("UPDATE TransactionCategoryEntity SET is_deleted = 1 WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Int)
}