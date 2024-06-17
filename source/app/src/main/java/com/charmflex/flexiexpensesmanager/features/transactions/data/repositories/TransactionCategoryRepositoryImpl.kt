package com.charmflex.flexiexpensesmanager.features.transactions.data.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionCategoryDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionCategoryEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategory
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

internal class TransactionCategoryRepositoryImpl @Inject constructor(
    private val transactionCategoryDao: TransactionCategoryDao
) : TransactionCategoryRepository {
    override suspend fun getAllCategoriesIncludedDeleted(): List<TransactionCategory> {
        return transactionCategoryDao.getAllCategoriesIncludedDeleted().firstOrNull()?.map {
            TransactionCategory(
                it.id,
                it.transactionTypeCode,
                it.name,
                it.parentId,
                it.isDeleted
            )
        } ?: listOf()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCategories(transactionTypeCode: String): Flow<TransactionCategories> {
        val res = transactionCategoryDao.getCategories(transactionTypeCode)
        return res.transformLatest {
            emit(it to it.filter { entity -> entity.parentId == 0 })
        }.map { (res, rootItems) ->
            TransactionCategories(
                items = rootItems.map { buildNode(1, res, it, null) }
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCategoriesIncludeDeleted(transactionTypeCode: String): Flow<TransactionCategories> {
        val res = transactionCategoryDao.getCategoriesIncludeDeleted(transactionTypeCode)
        return res.transformLatest {
            emit(it to it.filter { entity -> entity.parentId == 0 })
        }.map { (res, rootItems) ->
            TransactionCategories(
                items = rootItems.map { buildNode(1, res, it, null) }
            )
        }
    }

    override suspend fun addCategory(category: String, parentId: Int, transactionTypeCode: String) {
        val entity = TransactionCategoryEntity(
            name = category,
            parentId = parentId,
            transactionTypeCode = transactionTypeCode
        )
        return transactionCategoryDao.addCategory(entity)
    }

    override fun getCategoryById(categoryId: Int): Transaction.TransactionCategory {
        val res = transactionCategoryDao.getCategoryById(categoryId)
        return Transaction.TransactionCategory(
            res.id,
            res.name
        )
    }

    override suspend fun deleteCategory(categoryId: Int) {
        transactionCategoryDao.deleteCategory(categoryId)
    }

    private fun buildNode(
        level: Int,
        items: List<TransactionCategoryEntity>,
        entity: TransactionCategoryEntity,
        parentNode: TransactionCategories.Node?
    ): TransactionCategories.Node {
        return TransactionCategories.Node(
            level = level,
            categoryId = entity.id,
            categoryName = entity.name,
            parentNode = parentNode
        ).also { node ->
            node.addChildren(
                items
                    .filter { it.parentId == entity.id }
                    .map {
                    buildNode(
                        level = level + 1,
                        items = items,
                        entity = it,
                        parentNode = node
                    )
                }
            )
        }
    }
}