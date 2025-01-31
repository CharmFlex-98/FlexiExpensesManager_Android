package com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper

import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeViewState
import javax.inject.Inject

internal class AccountHomeUIMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val currencyRateUseCase: GetCurrencyRateUseCase
) {
     suspend fun map(from: Pair<List<AccountGroupSummary>, String>): List<AccountHomeViewState.AccountGroupSummaryUI> {
        val mainCurrency = from.second
        val rate = currencyRateUseCase.getCurrency(mainCurrency)?.rate ?: 1f
        return from.first.map {
            val mainCurrencyBalance = it.getPrimaryBalanceCent(rate , currencyRateUseCase)
            AccountHomeViewState.AccountGroupSummaryUI(
                accountGroupName = it.accountGroupName,
                accountsSummary = it.accountsSummary.map {
                    val mainCurrencyBalanceChild = it.getPrimaryBalanceInCent(rate , currencyRateUseCase)
                    AccountHomeViewState.AccountGroupSummaryUI.AccountSummaryUI(
                        accountId = it.accountId,
                        accountName = it.accountName,
                        balance = currencyFormatter.format(
                            it.balance,
                            it.currency
                        ),
                        balanceInCent = it.balance,
                        currency = it.currency,
                        mainCurrencyBalanceInCent = mainCurrencyBalanceChild,
                        mainCurrencyBalance = currencyFormatter.format(
                            mainCurrencyBalanceChild,
                            mainCurrency
                        ),
                        isCurrencyPrimary = mainCurrency == it.currency
                    )
                },
                balance = currencyFormatter.format(mainCurrencyBalance, mainCurrency),
                balanceInCent = mainCurrencyBalance,
            )
        }
    }

}