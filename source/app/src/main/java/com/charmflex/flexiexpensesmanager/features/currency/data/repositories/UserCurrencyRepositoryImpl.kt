package com.charmflex.flexiexpensesmanager.features.currency.data.repositories

import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class UserCurrencyRepositoryImpl @Inject constructor(
    private val currencyKeyStorage: CurrencyKeyStorage,
) : UserCurrencyRepository {
    override suspend fun setUserSetCurrencyRate(currency: String, rate: Float) {
        return currencyKeyStorage.setUserSetCurrencyRate(currency, rate)
    }

    override suspend fun getUserSetCurrencyRate(currency: String): Float {
        return currencyKeyStorage.getUserSetCurrencyRate(currency)
    }

    override suspend fun addSecondaryCurrency(currency: String) {
        currencyKeyStorage.addSecondaryCurrency(currency)
    }

    override suspend fun removeSecondaryCurrency(currency: String) {
        currencyKeyStorage.removeSecondaryCurrency(currency)
    }

    override suspend fun getSecondaryCurrency(): Set<String> {
        return currencyKeyStorage.getSecondaryCurrency()
    }
}