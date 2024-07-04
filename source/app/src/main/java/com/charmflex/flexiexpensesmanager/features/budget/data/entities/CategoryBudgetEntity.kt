package com.charmflex.flexiexpensesmanager.features.budget.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("category_id", unique = true)
    ]
)
internal class CategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("category_id")
    val categoryId: Int,
    @ColumnInfo("default_budget_in_cent")
    val defaultBudgetInCent: Long
)