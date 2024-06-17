package com.charmflex.flexiexpensesmanager.features.category.category.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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
                        rootNode = root,
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