package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategory
import kotlinx.coroutines.flow.Flow

internal interface TransactionCategoryRepository {

    suspend fun getAllCategoriesIncludedDeleted(): List<TransactionCategory>

    fun getCategories(transactionTypeCode: String): Flow<TransactionCategories>

    fun getCategoriesIncludeDeleted(transactionTypeCode: String): Flow<TransactionCategories>

    suspend fun addCategory(category: String, parentId: Int, transactionTypeCode: String)

    suspend fun deleteCategory(categoryId: Int)
}