package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories

internal interface TransactionCategoryRepository {

    suspend fun getAllCategories(transactionTypeCode: String): TransactionCategories
}