package com.charmflex.flexiexpensesmanager.features.home.usecases

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetCategoryPercentageUseCase @Inject constructor(
    private val categoryRepository: TransactionCategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): Map<String, Long>? {
        return transactionRepository.getTransactions().map { list ->
            categoryRepository.getAllCategoriesIncludeDeleted(TransactionType.EXPENSES.name)
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
        }.firstOrNull()
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