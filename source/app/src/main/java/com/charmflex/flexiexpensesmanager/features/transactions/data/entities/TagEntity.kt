package com.charmflex.flexiexpensesmanager.features.transactions.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("tag_name")
    val tagName: String
)