package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

internal data class TransactionDomainInput(
    val transactionName: String,
    val transactionAccountFrom: Int?,
    val transactionAccountTo: Int?,
    val transactionTypeCode: String,
    val amountInCent: Long,
    val currency: String,
    val rate: Float,
    val primaryCurrencyRate: Float?,
    val transactionDate: String,
    val transactionCategoryId: Int?,
    val tagIds: List<Int>,
    val schedulerId: Int?
)