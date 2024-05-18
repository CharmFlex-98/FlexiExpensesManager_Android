package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

internal class GetUserCurrencyUseCase @Inject constructor(
    private val userCurrencyRepository: UserCurrencyRepository,
    private val getCurrencyUseCase: GetCurrencyRateUseCase
) {

    suspend operator fun invoke(): Result<List<CurrencyRate>> {
        return resultOf {
            val rates = userCurrencyRepository.getSecondaryCurrency()
            rates.mapNotNull { getCurrencyUseCase(it) }
        }
    }
}