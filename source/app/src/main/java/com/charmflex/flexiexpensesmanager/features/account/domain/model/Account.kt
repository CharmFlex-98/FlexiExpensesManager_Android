package com.charmflex.flexiexpensesmanager.features.account.domain.model

internal data class Account(
    val accountId: Int,
    val accountName: String,
    val accountGroupId: Int,
    val accountGroupName: String,
)