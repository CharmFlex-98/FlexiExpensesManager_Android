package com.charmflex.flexiexpensesmanager.features.currency.service

import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.CurrencyRate
import javax.inject.Inject

internal class CurrencyService @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun getAllCurrencies(): List<String> {
        val data = currencyRepository.getCacheCurrencyRates()
        return data?.currencyRates?.map { it.key } ?: emptyList()
    }

    suspend fun getCurrencyRate(currency: String, fromCurrency: String): CurrencyRate? {
        val toCurrencyRate =
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.get(currency)?.rate
        val fromCurrencyRate =
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.get(fromCurrency)?.rate

        if (toCurrencyRate == null || fromCurrencyRate == null) {
            return null
        }

        return CurrencyRate(
            name = currency,
            from = fromCurrency,
            rate = toCurrencyRate / fromCurrencyRate,
            isCustom = true
        )
    }
}