package com.charmflex.flexiexpensesmanager.features.account.domain.repositories
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import kotlinx.coroutines.flow.Flow

internal interface AccountRepository {
    suspend fun getAccountById(id: Int): AccountGroup.Account
    fun getAllAccounts(): Flow<List<AccountGroup>>

    fun getAccountsSummary(
        startDate: String? = null,
        endDate: String? = null
    ): Flow<List<AccountGroupSummary>>

    suspend fun addAccountGroup(accountGroupName: String)

    suspend fun deleteAccountGroup(accountGroupId: Int)

    suspend fun addAccount(accountName: String, accountGroupId: Int, accountAmount: Long, currency: String, rate: Float)

    suspend fun deleteAccount(accountId: Int)

}