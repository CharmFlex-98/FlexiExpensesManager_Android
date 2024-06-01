package com.charmflex.flexiexpensesmanager.features.transactions.domain.model

import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag

internal data class ImportTransaction(
    val transactionName: String,
    val transactionAccountFrom: Int?,
    val transactionAccountTo: Int?,
    val transactionTypeCode: String,
    val amountInCent: Long,
    val currency: String,
    val rate: Float,
    val transactionDate: String,
    val transactionCategoryId: Int?,
    val tagIds: List<Int>
)