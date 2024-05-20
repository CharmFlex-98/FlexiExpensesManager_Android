package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag

internal data class Transaction(
    val transactionId: Long,
    val transactionName: String,
    val transactionAccountFrom: TransactionAccount?,
    val transactionAccountTo: TransactionAccount?,
    val transactionTypeCode: String,
    val amountInCent: Long,
    val currency: String,
    val rate: Float,
    val transactionDate: String,
    val transactionCategory: TransactionCategory?,
    val tags: List<Tag>
) {
    data class TransactionAccount(
        val id: Int,
        val name: String
    )
    data class TransactionCategory(
        val id: Int,
        val name: String
    )
}