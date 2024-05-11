package com.charmflex.flexiexpensesmanager.features.currency.data.repositories

import com.charmflex.flexiexpensesmanager.features.currency.data.remote.CurrencyApi
import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyRate
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import javax.inject.Inject

internal class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApi: CurrencyApi,
) : CurrencyRepository {

    override suspend fun getAvailableCurrency(): CurrencyRate {
        val res = currencyApi.getCurrencyRate()
        return CurrencyRate(
            success = res.success,
            terms = res.terms,
            privacy = res.privacy,
            timestamp = res.timestamp,
            date = res.date,
            base = res.base,
            rates = res.rates.map {
                CurrencyRate.Currency(
                    it.key,
                    it.value.toFloat()
                )
            }
        )
    }

    override suspend fun getCurrencyRate(currency: String): Float? {
        return currencyApi.getCurrencyRate().rates[currency]?.toFloat()
    }
}