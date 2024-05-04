package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import kotlinx.coroutines.flow.Flow

internal interface TransactionCategoryRepository {
    fun getAllCategories(transactionTypeCode: String): Flow<TransactionCategories>

    suspend fun addCategory(category: String, parentId: Int, transactionTypeCode: String)

    suspend fun deleteCategory(categoryId: Int)
}