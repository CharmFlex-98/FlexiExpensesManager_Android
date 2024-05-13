package com.charmflex.flexiexpensesmanager.features.currency.domain.repositories

import java.time.LocalDateTime

internal interface UserCurrencyRepository {
    fun getUserSetCurrencyRate(currency: String): Float
    fun addSecondaryCurrency(currency: String)
    fun removeSecondaryCurrency(currency: String)
    fun getSecondaryCurrency(): Set<String>
}