package com.charmflex.flexiexpensesmanager.business_core.transactions.domain

internal data class Transaction(
    val name: String,
    val amount: Int,
    val transactionDate: String,
    val transactionTypeInfo: TransactionTypeInfo,
    val remarks: String? = null
) {
    sealed interface TransactionTypeInfo {
        data class Income(
            val toAccount: String
        ) : TransactionTypeInfo
        data class Expenses(
            val fromAccount: String
        ) : TransactionTypeInfo
        data class Transfer(
            val fromAccount: String,
            val toAccount: String
        ) : TransactionTypeInfo
    }
}