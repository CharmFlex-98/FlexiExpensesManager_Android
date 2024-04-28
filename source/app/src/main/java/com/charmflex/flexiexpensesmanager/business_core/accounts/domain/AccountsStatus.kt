package com.charmflex.flexiexpensesmanager.business_core.accounts.domain

internal data class AccountsStatus(
    val accounts: List<Account>,
) {
    data class Account(
        val name: String,
        val parentAccountGroup: String,
        val balance: Int,
        val remarks: String? = null
    )
}