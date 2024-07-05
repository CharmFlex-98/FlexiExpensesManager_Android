package com.charmflex.flexiexpensesmanager.features.category.category.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity

// Delete category should not real delete it. Because transaction need a reference to it
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TransactionTypeEntity::class,
            parentColumns = ["code"],
            childColumns = ["transaction_type_code"],
            onDelete = ForeignKey.RESTRICT
        ),
    ],
    indices = [
        Index("transaction_type_code", "is_deleted")
    ]
)
internal data class TransactionCategoryEntity(
    @PrimaryKey(true)
    val id: Int = 0,
    @ColumnInfo("transaction_type_code")
    val transactionTypeCode: String,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("parent_id")
    val parentId: Int,
    @ColumnInfo("is_deleted", defaultValue = "false")
    val isDeleted: Boolean = false
)



