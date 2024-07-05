package com.charmflex.flexiexpensesmanager.features.category.category.data.responses

import androidx.room.ColumnInfo


internal data class CategoryTransactionAmountResponse(
    @ColumnInfo("category_id")
    val categoryId: Int,
    @ColumnInfo("category_name")
    val categoryName: String,
    @ColumnInfo("parent_category_id")
    val parentCategoryId: Int,
    @ColumnInfo("expenses_amount_in_cent")
    val expensesAmountInCent: Long
)