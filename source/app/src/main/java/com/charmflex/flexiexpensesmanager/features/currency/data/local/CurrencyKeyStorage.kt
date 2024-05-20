package com.charmflex.flexiexpensesmanager.features.currency.data.local

import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.core.utils.DEFAULT_DATE_TIME_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDateTime
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import java.time.LocalDateTime
import javax.inject.Inject

private const val USER_SET_CURRENCY_RATE_KEY = "user_set_currency_rate"
private const val LAST_CURRENCY_UPDATE_TIMESTAMP_KEY = "last_currency_update_timestamp"
private const val PRIMARY_CURRENCY_KEY = "primary_currency_key"
internal const val SECONDARY_CURRENCY_KEY = "secondary_currency_key"
internal interface CurrencyKeyStorage {

    suspend fun setUserSetCurrencyRate(currency: String, rate: Float)
    suspend fun getUserSetCurrencyRate(currency: String): Float
    suspend fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime)
    suspend fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime?
    suspend fun setPrimaryCurrency(currency: String)
    suspend fun getPrimaryCurrency(): String
    suspend fun addSecondaryCurrency(currency: String)
    suspend fun removeSecondaryCurrency(currency: String)
    suspend fun getSecondaryCurrency(): Set<String>
}

internal class CurrencyKeyStorageImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs,
) : CurrencyKeyStorage {
    override suspend fun setUserSetCurrencyRate(currency: String, rate: Float) {
        sharedPrefs.setFloat("${USER_SET_CURRENCY_RATE_KEY}_$currency", rate)
    }

    override suspend fun getUserSetCurrencyRate(currency: String): Float {
        return sharedPrefs.getFloat("${USER_SET_CURRENCY_RATE_KEY}_$currency", -1f)
    }

    override suspend fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime) {
        return sharedPrefs.setString(
            LAST_CURRENCY_UPDATE_TIMESTAMP_KEY,
            localDateTime.toStringWithPattern(DEFAULT_DATE_TIME_PATTERN)
        )
    }

    override suspend fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime? {
        val res = sharedPrefs.getString(LAST_CURRENCY_UPDATE_TIMESTAMP_KEY, "")
        return if (res.isEmpty()) null
        else res.toLocalDateTime(DEFAULT_DATE_TIME_PATTERN)
    }

    override suspend fun setPrimaryCurrency(currency: String) {
        sharedPrefs.setString(PRIMARY_CURRENCY_KEY, currency)
    }

    override suspend fun getPrimaryCurrency(): String {
        return sharedPrefs.getString(PRIMARY_CURRENCY_KEY, "")
    }

    override suspend fun addSecondaryCurrency(currency: String) {
        val res = getSecondaryCurrency().toMutableSet().apply { add(currency) }
        sharedPrefs.setStringSet(SECONDARY_CURRENCY_KEY, res)
    }

    override suspend fun removeSecondaryCurrency(currency: String) {
        val res = getSecondaryCurrency()
        res.toMutableSet().remove(currency)
        sharedPrefs.setStringSet(SECONDARY_CURRENCY_KEY, res)
    }

    override suspend fun getSecondaryCurrency(): Set<String> {
        return sharedPrefs.getStringSet(SECONDARY_CURRENCY_KEY)
    }
}