package com.charmflex.flexiexpensesmanager.features.category.category.domain.usecases

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

internal class GetTransactionListByCategoryUseCase @Inject constructor(
    private val categoryRepository: TransactionCategoryRepository,
) {
    suspend operator fun invoke(
        list: List<Transaction>,
        categoryId: Int,
        categoryName: String,
        transactionType: TransactionType,
    ): List<Transaction> {
        return categoryRepository.getCategoriesIncludeDeleted(transactionType.name)
            .firstOrNull()?.let {
                val rootCategories = it.items
                val categoryToRootMap = mutableMapOf<CategoryHolder, CategoryHolder>()
                rootCategories.forEach { root ->
                    buildCategoryToRootMapping(
                        rootBasicCategoryNode = root,
                        category = root,
                        categoryToRootMap
                    )
                }

                list
                    .filter { transaction ->
                        transaction.transactionCategory?.let {
                            categoryToRootMap[CategoryHolder(it.id, it.name)] == CategoryHolder(
                                categoryId,
                                categoryName
                            )
                        } ?: false
                    }

            } ?: listOf()
    }
}