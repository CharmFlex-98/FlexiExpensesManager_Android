package com.charmflex.flexiexpensesmanager.features.scheduler.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("id", "is_deleted")
    ]
)
internal data class ScheduledTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo("scheduled_transaction_name")
    val transactionName: String,
    @ColumnInfo("scheduled_account_from_id")
    val accountFromId: Int?,
    @ColumnInfo("scheduled_account_to_id")
    val accountToId: Int?,
    @ColumnInfo("transaction_type_code")
    val transactionType: String,
    @ColumnInfo("amount_in_cent")
    val amountInCent: Long,
    @ColumnInfo("start_update_date")
    val startUpdateDate: String,
    @ColumnInfo("next_update_date")
    val nextUpdateDate: String,
    @ColumnInfo("category_id")
    val categoryId: Int?,
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("rate")
    val rate: Float,
    @ColumnInfo("primary_currency_rate")
    val primaryCurrencyRate: Float?,
    @ColumnInfo("scheduler_period")
    val schedulerPeriod: String,
    @ColumnInfo("is_deleted")
    val isDeleted: Boolean = false
)