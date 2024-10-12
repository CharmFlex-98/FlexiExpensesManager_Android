package com.charmflex.flexiexpensesmanager.features.budget.domain.repositories

import com.charmflex.flexiexpensesmanager.features.budget.data.responses.CategoryBudgetResponse
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.AdjustedCategoryBudgetNode
import kotlinx.coroutines.flow.Flow

internal interface CategoryBudgetRepository {
    suspend fun addCategoryBudget(categoryId: Int, amountInCent: Long): Long
    fun getAllCategoryBudgets(): Flow<List<CategoryBudgetResponse>>
    fun getMonthlyCategoryBudgetInfo(startDate: String, endDate: String): Flow<List<AdjustedCategoryBudgetNode>>
    suspend fun deleteCategoryBudget(budgetId: Int)
}