package com.charmflex.flexiexpensesmanager.features.currency.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class CurrencyMetaDataEntity(
    @PrimaryKey
    val currencyCode: String,
    @ColumnInfo("name")
    val currencyName: String,
    @ColumnInfo(name = "defaultFractionDigit")
    val defaultFractionDigit: Int
)