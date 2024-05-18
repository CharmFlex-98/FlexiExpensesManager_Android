package com.charmflex.flexiexpensesmanager.features.currency.domain.repositories

import java.time.LocalDateTime

internal interface UserCurrencyRepository {
    suspend fun setUserSetCurrencyRate(currency: String, rate: Float)
    suspend fun getUserSetCurrencyRate(currency: String): Float
    suspend fun addSecondaryCurrency(currency: String)
    suspend fun removeSecondaryCurrency(currency: String)
    suspend fun getSecondaryCurrency(): Set<String>
}