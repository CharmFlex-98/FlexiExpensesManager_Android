package com.charmflex.flexiexpensesmanager.features.transactions.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Delete category should not real delete it. Because transaction need a reference to it
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TransactionTypeEntity::class,
            parentColumns = ["code"],
            childColumns = ["transaction_type_code"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TransactionCategoryEntity(
    @PrimaryKey(true)
    val id: Int = 0,
    @ColumnInfo("transaction_type_code", index = true)
    val transactionTypeCode: String,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("parent_id")
    val parentId: Int,
    @ColumnInfo("is_deleted", defaultValue = "false")
    val isDeleted: Boolean = false
)



