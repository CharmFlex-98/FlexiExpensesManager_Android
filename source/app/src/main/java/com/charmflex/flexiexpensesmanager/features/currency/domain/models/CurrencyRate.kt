package com.charmflex.flexiexpensesmanager.features.currency.domain.models


internal data class CurrencyRate(
    val success: String,
    val terms: String,
    val privacy: String,
    val timestamp: Long,
    val date: String,
    val base: String,
    val rates: List<Currency>
) {
    data class Currency(
        val currency: String,
        val rate: Float
    )
}
