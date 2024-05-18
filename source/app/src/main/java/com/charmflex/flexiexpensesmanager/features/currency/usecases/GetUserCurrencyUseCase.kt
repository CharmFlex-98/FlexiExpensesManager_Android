package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.domain.models.CurrencyData
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

internal class GetUserCurrencyUseCase @Inject constructor(
    private val userCurrencyRepository: UserCurrencyRepository,
    private val getUserCurrencyUseCase: GetCurrencyRateUseCase
) {

    suspend operator fun invoke(): Result<List<CurrencyData.CurrencyRate>> {
        return resultOf {
            val rates = userCurrencyRepository.getSecondaryCurrency()
            rates.mapNotNull { currencyCode ->
                val res = getUserCurrencyUseCase(currencyCode)
                res?.let { rate ->
                    CurrencyData.CurrencyRate(
                        name = currencyCode,
                        value = rate
                    )
                }
            }
        }
    }
}