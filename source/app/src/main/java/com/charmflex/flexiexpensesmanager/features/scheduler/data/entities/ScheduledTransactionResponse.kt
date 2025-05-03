package com.charmflex.flexiexpensesmanager.features.scheduler.data.entities

import androidx.room.ColumnInfo

internal data class ScheduledTransactionResponse(
    @ColumnInfo("scheduler_id")
    val schedulerId: Long,
    @ColumnInfo("scheduled_transaction_name")
    val scheduledTransactionName: String,
    @ColumnInfo("scheduled_account_from_id")
    val scheduledAccountFromId: Int?,
    @ColumnInfo("account_from_name")
    val scheduledAccountFromName: String?,
    @ColumnInfo("account_from_currency")
    val scheduledAccountFromCurrency: String?,
    @ColumnInfo("scheduled_account_to_id")
    val scheduledAccountToId: Int?,
    @ColumnInfo("account_to_name")
    val scheduledAccountToName: String?,
    @ColumnInfo("account_to_currency")
    val scheduledAccountToCurrency: String?,
    @ColumnInfo("transaction_type_code")
    val transactionTypeCode: String,
    @ColumnInfo("minor_unit_amount")
    val minorUnitAmount: Long,
    @ColumnInfo("start_update_date")
    val startUpdateDate: String,
    @ColumnInfo("next_update_date")
    val nextUpdateDate: String,
    @ColumnInfo("category_id")
    val categoryId: Int?,
    @ColumnInfo("category_name")
    val categoryName: String?,
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("account_minor_unit_amount")
    val accountMinorUnitAmount: Long,
    @ColumnInfo("primary_minor_unit_amount")
    val primaryMinorUnitAmount: Long,
    @ColumnInfo("tag_ids")
    val tagIds: String?,
    @ColumnInfo("tag_names")
    val tagNames: String?,
    @ColumnInfo("scheduler_period")
    val schedulerPeriod: String
)