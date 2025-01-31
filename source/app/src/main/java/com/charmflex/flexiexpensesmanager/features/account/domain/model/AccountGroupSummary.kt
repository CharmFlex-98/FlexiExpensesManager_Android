package com.charmflex.flexiexpensesmanager.features.account.domain.model

import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase

internal data class AccountGroupSummary(
    val accountGroupId: Int,
    val accountGroupName: String,
    val accountsSummary: List<AccountSummary>
) {
    data class AccountSummary(
        val accountId: Int,
        val accountName: String,
        val balance: Long,
        val currency: String
    ) {
        suspend fun getPrimaryBalanceInCent(primaryCurrencyRate: Float, currencyRateUseCase: GetCurrencyRateUseCase): Long {
            val rate = currencyRateUseCase.getCurrency(currency, false)?.rate ?: primaryCurrencyRate
            return (balance * rate).toLong()
        }
    }

    suspend fun getPrimaryBalanceCent(primaryCurrencyRate: Float, currencyRateUseCase: GetCurrencyRateUseCase): Long {
        return accountsSummary.map {
            it.getPrimaryBalanceInCent(primaryCurrencyRate, currencyRateUseCase)
        }
            .reduceOrNull { acc, i -> acc + i }?.toLong() ?: 0
    }
    val balance get() = accountsSummary.map { it.balance }.reduceOrNull { acc, i -> acc + i } ?: 0
}