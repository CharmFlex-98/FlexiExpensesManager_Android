package com.charmflex.flexiexpensesmanager.features.currency.domain.repositories


internal interface UserCurrencyRepository {
    suspend fun setUserSetCurrencyRate(currency: String, rate: Float)
    suspend fun getUserSetCurrencyRate(currency: String): Float?
    suspend fun removeUserSetCurrencyRate(currency: String)

    // This is useful when main currency is changed.
    suspend fun resetUserSetCurrencyRate()
    suspend fun setPrimaryCurrency(currency: String)
    suspend fun getPrimaryCurrency(): String
    suspend fun addSecondaryCurrency(currency: String)
    suspend fun removeSecondaryCurrency(currency: String)
    suspend fun getSecondaryCurrency(): Set<String>
}