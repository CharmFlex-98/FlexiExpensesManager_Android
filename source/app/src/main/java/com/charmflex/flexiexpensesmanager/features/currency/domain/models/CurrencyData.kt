package com.charmflex.flexiexpensesmanager.features.currency.domain.models

import kotlinx.serialization.Serializable


@Serializable
internal data class CurrencyData(
    val timestamp: Long,
    val date: String,
    val base: String,
    val currencyRates: Map<String, Float>
) {
    data class CurrencyRate(
        val name: String,
        val value: Float
    )

    fun toList(): List<CurrencyRate> {
        return currencyRates.toList().map {
            CurrencyRate(
                it.first,
                it.second
            )
        }
    }
}