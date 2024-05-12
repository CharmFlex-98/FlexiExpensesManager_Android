package com.charmflex.flexiexpensesmanager.features.currency.data.local

import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.core.utils.DEFAULT_DATE_TIME_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDateTime
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import java.time.LocalDateTime
import javax.inject.Inject

private const val USER_SET_CURRENCY_RATE_KEY = "user_set_currency_rate"
private const val LAST_CURRENCY_UPDATE_TIMESTAMP_KEY = "last_currency_update_timestamp"

internal interface CurrencyKeyStorage {
    fun getUserSetCurrencyRate(currency: String): Float
    fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime)
    fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime?
}

internal class CurrencyKeyStorageImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : CurrencyKeyStorage {
    override fun getUserSetCurrencyRate(currency: String): Float {
        return sharedPrefs.getFloat("${USER_SET_CURRENCY_RATE_KEY}_$currency", -1f)
    }

    override fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime) {
        return sharedPrefs.setString(
            LAST_CURRENCY_UPDATE_TIMESTAMP_KEY,
            localDateTime.toStringWithPattern(DEFAULT_DATE_TIME_PATTERN)
        )
    }

    override fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime? {
        val res = sharedPrefs.getString(LAST_CURRENCY_UPDATE_TIMESTAMP_KEY, "")
        return if (res.isEmpty()) null
        else res.toLocalDateTime(DEFAULT_DATE_TIME_PATTERN)
    }

}