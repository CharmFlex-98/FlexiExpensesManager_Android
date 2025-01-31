package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

// Use case to get currency rate
// 1. Check user custom-set currency rate, if not found
// 2. Use real time currency rate saved earlier
internal class GetCurrencyRateUseCase @Inject constructor(
    private val userCurrencyRepository: UserCurrencyRepository,
    private val currencyRepository: CurrencyRepository,
) {

    suspend fun getCurrency(currency: String, customFirst: Boolean = true): CurrencyRate? {
        val fromCurrency = userCurrencyRepository.getPrimaryCurrency()
        return if (customFirst) {
            userCurrencyRepository.getUserSetCurrencyRate(currency = currency)?.let {
                CurrencyRate(
                    name = currency,
                    from = fromCurrency,
                    rate = it,
                    isCustom = true
                )
            } ?: getLatestCurrencyRate(currency, fromCurrency)
        } else {
            getLatestCurrencyRate(currency, fromCurrency)
        }
    }

    suspend fun getAll(): List<CurrencyRate> {
        val fromCurrency = userCurrencyRepository.getPrimaryCurrency()
        val fromCurrencyRate =
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.get(fromCurrency)
        val currencyRates =
            currencyRepository.getCacheCurrencyRates()?.currencyRates
        return currencyRates?.let {
            it.map { r ->
                CurrencyRate(
                    r.key,
                    fromCurrency,
                    fromCurrencyRate?.div(r.value) ?: 1f,
                    false
                )
            }
        } ?: listOf()
    }

    private suspend fun getLatestCurrencyRate(currency: String, fromCurrency: String): CurrencyRate? {
        val toCurrencyRate =
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.get(currency)
        val fromCurrencyRate =
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.get(fromCurrency)

        if (toCurrencyRate == null || fromCurrencyRate == null) {
            return CurrencyRate(
                name = currency,
                from = fromCurrency,
                rate = 1f,
                isCustom = true
            )
        }

        return CurrencyRate(
            name = currency,
            from = fromCurrency,
            rate = fromCurrencyRate / toCurrencyRate,
            isCustom = false
        )
    }
}

internal data class CurrencyRate(
    val name: String,
    val from: String,
    val rate: Float,
    val isCustom: Boolean
)