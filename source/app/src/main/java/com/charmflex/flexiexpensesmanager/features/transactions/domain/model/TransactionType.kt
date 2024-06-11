package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

internal enum class TransactionType {
    EXPENSES, INCOME, TRANSFER;

    companion object {
        fun fromString(value: String): TransactionType {
            return when (value) {
                EXPENSES.name -> EXPENSES
                INCOME.name -> INCOME
                TRANSFER.name -> TRANSFER
                else -> EXPENSES
            }
        }
    }
}