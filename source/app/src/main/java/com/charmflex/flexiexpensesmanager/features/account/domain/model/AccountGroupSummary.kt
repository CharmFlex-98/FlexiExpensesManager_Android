package com.charmflex.flexiexpensesmanager.features.account.domain.model

import androidx.compose.ui.graphics.Color

internal data class AccountGroupSummary(
    val accountGroupId: Int,
    val accountGroupName: String,
    val accountsSummary: List<AccountSummary>
) {
    data class AccountSummary(
        val accountId: Int,
        val accountName: String,
        val balance: Int
    ) {
        val balanceTextColor = balance.toBalanceColor()
    }

    val balance get() = accountsSummary.map { it.balance }.reduceOrNull { acc, i -> acc + i } ?: 0
    val balanceTextColor get() = balance.toBalanceColor()
}

private fun Int.toBalanceColor(): Color {
    return if (this < 0) Color.Red else Color.Green
}