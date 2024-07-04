package com.charmflex.flexiexpensesmanager.features.budget.domain.models

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction


internal data class AdjustedCategoryBudgetNode(
    val category: Transaction.TransactionCategory,
    val parentCategoryId: Int,
    private val defaultBudgetInCent: Long
) {
    val adjustedBudgetInCent: Long
        get() {
            return if (defaultBudgetInCent != 0L) defaultBudgetInCent
            else {
                children.map {
                    it.adjustedBudgetInCent
                }.reduceOrNull { acc, l -> acc + l } ?: 0
            }
        }

    private val _children: MutableList<AdjustedCategoryBudgetNode> = mutableListOf()
    val children get() = _children.toList()

    fun appendChildren(adjustedCategoryBudgetNode: AdjustedCategoryBudgetNode) {
        _children.add(adjustedCategoryBudgetNode)
    }
}