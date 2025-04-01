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


interface I1
interface I2
open class C1 : I1
class C3: C1(), I2
fun testing() {
    val res1: I1 = C3();
    val res2: I2 = res1 as I2
}