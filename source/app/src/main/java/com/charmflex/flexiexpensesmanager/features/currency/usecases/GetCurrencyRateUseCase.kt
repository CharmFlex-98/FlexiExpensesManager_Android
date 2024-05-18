package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

// Use case to get currency rate
// 1. Check user custom-set currency rate, if not found
// 2. Use real time currency rate saved earlier
internal class GetCurrencyRateUseCase @Inject constructor(
    private val userCurrencyRepository: UserCurrencyRepository,
    private val currencyRepository: CurrencyRepository,
) {

    suspend operator fun invoke(currency: String): Float? {
        val userSet = userCurrencyRepository.getUserSetCurrencyRate(currency = currency)
        return if (userSet.isValid()) userSet
        else {
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.get(currency)
        }
    }
}

private fun Float.isValid(): Boolean {
    return this >= 0
}