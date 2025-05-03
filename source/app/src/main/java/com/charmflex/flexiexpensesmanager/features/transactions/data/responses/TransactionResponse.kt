package com.charmflex.flexiexpensesmanager.features.transactions.data.responses

import androidx.room.ColumnInfo

internal data class TransactionResponse(
    @ColumnInfo("transaction_id")
    val transactionId: Long,
    @ColumnInfo("transaction_name")
    val transactionName: String,
    @ColumnInfo("account_from_id")
    val accountFromId: Int?,
    @ColumnInfo("account_from_name")
    val accountFromName: String?,
    @ColumnInfo("account_from_currency")
    val accountFromCurrency: String?,
    @ColumnInfo("account_to_id")
    val accountToId: Int?,
    @ColumnInfo("account_to_name")
    val accountToName: String?,
    @ColumnInfo("account_to_currency")
    val accountToCurrency: String?,
    @ColumnInfo("transaction_type_code")
    val transactionTypeCode: String,
    @ColumnInfo("minor_unit_amount")
    val minorUnitAmount: Long,
    @ColumnInfo("account_minor_unit_amount")
    val accountMinorUnitAmount: Long,
    @ColumnInfo("primary_minor_unit_amount")
    val primaryMinorUnitAmount: Long,
    @ColumnInfo("transaction_date")
    val transactionDate: String,
    @ColumnInfo("category_id")
    val categoryId: Int?,
    @ColumnInfo("category_name")
    val categoryName: String?,
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("tag_ids")
    val tagIds: String?,
    @ColumnInfo("tag_names")
    val tagNames: String?
)