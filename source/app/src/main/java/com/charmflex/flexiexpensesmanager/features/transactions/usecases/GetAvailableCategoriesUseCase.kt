package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import javax.inject.Inject

internal class GetAvailableCategoriesUseCase @Inject constructor(
    private val transactionCategoryRepository: TransactionCategoryRepository
) {
    suspend operator fun invoke(transactionTypeCode: String): TransactionCategories {
        return transactionCategoryRepository.getAllCategories(transactionTypeCode)
    }
}