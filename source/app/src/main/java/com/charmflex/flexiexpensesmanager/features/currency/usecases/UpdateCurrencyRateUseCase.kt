package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

internal class UpdateCurrencyRateUseCase @Inject constructor(
    private val fileStorage: FileStorage,
    private val currencyRepository: CurrencyRepository,
    private val currencyKeyStorage: CurrencyKeyStorage
) {
    suspend operator fun invoke(): Result<Unit> {
        return resultOf {
            val lastUpdate = currencyKeyStorage.getLastCurrencyRateUpdateTimestamp()
            if (lastUpdate == null) {
                writeLatestCurrencyRate()
            } else {
                val duration = Duration.between(lastUpdate, LocalDateTime.now())
                // Only update after 24 hours
                if (duration.toHours() > 24) {
                    writeLatestCurrencyRate()
                }
            }
        }
    }

    private suspend fun writeLatestCurrencyRate() {
        val res = currencyRepository.getAvailableCurrency()
        val json = Json.encodeToString(res)
        try {
            fileStorage.write(CURRENCY_FILE_NAME, json.toByteArray())
            currencyKeyStorage.setLastCurrencyRateUpdateTimestamp(LocalDateTime.now())
        } catch (e: Exception) {
            throw e
        }
    }
}

internal const val CURRENCY_FILE_NAME = "currency_rate.txt"