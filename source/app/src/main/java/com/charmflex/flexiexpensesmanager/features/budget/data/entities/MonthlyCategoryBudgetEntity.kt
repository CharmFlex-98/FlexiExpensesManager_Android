package com.charmflex.flexiexpensesmanager.features.budget.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("budget_month_year", unique = true)
    ]
)
internal data class MonthlyCategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo("budget_month_year")
    val budgetMonthYear: String,
    @ColumnInfo("custom_budget_in_cent")
    val customBudgetInCent: Long,
    @ColumnInfo("category_budget_id")
    val categoryBudgetId: Int,
)