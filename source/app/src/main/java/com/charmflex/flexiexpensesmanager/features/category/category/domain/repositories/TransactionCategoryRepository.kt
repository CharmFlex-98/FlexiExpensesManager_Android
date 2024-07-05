package com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories

import com.charmflex.flexiexpensesmanager.features.category.category.data.repositories.CategoryAmountNode
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategory
import kotlinx.coroutines.flow.Flow

internal interface TransactionCategoryRepository {
    fun getAllCategoryTransactionAmount(
        startDate: String?,
        endDate: String?,
        transactionTypeCode: String
    ): Flow<List<CategoryAmountNode>>

    suspend fun getAllCategoriesIncludedDeleted(): List<TransactionCategory>

    fun getCategories(transactionTypeCode: String): Flow<TransactionCategories>


    fun getCategoriesIncludeDeleted(transactionTypeCode: String): Flow<TransactionCategories>

    suspend fun addCategory(category: String, parentId: Int, transactionTypeCode: String)
    fun getCategoryById(categoryId: Int): Transaction.TransactionCategory

    suspend fun deleteCategory(categoryId: Int)
}