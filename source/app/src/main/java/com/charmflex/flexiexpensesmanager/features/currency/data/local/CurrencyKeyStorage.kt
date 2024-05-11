package com.charmflex.flexiexpensesmanager.features.currency.data.local

import android.content.SharedPreferences
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import javax.inject.Inject

private const val USER_SET_CURRENCY_RATE_KEY = "user_set_currency_rate"

internal interface CurrencyKeyStorage {
    fun getUserSetCurrencyRate(currency: String): Float
}

internal class CurrencyKeyStorageImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : CurrencyKeyStorage {
    override fun getUserSetCurrencyRate(currency: String): Float {
        return sharedPrefs.getFloat("${USER_SET_CURRENCY_RATE_KEY}_$currency", -1f)
    }

}