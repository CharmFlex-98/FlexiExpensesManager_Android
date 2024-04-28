package com.charmflex.flexiexpensesmanager.features.account.data.repositories

import com.charmflex.flexiexpensesmanager.business_core.accounts.domain.AccountsStatus
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.account.domain.model.Account
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {
    override suspend fun getAllAccounts(): List<Account> {
        val res = mutableListOf<Account>()
        accountDao.getAllAccounts().forEach {
            it.value.forEach { acc ->
                res.add(
                    Account(
                        accountId = acc.id,
                        accountName = acc.name,
                        accountGroupId = it.key.id,
                        accountGroupName = it.key.name
                    )
                )
            }
        }

        return res
    }
}