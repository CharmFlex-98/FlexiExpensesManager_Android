package com.charmflex.flexiexpensesmanager.features.category.category.domain.models

import com.charmflex.flexiexpensesmanager.features.category.category.domain.CategoryNode

internal class TransactionCategories(
    val items: List<BasicCategoryNode>
) {
    internal data class BasicCategoryNode(
        override val categoryId: Int,
        override val categoryName: String,
        override val parentCategoryId: Int,
        val level: Int,
    ) : CategoryNode<BasicCategoryNode> {
        private val _childBasicCategoryNodes: MutableList<BasicCategoryNode> = mutableListOf()
        override val children get() = _childBasicCategoryNodes.toList()

        override fun addChildren(children: List<BasicCategoryNode>) {
            _childBasicCategoryNodes.addAll(children)
        }

        val isLeaf get() = children.isEmpty()

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