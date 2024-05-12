package com.charmflex.flexiexpensesmanager.features.currency.domain.models

import kotlinx.serialization.Serializable


@Serializable
internal data class CurrencyRate(
    val timestamp: Long,
    val date: String,
    val base: String,
    val rates: Map<String, Float>
)
