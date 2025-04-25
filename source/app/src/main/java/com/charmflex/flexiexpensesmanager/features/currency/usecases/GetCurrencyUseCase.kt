package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

// Get user set primary and secondary currency and it's rate
internal class GetCurrencyUseCase @Inject constructor(
    private val userCurrencyRepository: UserCurrencyRepository,
    private val getCurrencyUseCase: GetCurrencyRateUseCase,
) {

    suspend fun secondary(): Result<List<CurrencyRate>> {
        return resultOf {
            val rates = userCurrencyRepository.getSecondaryCurrency()
            rates.mapNotNull { getCurrencyUseCase.getPrimaryCurrencyRate(it) }
        }
    }

    suspend fun primary(): Result<CurrencyRate?> {
        return resultOf {
            val rate = userCurrencyRepository.getPrimaryCurrency()
            getCurrencyUseCase.getPrimaryCurrencyRate(rate)
        }
    }
}