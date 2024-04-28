package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionCategoryDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionCategoryEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import javax.inject.Inject

internal class TransactionCategoryRepositoryImpl @Inject constructor(
    private val transactionCategoryDao: TransactionCategoryDao
) : TransactionCategoryRepository {
    override suspend fun getAllCategories(transactionTypeCode: String): TransactionCategories {
        val res = transactionCategoryDao.getCategories(transactionTypeCode)
        val rootItems = res.filter { it.parentId == 0 }
        return TransactionCategories(
            items = rootItems.map { buildNode(res, it) }
        )
    }

    private fun buildNode(
        items: List<TransactionCategoryEntity>,
        entity: TransactionCategoryEntity
    ): TransactionCategories.Node {
        return TransactionCategories.Node(
            categoryId = entity.id,
            categoryName = entity.name,
            parentNodeId = entity.parentId,
            childNodes = items.filter { it.parentId == entity.id }.map {
                buildNode(
                    items = items,
                    entity = it
                )
            }
        )
    }
}