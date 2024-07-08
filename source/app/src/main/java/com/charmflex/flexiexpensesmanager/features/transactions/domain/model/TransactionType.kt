package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

// TODO: Need to handle locale
internal enum class TransactionType {
    EXPENSES, INCOME, TRANSFER, UPDATE_ACCOUNT;

    companion object {
        fun fromString(value: String): TransactionType {
            return when (value) {
                EXPENSES.name -> EXPENSES
                INCOME.name -> INCOME
                TRANSFER.name -> TRANSFER
                UPDATE_ACCOUNT.name -> UPDATE_ACCOUNT
                else -> EXPENSES
            }
        }
    }
}