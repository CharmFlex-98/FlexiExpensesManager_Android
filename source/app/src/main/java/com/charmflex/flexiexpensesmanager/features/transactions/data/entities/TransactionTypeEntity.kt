package com.charmflex.flexiexpensesmanager.features.transactions.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// This should be fix. No change is allowed
@Entity(
    indices = [
        Index("code", unique = true)
    ]
)
internal data class TransactionTypeEntity(
    @PrimaryKey(true)
    val id: Int = 0,
    @ColumnInfo("code")
    val code: String
)