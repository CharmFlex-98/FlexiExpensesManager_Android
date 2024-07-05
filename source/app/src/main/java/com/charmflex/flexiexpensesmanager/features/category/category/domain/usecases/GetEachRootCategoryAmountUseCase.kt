package com.charmflex.flexiexpensesmanager.features.category.category.domain.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.features.category.category.domain.models.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

internal class GetEachRootCategoryAmountUseCase @Inject constructor(
    private val categoryRepository: TransactionCategoryRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(tagFilter: List<Int> = listOf(), dateFilter: DateFilter? = null, transactionType: TransactionType): Flow<Map<CategoryHolder, Long>?> {
        val startDate = dateFilter.getStartDate()
        val endDate = dateFilter.getEndDate()

        return categoryRepository.getAllExpensesCategoryTransactionAmount(startDate, endDate).transformLatest { nodeList ->
            val res: MutableMap<CategoryHolder, Long> = mutableMapOf()
            nodeList.forEach {
                res[CategoryHolder(it.categoryId, it.categoryName)] = it.adjustedExpensesAmountInCent
            }
            emit(res)
        }
    }
}

internal data class CategoryHolder(
    val id: Int,
    val name: String,
)

internal fun buildCategoryToRootMapping(rootBasicCategoryNode: TransactionCategories.BasicCategoryNode, category: TransactionCategories.BasicCategoryNode, currentMap: MutableMap<CategoryHolder, CategoryHolder>): Map<CategoryHolder, CategoryHolder> {
    currentMap[CategoryHolder(category.categoryId, category.categoryName)] = CategoryHolder(rootBasicCategoryNode.categoryId, rootBasicCategoryNode.categoryName)
    category.children.forEach {
        buildCategoryToRootMapping(rootBasicCategoryNode, it, currentMap)
    }

    return currentMap
}