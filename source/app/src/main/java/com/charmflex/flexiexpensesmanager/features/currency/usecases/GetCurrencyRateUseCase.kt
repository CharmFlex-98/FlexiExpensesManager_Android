package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.service.CurrencyService
import javax.inject.Inject

// Use case to get currency rate
// 1. Check user custom-set currency rate, if not found
// 2. Use real time currency rate saved earlier
internal class GetCurrencyRateUseCase @Inject constructor(
    private val userCurrencyRepository: UserCurrencyRepository,
    private val currencyService: CurrencyService
) {

    suspend fun getPrimaryCurrencyRate(currency: String, customFirst: Boolean = true): CurrencyRate? {
        val fromCurrency = userCurrencyRepository.getPrimaryCurrency()
        return if (customFirst) {
            userCurrencyRepository.getUserSetCurrencyRate(currency = currency)?.let {
                CurrencyRate(
                    name = currency,
                    from = fromCurrency,
                    rate = it,
                    isCustom = true
                )
            } ?: currencyService.getCurrencyRate(currency, fromCurrency)
        } else {
            currencyService.getCurrencyRate(currency, fromCurrency)
        }
    }
}

internal data class CurrencyRate(
    val name: String,
    val from: String,
    val rate: Float,
    val isCustom: Boolean
)