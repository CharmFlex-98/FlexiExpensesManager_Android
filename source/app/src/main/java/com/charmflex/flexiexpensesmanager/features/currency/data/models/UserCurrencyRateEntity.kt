package com.charmflex.flexiexpensesmanager.features.currency.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
internal data class UserCurrencyRateEntity(
    @PrimaryKey
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("rate")
    val rate: Float
)