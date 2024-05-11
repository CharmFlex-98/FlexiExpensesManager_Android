package com.charmflex.flexiexpensesmanager.features.currency.domain.repositories

import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyRate

internal interface CurrencyRepository {
    suspend fun getAvailableCurrency(): CurrencyRate
    suspend fun getCurrencyRate(currency: String): Float?
}