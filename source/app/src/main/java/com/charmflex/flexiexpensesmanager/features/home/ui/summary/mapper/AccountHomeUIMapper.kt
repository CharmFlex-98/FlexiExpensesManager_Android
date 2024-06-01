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
    private val userCurrencyRepository: UserCurrencyRepository
) :
    Mapper<List<AccountGroupSummary>, List<AccountHomeViewState.AccountGroupSummaryUI>> {
    override suspend fun map(from: List<AccountGroupSummary>): List<AccountHomeViewState.AccountGroupSummaryUI> {
        val mainCurrency = userCurrencyRepository.getPrimaryCurrency()
        return from.map {
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
                        textColor = if (it.balance < 0) Color.Red else Color.Green
                    )
                },
                balance = currencyFormatter.format(it.balance, mainCurrency),
                balanceInCent = it.balance,
                textColor = if (it.balance < 0) Color.Red else Color.Green
            )
        }
    }

}