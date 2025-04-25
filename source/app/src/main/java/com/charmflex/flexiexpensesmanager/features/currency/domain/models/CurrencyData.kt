package com.charmflex.flexiexpensesmanager.features.currency.domain.models

import kotlinx.serialization.Serializable


@Serializable
internal data class CurrencyData(
    val timestamp: Long,
    val date: String,
    val base: String,
    val currencyRates: Map<String, Currency>
) {
    @Serializable
    data class Currency(
        val name: String,
        val fractionDigit: Int,
        val rate: Float
    )
}