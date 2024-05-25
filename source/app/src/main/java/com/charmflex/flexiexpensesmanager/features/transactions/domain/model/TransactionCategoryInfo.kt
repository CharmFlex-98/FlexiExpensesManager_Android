package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionCategoryEntity

internal data class TransactionCategories(
    val items: List<Node>
) {
    internal data class Node(
        val categoryId: Int,
        val categoryName: String,
        val level: Int,
        val parentNodeId: Int?,
        val childNodes: List<Node>
    ) {
        val isRoot get() = parentNodeId == null
        val isLeaf get() = childNodes.isEmpty()

        val allowSubCategory get() = level < 3
    }
}

internal data class TransactionCategory(
    val id: Int = 0,
    val transactionTypeCode: String,
    val name: String,
    val parentId: Int,
    val isDeleted: Boolean = false
)