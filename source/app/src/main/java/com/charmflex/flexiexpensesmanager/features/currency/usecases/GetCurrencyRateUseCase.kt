package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyRate
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class GetCurrencyRateUseCase @Inject constructor(
    private val keyStorage: CurrencyKeyStorage,
    private val fileStorage: FileStorage
) {

    suspend operator fun invoke(currency: String): Float? {
        val userSet = keyStorage.getUserSetCurrencyRate(currency = currency)
        return if (userSet.isValid()) userSet
        else {
            val cache = fileStorage.read(CURRENCY_FILE_NAME)
            Json.decodeFromString<CurrencyRate>(cache).rates[currency]
        }
    }
}

private fun Float.isValid(): Boolean {
    return this >= 0
}