package com.charmflex.flexiexpensesmanager.features.budget.data.responses

import androidx.room.ColumnInfo
import org.checkerframework.checker.units.qual.C

internal data class CategoryBudgetResponse(
    @ColumnInfo("id")
    val id: Int,
    @ColumnInfo("category_id")
    val categoryId: Int,
    @ColumnInfo("category_name")
    val categoryName: String,
    @ColumnInfo("default_budget_in_cent")
    val defaultBudgetInCent: Long
)