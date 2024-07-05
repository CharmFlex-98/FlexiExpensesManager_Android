package com.charmflex.flexiexpensesmanager.features.budget.data.responses

import androidx.room.ColumnInfo
import androidx.room.Embedded

internal data class MonthlyCategoryBudgetInfoResponse(
    @ColumnInfo("category_id")
    val categoryId: Int,
    @ColumnInfo("category_name")
    val categoryName: String,
    @ColumnInfo("category_parent_id")
    val categoryParentId: Int,
    @ColumnInfo("expenses_amount_in_cent")
    val expensesAmountInCent: Long,
    @Embedded
    val budget: Budget?,
) {

    data class Budget(
        @ColumnInfo("category_budget_id")
        val categoryBudgetId: Int,
        @ColumnInfo("default_budget_in_cent")
        val defaultBudgetInCent: Long,
        @Embedded
        val customMonthlyBudget: CustomMonthlyBudget?
    )

    data class CustomMonthlyBudget(
        @ColumnInfo("budget_month_year")
        val budgetMonthYear: String,
        @ColumnInfo("custom_budget_in_cent")
        val customBudgetInCent: Long
    )
}