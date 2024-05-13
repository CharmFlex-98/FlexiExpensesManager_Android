package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

// This is for updating the currency rate (as source of truth for real time currency rate)
internal class UpdateCurrencyRateUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
) {

    suspend operator fun invoke(): Result<Unit> {
        return resultOf {
            val lastUpdate = currencyRepository.getLastCurrencyRateUpdateTimestamp()
            if (lastUpdate == null) {
                currencyRepository.fetchLatestCurrencyRates()
            } else {
                val duration = Duration.between(lastUpdate, LocalDateTime.now())
                // Only update after 24 hours
                if (duration.toHours() > 24) {
                    currencyRepository.fetchLatestCurrencyRates()
                }
            }
        }
    }
}