package com.charmflex.flexiexpensesmanager.features.currency.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.sql.Timestamp

@JsonClass(generateAdapter = true)
data class CurrencyRateResponse(
    val success: String,
    val terms: String,
    val privacy: String,
    val timestamp: Long,
    val date: String,
    val base: String,
    val rates: Map<String, Double>
)