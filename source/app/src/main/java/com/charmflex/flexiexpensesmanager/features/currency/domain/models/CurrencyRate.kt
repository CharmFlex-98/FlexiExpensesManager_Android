package com.charmflex.flexiexpensesmanager.features.currency.domain.models


internal data class CurrencyRate(
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
