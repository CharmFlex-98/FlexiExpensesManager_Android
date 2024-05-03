package com.charmflex.flexiexpensesmanager.features.account.domain.repositories
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import kotlinx.coroutines.flow.Flow

internal interface AccountRepository {
    fun getAllAccounts(): Flow<List<AccountGroup>>

    fun getAccountsSummary(): Flow<List<AccountGroupSummary>>

    suspend fun addAccountGroup(accountGroupName: String)

    suspend fun addAccount(accountName: String, accountGroupId: Int)
}