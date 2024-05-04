package com.charmflex.flexiexpensesmanager.features.transactions.domain.model


internal data class Transaction(
    val transactionId: Long,
    val transactionName: String,
    val transactionAccountFrom: TransactionAccount?,
    val transactionAccountTo: TransactionAccount?,
    val transactionTypeCode: String,
    val amountInCent: Int,
    val transactionDate: String,
    val transactionCategory: TransactionCategory?,
    val tags: List<Tag>
) {

    data class TransactionAccount(
        val id: Int,
        val name: String
    )
    data class Tag(
        val id: Int,
        val tagName: String
    )
    data class TransactionCategory(
        val id: Int,
        val name: String
    )
}