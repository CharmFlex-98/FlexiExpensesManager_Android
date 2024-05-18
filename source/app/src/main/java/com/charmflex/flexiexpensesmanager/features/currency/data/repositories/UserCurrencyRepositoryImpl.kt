package com.charmflex.flexiexpensesmanager.features.currency.data.repositories

import com.charmflex.flexiexpensesmanager.features.currency.data.daos.CurrencyDao
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.data.models.UserCurrencyRateEntity
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

private const val DEFAULT_PRIMARY_CURRENCY_CODE = "USD"
internal class UserCurrencyRepositoryImpl @Inject constructor(
    private val currencyKeyStorage: CurrencyKeyStorage,
    private val currencyDao: CurrencyDao
) : UserCurrencyRepository {
    override suspend fun setUserSetCurrencyRate(currency: String, rate: Float) {
        val data = UserCurrencyRateEntity(
            name = currency,
            rate = rate
        )
        return currencyDao.insertUserCurrency(data)
    }

    override suspend fun getUserSetCurrencyRate(currency: String): Float? {
        return currencyDao.getUserCurrency(currency)?.rate
    }

    override suspend fun removeUserSetCurrencyRate(currency: String) {
        return currencyDao.deleteUserCurrency(currency)
    }

    override suspend fun resetUserSetCurrencyRate() {
        currencyDao.removeAllUserCurrency()
    }

    override suspend fun setPrimaryCurrency(currency: String) {
        currencyKeyStorage.setPrimaryCurrency(currency)
    }

    override suspend fun getPrimaryCurrency(): String {
        val res = currencyKeyStorage.getPrimaryCurrency()
        return res.ifEmpty { DEFAULT_PRIMARY_CURRENCY_CODE }
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