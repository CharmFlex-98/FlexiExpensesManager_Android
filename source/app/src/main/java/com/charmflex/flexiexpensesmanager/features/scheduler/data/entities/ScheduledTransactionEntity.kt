package com.charmflex.flexiexpensesmanager.features.scheduler.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.charmflex.flexiexpensesmanager.features.currency.data.entities.CurrencyMetaDataEntity

@Entity(
    indices = [
        Index("id", "is_deleted")
    ],
    foreignKeys = [
        ForeignKey(
            entity = CurrencyMetaDataEntity::class,
            parentColumns = ["currencyCode"],
            childColumns = ["currency"]
        )
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
    @ColumnInfo("minor_unit_amount")
    val minorUnitAmount: Long,
    @ColumnInfo("start_update_date")
    val startUpdateDate: String,
    @ColumnInfo("next_update_date")
    val nextUpdateDate: String,
    @ColumnInfo("category_id")
    val categoryId: Int?,
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("account_minor_unit_amount", defaultValue = "0")
    val accountMinorUnitAmount: Long,
    @ColumnInfo("primary_minor_unit_amount", defaultValue = "0")
    val primaryMinorUnitAmount: Long,
    @ColumnInfo("scheduler_period")
    val schedulerPeriod: String,
    @ColumnInfo("is_deleted")
    val isDeleted: Boolean = false
)