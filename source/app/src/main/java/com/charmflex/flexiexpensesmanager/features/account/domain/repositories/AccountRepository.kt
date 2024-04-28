package com.charmflex.flexiexpensesmanager.features.account.domain.repositories

import com.charmflex.flexiexpensesmanager.features.account.domain.model.Account

internal interface AccountRepository {
    suspend fun getAllAccounts(): List<Account>
}