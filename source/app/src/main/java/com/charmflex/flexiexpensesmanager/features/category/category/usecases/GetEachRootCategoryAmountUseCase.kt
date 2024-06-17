package com.charmflex.flexiexpensesmanager.features.category.category.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetEachRootCategoryAmountUseCase @Inject constructor(
    private val categoryRepository: TransactionCategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(tagFilter: List<Int> = listOf(), dateFilter: DateFilter? = null, transactionType: TransactionType): Flow<Map<CategoryHolder, Long>?> {
        val startDate = dateFilter.getStartDate()
        val endDate = dateFilter.getEndDate()

        return transactionRepository.getTransactions(startDate = startDate, endDate = endDate, tagFilter = tagFilter).map { list ->
            categoryRepository.getCategoriesIncludeDeleted(transactionType.name)
                .firstOrNull()?.let {
                    val rootCategories = it.items
                    val categoryToRootMap = mutableMapOf<CategoryHolder, CategoryHolder>()
                    rootCategories.forEach { root ->
                        buildCategoryToRootMapping(rootNode = root, category = root, categoryToRootMap)
                    }
                    val rootToAmountMap = mutableMapOf<CategoryHolder, Long>()

                    list
                        .filter { transaction ->
                            transaction.transactionTypeCode == transactionType.name
                        }
                        .forEach {
                            val categoryHolderKey = it.transactionCategory?.let { CategoryHolder(it.id, it.name) }
                            val root = categoryToRootMap[categoryHolderKey]
                            root?.let { rootHolder ->
                                val currentAmount = rootToAmountMap.getOrDefault(root, 0)
                                rootToAmountMap[CategoryHolder(rootHolder.id, rootHolder.name)] = currentAmount + it.amountInCent
                            }
                        }
                    rootToAmountMap
                }
        }
    }
}

internal data class CategoryHolder(
    val id: Int,
    val name: String,
)

internal fun buildCategoryToRootMapping(rootNode: TransactionCategories.Node, category: TransactionCategories.Node, currentMap: MutableMap<CategoryHolder, CategoryHolder>): Map<CategoryHolder, CategoryHolder> {
    currentMap[CategoryHolder(category.categoryId, category.categoryName)] = CategoryHolder(rootNode.categoryId, rootNode.categoryName)
    category.childNodes.forEach {
        buildCategoryToRootMapping(rootNode, it, currentMap)
    }

    return currentMap
}