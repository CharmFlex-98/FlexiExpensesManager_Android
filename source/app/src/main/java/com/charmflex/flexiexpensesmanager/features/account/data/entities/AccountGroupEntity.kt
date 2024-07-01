package com.charmflex.flexiexpensesmanager.features.account.data.entities

import androidx.compose.ui.util.trace
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("name", unique = true)
    ]
)
internal data class AccountGroupEntity(
    @PrimaryKey(true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo("is_deleted", defaultValue = "false")
    val isDeleted: Boolean = false
)