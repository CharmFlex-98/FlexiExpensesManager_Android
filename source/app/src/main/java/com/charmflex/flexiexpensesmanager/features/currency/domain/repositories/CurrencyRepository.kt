package com.charmflex.flexiexpensesmanager.features.currency.domain.repositories

import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyRate
import java.time.LocalDateTime

internal interface CurrencyRepository {

    suspend fun fetchLatestCurrencyRates(): CurrencyRate

    suspend fun getCacheCurrencyRates(): CurrencyRate?

    fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime)
    fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime?
}