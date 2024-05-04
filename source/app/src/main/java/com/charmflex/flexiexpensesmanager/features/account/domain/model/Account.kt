package com.charmflex.flexiexpensesmanager.features.account.domain.model


internal data class AccountGroup(
    val accountGroupId: Int,
    val accountGroupName: String,
    val accounts: List<Account>
) {
    data class Account(
        val accountId: Int,
        val accountName: String,
    )
}