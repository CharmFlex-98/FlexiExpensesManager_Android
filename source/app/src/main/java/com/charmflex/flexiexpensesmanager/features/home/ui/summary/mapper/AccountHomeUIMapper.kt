package com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper

import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeViewState
import javax.inject.Inject

internal class AccountHomeUIMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {
    fun map(from: Pair<List<AccountGroupSummary>, String>): List<AccountHomeViewState.AccountGroupSummaryUI> {
        val mainCurrency = from.second
        return from.first.map {
            val mainCurrencyBalance = it.getPrimaryBalance()
            AccountHomeViewState.AccountGroupSummaryUI(
                accountGroupName = it.accountGroupName,
                accountsSummary = it.accountsSummary.map {
                    val mainCurrencyBalanceChild = it.balanceInPrimaryCurrency
                    AccountHomeViewState.AccountGroupSummaryUI.AccountSummaryUI(
                        accountId = it.accountId,
                        accountName = it.accountName,
                        balance = currencyFormatter.format(
                            it.balance,
                            it.currency
                        ),
                        minorUnitBalance = it.balance,
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