package com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper

import androidx.compose.ui.graphics.Color
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeViewState
import javax.inject.Inject

internal class AccountHomeUIMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) :
    Mapper<Pair<List<AccountGroupSummary>, String>, List<AccountHomeViewState.AccountGroupSummaryUI>> {
    override fun map(from: Pair<List<AccountGroupSummary>, String>): List<AccountHomeViewState.AccountGroupSummaryUI> {
        val mainCurrency = from.second
        return from.first.map {
            AccountHomeViewState.AccountGroupSummaryUI(
                accountGroupName = it.accountGroupName,
                accountsSummary = it.accountsSummary.map {
                    AccountHomeViewState.AccountGroupSummaryUI.AccountSummaryUI(
                        accountId = it.accountId,
                        accountName = it.accountName,
                        balance = currencyFormatter.format(
                            it.balance,
                            mainCurrency
                        ),
                        balanceInCent = it.balance,
                    )
                },
                balance = currencyFormatter.format(it.balance, mainCurrency),
                balanceInCent = it.balance,
            )
        }
    }

}