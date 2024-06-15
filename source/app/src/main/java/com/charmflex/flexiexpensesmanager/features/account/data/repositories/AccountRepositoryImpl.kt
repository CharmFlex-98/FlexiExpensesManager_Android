package com.charmflex.flexiexpensesmanager.features.account.data.repositories

import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountGroupEntity
import com.charmflex.flexiexpensesmanager.features.account.data.responses.AccountResponse
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
) : AccountRepository {
    override suspend fun getAccountById(id: Int): AccountGroup.Account {
        val res = accountDao.getAccountById(id)
        return AccountGroup.Account(
            accountId = res.id,
            accountName = res.name
        )
    }

    override fun getAllAccounts(): Flow<List<AccountGroup>> {
        return accountDao.getAllAccounts()
            .map {
                val map: MutableMap<Pair<Int, String>, List<AccountResponse>> = mutableMapOf()
                it.forEach { response ->
                    val key = response.accountGroupId to response.accountGroupName
                    if (!map.containsKey(key)) {
                        map[key] = listOf(response)
                    } else {
                        val items =
                            map[key]?.toMutableList() ?: mutableListOf()
                        map[key] = items.also { it.add(response) }
                    }
                }
                map.entries.map { entryMap ->
                    val accountGroupId = entryMap.key.first
                    val accountGroupName = entryMap.key.second
                    val data = entryMap.value
                    AccountGroup(
                        accountGroupId = accountGroupId,
                        accountGroupName = accountGroupName,
                        accounts = data.filter { it.account != null }.map { acc ->
                            AccountGroup.Account(
                                accountId = acc.account!!.accountId,
                                accountName = acc.account.accountName
                            )
                        }
                    )
                }
            }
    }

    override fun getAccountsSummary(
        startDate: String?,
        endDate: String?
    ): Flow<List<AccountGroupSummary>> {
        return accountDao.getAccountsSummary(startDate, endDate)
            .map {
                it.groupBy { res -> res.accountGroupId to res.accountGroupName }
                    .map {
                        val groupName = it.key.second
                        val groupId = it.key.first
                        AccountGroupSummary(
                            accountGroupId = groupId,
                            accountGroupName = groupName,
                            accountsSummary = it.value.filter { child -> child.accountId != null }
                                .map { acc ->
                                    AccountGroupSummary.AccountSummary(
                                        accountId = acc.accountId!!,
                                        accountName = acc.accountName!!,
                                        balance = acc.inAmount - acc.outAmount + acc.initialAmount
                                    )
                                }
                        )
                    }
            }
    }

    override suspend fun addAccountGroup(accountGroupName: String) {
        val accountGroupEntity = AccountGroupEntity(
            name = accountGroupName
        )
        accountDao.insertAccountGroup(accountGroupEntity)
    }

    override suspend fun deleteAccountGroup(accountGroupId: Int) {
        accountDao.deleteAccountGroup(accountGroupId = accountGroupId)
    }

    override suspend fun addAccount(accountName: String, accountGroupId: Int, initialAmount: Long): Long {
        val entity = AccountEntity(
            name = accountName,
            accountGroupId = accountGroupId,
            additionalInfo = null,
            initialAmount = initialAmount
        )

        return accountDao.insertAccount(entity)
    }

    override suspend fun deleteAccount(accountId: Int) {
        accountDao.deleteAccount(accountId = accountId)
    }
}