package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

internal data class TransactionCategories(
    val items: List<Node>
) {
    internal data class Node(
        val categoryId: Int,
        val categoryName: String,
        val level: Int,
    ) {
        private val _childNodes: MutableList<Node> = mutableListOf()
        val childNodes get() = _childNodes.toList()
        fun addChildren(nodes: List<Node>) {
            _childNodes.addAll(nodes)
        }

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