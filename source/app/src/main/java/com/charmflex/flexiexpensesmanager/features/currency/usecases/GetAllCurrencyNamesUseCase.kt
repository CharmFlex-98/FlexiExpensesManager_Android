package com.charmflex.flexiexpensesmanager.features.currency.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import javax.inject.Inject

internal class GetAllCurrencyNamesUseCase@Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    suspend operator fun invoke(): Result<List<String>> {
        return resultOf {
            currencyRepository.getCacheCurrencyRates()?.currencyRates?.map { it.key } ?: listOf()
        }
    }
}