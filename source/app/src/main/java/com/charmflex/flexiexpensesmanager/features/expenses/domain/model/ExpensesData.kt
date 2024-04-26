package com.charmflex.flexiexpensesmanager.features.expenses.domain.model

internal data class ExpensesData(
    val name: String,
    val category: String,
    val type: String,
    val amount: Float,
    val timeStamp: String,
)