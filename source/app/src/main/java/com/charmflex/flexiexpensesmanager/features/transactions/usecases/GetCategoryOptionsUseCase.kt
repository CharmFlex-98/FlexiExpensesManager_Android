package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.CategorySelectionItem
import javax.inject.Inject

internal class GetCategoryOptionsUseCase @Inject constructor(
    private val transactionCategoryRepository: TransactionCategoryRepository
) {
//    suspend operator fun invoke(transactionTypeCode: String): List<CategorySelectionItem> {
//        return transactionCategoryRepository.getAllCategories(transactionTypeCode).items.map {
//            CategorySelectionItem(
//                id = it.categoryId.toString(),
//                title = it.categoryName
//            )
//        }
//    }
}