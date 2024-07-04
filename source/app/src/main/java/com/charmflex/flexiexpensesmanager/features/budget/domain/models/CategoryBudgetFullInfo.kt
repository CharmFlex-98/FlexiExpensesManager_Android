package com.charmflex.flexiexpensesmanager.features.budget.domain.models

internal data class CategoryBudgetFullInfo(
    val categoryId: Int,
    val categoryName: String,
    val categoryParentId: Int,
    val budget: BudgetDomainModel?,
) {
    data class BudgetDomainModel(
        val categoryBudgetId: Int,
        val defaultBudgetInCent: Long,
        val customMonthlyBudgets: List<CustomMonthlyBudgetDomainModel> = emptyList()
    )

    data class CustomMonthlyBudgetDomainModel(
        val budgetMonthYear: String,
        val customBudgetInCent: Long
    )
}