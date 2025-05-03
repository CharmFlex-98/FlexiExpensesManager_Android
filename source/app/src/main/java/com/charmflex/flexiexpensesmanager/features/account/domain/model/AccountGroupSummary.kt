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
        val balanceInPrimaryCurrency: Long,
        val currency: String,
        val hasError: Boolean = false
    )

    fun getPrimaryBalance(): Long {
        return accountsSummary.map {
            it.balanceInPrimaryCurrency
        }.reduceOrNull { acc, i -> acc + i }?.toLong() ?: 0
    }
}


interface I1
interface I2
open class C1 : I1
class C3 : C1(), I2

fun testing() {
    val res1: I1 = C3();
    val res2: I2 = res1 as I2
}