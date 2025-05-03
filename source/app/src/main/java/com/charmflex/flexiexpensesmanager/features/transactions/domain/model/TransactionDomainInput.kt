package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

internal data class TransactionDomainInput(
    val transactionName: String,
    val transactionAccountFrom: Int?,
    val transactionAccountTo: Int?,
    val transactionTypeCode: String,
    val minorUnitAmount: Long,
    val currency: String,
    val accountMinorUnitAmount: Long,
    val primaryMinorUnitAmount: Long,
    val transactionDate: String,
    val transactionCategoryId: Int?,
    val tagIds: List<Int>,
    val schedulerId: Int?
)