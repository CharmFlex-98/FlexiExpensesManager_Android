package com.charmflex.flexiexpensesmanager.features.currency.data.repositories

import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.data.remote.CurrencyApi
import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyData
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject

internal class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val fileStorage: FileStorage,
    private val currencyKeyStorage: CurrencyKeyStorage
) : CurrencyRepository {

    override suspend fun fetchLatestCurrencyRates(): CurrencyData {
        val res = currencyApi.getCurrencyRate()
        val item = CurrencyData(
            timestamp = res.timestamp,
            date = res.date,
            base = res.base,
            currencyRates = res.rates.mapValues {
                it.value.toFloat()
            }
        )
        setLatestCurrencyRates(item)
        return item
    }

    private suspend fun setLatestCurrencyRates(currencyData: CurrencyData) {
        val json = Json.encodeToString(currencyData)
        fileStorage.write(CURRENCY_FILE_NAME, json.toByteArray())
        currencyKeyStorage.setLastCurrencyRateUpdateTimestamp(LocalDateTime.now())
    }

    override suspend fun getCacheCurrencyRates(): CurrencyData? {
        return try {
            val res = fileStorage.read(CURRENCY_FILE_NAME)
            Json.decodeFromString<CurrencyData>(res)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun setLastCurrencyRateUpdateTimestamp(localDateTime: LocalDateTime) {
        currencyKeyStorage.setLastCurrencyRateUpdateTimestamp(localDateTime)
    }

    override suspend fun getLastCurrencyRateUpdateTimestamp(): LocalDateTime? {
        return currencyKeyStorage.getLastCurrencyRateUpdateTimestamp()
    }
}

private const val CURRENCY_FILE_NAME = "currency_rate.txt"