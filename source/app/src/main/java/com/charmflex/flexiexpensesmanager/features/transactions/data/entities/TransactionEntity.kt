package com.charmflex.flexiexpensesmanager.features.transactions.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionEntity

// TODO: create index on scheduler ID column
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_from_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_to_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionTypeEntity::class,
            parentColumns = ["code"],
            childColumns = ["transaction_type_code"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScheduledTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduler_id"],
        )
    ]
)
internal data class TransactionEntity(
    @PrimaryKey(true)
    val id: Long = 0L,
    @ColumnInfo("transaction_name")
    val transactionName: String,
    @ColumnInfo("account_from_id", index = true)
    val accountFromId: Int?,
    @ColumnInfo("account_to_id", index = true)
    val accountToId: Int?,
    @ColumnInfo("transaction_type_code", index = true)
    val transactionTypeCode: String,
    @ColumnInfo("amount_in_cent")
    val amountInCent: Long,
    @ColumnInfo("transaction_date")
    val transactionDate: String,
    @ColumnInfo("category_id", index = true)
    val categoryId: Int?,
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("rate")
    val rate: Float,
    @ColumnInfo("scheduler_id")
    val schedulerId: Long?
)