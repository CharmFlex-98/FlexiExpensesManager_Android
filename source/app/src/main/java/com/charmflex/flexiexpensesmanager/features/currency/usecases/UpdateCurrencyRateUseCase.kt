package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class UpdateCurrencyRateUseCase @Inject constructor(
    private val fileStorage: FileStorage,
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke() {
        val res = currencyRepository.getAvailableCurrency()
        val json = Json.encodeToString(res)
        fileStorage.write(CURRENCY_FILE_NAME, json.toByteArray())
    }
}

internal const val CURRENCY_FILE_NAME = "currency_rate.txt"