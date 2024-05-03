package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

import androidx.room.ColumnInfo

internal data class Transaction(
    val transactionId: Long,
    val transactionName: String,
    val accountToId: Int?,
    val transactionTypeCode: String,
    val amountInCent: Int,
    val transactionDate: String,
    val categoryId: Int,
    val categoryName: String,
    val tags: List<Tag>
) {
    data class Tag(
        val id: Int,
        val tagName: String
    )
}