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
    operator fun invoke(tagFilter: List<Int> = listOf(), dateFilter: DateFilter? = null): Flow<Map<String, Long>?> {
        val startDate = dateFilter.getStartDate()
        val endDate = dateFilter.getEndDate()

        return transactionRepository.getAllTransactions(startDate = startDate, endDate = endDate, tagFilter = tagFilter).map { list ->
            categoryRepository.getCategoriesIncludeDeleted(TransactionType.EXPENSES.name)
                .firstOrNull()?.let {
                    val resMap = mutableMapOf<String, Long>()
                    val rootCategories = it.items
                    val categoryIdsMap = mutableMapOf<String, List<Int>>()
                    rootCategories.forEach { root ->
                        val childrenIds = mutableListOf(root.categoryId)
                        getChildrenCategoryIds(root, childrenIds)
                        categoryIdsMap[root.categoryName] = childrenIds.toList()
                    }

                    list
                        .filter { transaction ->
                            transaction.transactionTypeCode == TransactionType.EXPENSES.name
                        }
                        .forEach {
                            for ((rootCategory, childrenIds) in categoryIdsMap.entries) {
                                if (childrenIds.contains(it.transactionCategory?.id)) {
                                    val currentAmount = resMap.getOrDefault(rootCategory, 0)
                                    resMap[rootCategory] = currentAmount + it.amountInCent
                                    break
                                }
                            }
                        }
                    resMap
                }
        }
    }

    private fun getChildrenCategoryIds(
        node: TransactionCategories.Node,
        children: MutableList<Int>
    ) {
        for (child in node.childNodes) {
            children.add(child.categoryId)
            getChildrenCategoryIds(child, children)
        }
    }
}