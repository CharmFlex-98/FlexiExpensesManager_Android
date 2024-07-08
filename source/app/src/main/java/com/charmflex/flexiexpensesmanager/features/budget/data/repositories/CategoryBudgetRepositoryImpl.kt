package com.charmflex.flexiexpensesmanager.features.budget.data.repositories

import com.charmflex.flexiexpensesmanager.features.budget.data.daos.CategoryBudgetDao
import com.charmflex.flexiexpensesmanager.features.budget.data.entities.CategoryBudgetEntity
import com.charmflex.flexiexpensesmanager.features.budget.data.responses.CategoryBudgetResponse
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.AdjustedCategoryBudgetNode
import com.charmflex.flexiexpensesmanager.features.budget.domain.repositories.CategoryBudgetRepository
import com.charmflex.flexiexpensesmanager.features.category.category.domain.buildCategoryTree
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
    override fun getMonthlyCategoryBudgetInfo(startDate: String, endDate: String): Flow<List<AdjustedCategoryBudgetNode>> {
        val res = categoryBudgetDao.getMonthlyCategoryBudget(startDate, endDate)
        return res.transformLatest { list ->
            val map = list.groupBy { it.categoryParentId }
            val nodes = list.filter {
                it.categoryParentId == 0
            }.map {
                buildCategoryTree(
                    retrievalKey = {
                        it.categoryId
                    },
                    parentCatIDChildrenMap = map,
                    responseEntity = it,
                ) { level, entity ->
                    AdjustedCategoryBudgetNode(
                        categoryId = entity.categoryId,
                        categoryName = entity.categoryName,
                        parentCategoryId = entity.categoryParentId,
                        defaultBudgetInCent = entity.budget?.defaultBudgetInCent ?: 0,
                        expensesInCent = entity.expensesAmountInCent
                    )
                }
            }
            emit(nodes)
        }
    }

    override suspend fun deleteCategoryBudget(budgetId: Int) {
        categoryBudgetDao.deleteCategoryBudget(budgetId)
    }
}