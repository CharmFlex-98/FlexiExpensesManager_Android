package com.charmflex.flexiexpensesmanager.features.budget.data.repositories

import com.charmflex.flexiexpensesmanager.features.budget.data.daos.CategoryBudgetDao
import com.charmflex.flexiexpensesmanager.features.budget.data.entities.CategoryBudgetEntity
import com.charmflex.flexiexpensesmanager.features.budget.data.responses.CategoryBudgetResponse
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.CategoryBudgetFullInfo
import com.charmflex.flexiexpensesmanager.features.budget.domain.repositories.CategoryBudgetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

internal class CategoryBudgetRepositoryImpl @Inject constructor(
    private val categoryBudgetDao: CategoryBudgetDao
): CategoryBudgetRepository {
    override suspend fun addCategoryBudget(categoryId: Int, amountInCent: Long): Long {
        val entity = CategoryBudgetEntity(
            categoryId = categoryId,
            defaultBudgetInCent = amountInCent
        )
        return categoryBudgetDao.addCategoryBudget(entity)
    }

    override fun getAllCategoryBudgets(): Flow<List<CategoryBudgetResponse>> {
        return categoryBudgetDao.getAllCategoryBudgets()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMonthlyCategoryBudgetInfo(monthYear: String): Flow<List<CategoryBudgetFullInfo>> {
        val res = categoryBudgetDao.getCategoryBudgetByMonth()
        return res.transformLatest { responses ->
            val item = responses.groupBy {
                CategoryBudgetFullInfo(
                    it.categoryId,
                    it.categoryName,
                    it.categoryParentId,
                    it.budget?.let {
                        CategoryBudgetFullInfo.BudgetDomainModel(
                            it.categoryBudgetId,
                            it.defaultBudgetInCent
                        )
                    }
                )
            }
                .map { (model, monthlyBudgetResponses) ->
                    CategoryBudgetFullInfo(
                        categoryId = model.categoryId,
                        categoryName = model.categoryName,
                        categoryParentId = model.categoryParentId,
                        budget = model.budget?.copy(
                            customMonthlyBudgets = monthlyBudgetResponses.mapNotNull {
                                it.budget?.customMonthlyBudget?.let {
                                    CategoryBudgetFullInfo.CustomMonthlyBudgetDomainModel(
                                        it.budgetMonthYear,
                                        it.customBudgetInCent
                                    )
                                }
                            }
                        )
                    )
                }
            emit(item)
        }
    }

    override suspend fun deleteCategoryBudget(budgetId: Int) {
        categoryBudgetDao.deleteCategoryBudget(budgetId)
    }
}

private data class MonthlyBudgetIntermediateData(
    val categoryId: Int,
    val categoryName: String,
    val categoryParentId: Int,
    val categoryBudgetId: Int?,
    val defaultBudgetInCent: Long?
)