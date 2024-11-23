package com.charmflex.flexiexpensesmanager.features.category.category.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.category.category.data.entities.TransactionCategoryEntity
import com.charmflex.flexiexpensesmanager.features.category.category.data.responses.CategoryTransactionAmountResponse
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
        "SELECT tc.id as category_id, " +
                "tc.name as category_name, " +
                "tc.parent_id as parent_category_id, " +
                "COALESCE(SUM(t.amount_in_cent * t.rate), 0) as expenses_amount_in_cent FROM " +
                " (SELECT * FROM TransactionCategoryEntity WHERE is_deleted = 0 AND transaction_type_code = :transactionTypeCode) tc " +
                "LEFT JOIN (SELECT * FROM TransactionEntity " +
                " WHERE (:startDate IS NULL OR date(transaction_date) >= date(:startDate))" +
                " AND (:endDate IS NULL OR date(transaction_date) <= date(:endDate))) t" +
                " ON tc.id = category_id" +
                " GROUP BY tc.id"
    )
    fun getAllCategoryTransactionAmount(
        startDate: String?,
        endDate: String?,
        transactionTypeCode: String
    ): Flow<List<CategoryTransactionAmountResponse>>

    @Query(
        "SELECT * FROM TransactionCategoryEntity WHERE id = :categoryId"
    )
    fun getCategoryById(categoryId: Int): TransactionCategoryEntity

    @Insert
    suspend fun addCategory(categoryEntity: TransactionCategoryEntity)

    @Query("UPDATE TransactionCategoryEntity SET is_deleted = 1 WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Int)
}