package com.charmflex.flexiexpensesmanager.features.currency.data.repositories

import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

internal class UserCurrencyRepositoryImpl @Inject constructor(
    private val currencyKeyStorage: CurrencyKeyStorage,
) : UserCurrencyRepository {
    override fun getUserSetCurrencyRate(currency: String): Float {
        return currencyKeyStorage.getUserSetCurrencyRate(currency)
    }

    override fun addSecondaryCurrency(currency: String) {
        currencyKeyStorage.addSecondaryCurrency(currency)
    }

    override fun removeSecondaryCurrency(currency: String) {
        currencyKeyStorage.removeSecondaryCurrency(currency)
    }

    override fun getSecondaryCurrency(): Set<String> {
        return currencyKeyStorage.getSecondaryCurrency()
    }
}