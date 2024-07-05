package com.charmflex.flexiexpensesmanager.features.budget.domain.models

import com.charmflex.flexiexpensesmanager.features.category.category.domain.CategoryNode


internal data class AdjustedCategoryBudgetNode(
    override val categoryId: Int,
    override val categoryName: String,
    override val parentCategoryId: Int,
    private val expensesInCent: Long,
    private val defaultBudgetInCent: Long
) : CategoryNode<AdjustedCategoryBudgetNode> {
    val adjustedExpensesInCent: Long
        get() {
            return expensesInCent + (children.map {
                it.adjustedExpensesInCent
            }.reduceOrNull { acc, l -> acc + l } ?: 0)
        }

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
    override val children get() = _children.toList()

    override fun addChildren(children: List<AdjustedCategoryBudgetNode>) {
        _children.addAll(children)
    }
}