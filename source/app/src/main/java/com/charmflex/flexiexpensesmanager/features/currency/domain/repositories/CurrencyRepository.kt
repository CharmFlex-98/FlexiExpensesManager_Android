package com.charmflex.flexiexpensesmanager.features.currency.domain.repositories

import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyData
import java.time.LocalDateTime

internal interface CurrencyRepository {

    suspend fun fetchLatestCurrencyRates(): CurrencyData

    suspend fun getCacheCurrencyRates(): CurrencyData?

    suspend fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime)
    suspend fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime?
}