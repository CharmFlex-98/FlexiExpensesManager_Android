package com.charmflex.flexiexpensesmanager.features.currency.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.sql.Timestamp

@JsonClass(generateAdapter = true)
data class CurrencyRateResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "terms")
    val terms: String,
    @Json(name = "privacy")
    val privacy: String,
    val timestamp: Long,
    @Json(name = "date")
    val date: String,
    @Json(name = "base")
    val base: String,
    @Json(name = "rates")
    val rates: Map<String, Double>
)