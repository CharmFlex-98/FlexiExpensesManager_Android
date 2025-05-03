package com.charmflex.flexiexpensesmanager.features.account.data.repositories

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.RateExchangeManager
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.core.utils.unwrapResult
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountTransactionDao
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountGroupEntity
import com.charmflex.flexiexpensesmanager.features.account.data.responses.AccountResponse
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.constants.CurrencyDefaults
import com.charmflex.flexiexpensesmanager.features.currency.service.CurrencyService
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyUseCase
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val accountTransactionDao: AccountTransactionDao,
    private val currencyService: CurrencyService,
    private val rateExchangeManager: RateExchangeManager,
    private val getCurrencyUseCase: GetCurrencyUseCase
) : AccountRepository {
    override suspend fun getAccountById(id: Int): AccountGroup.Account {
        val res = accountDao.getAccountById(id)
        return AccountGroup.Account(
            accountId = res.id,
            accountName = res.name,
            currency = res.currency
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
                                accountName = acc.account.accountName,
                                currency = acc.account.currency
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
                val primaryCurrency = unwrapResult(getCurrencyUseCase.primary())?.name ?: CurrencyDefaults.DEFAULT_CURRENCY
                it.groupBy { res -> res.accountGroupId to res.accountGroupName }
                    .map {
                        val groupName = it.key.second
                        val groupId = it.key.first
                        AccountGroupSummary(
                            accountGroupId = groupId,
                            accountGroupName = groupName,
                            accountsSummary = it.value.filter { child -> child.accountId != null }
                                .map { acc ->
                                    val rate = currencyService.getCurrencyRate(primaryCurrency, acc.currency!!)?.rate
                                    val balance = acc.inAmount - acc.outAmount
                                    AccountGroupSummary.AccountSummary(
                                        accountId = acc.accountId!!,
                                        accountName = acc.accountName!!,
                                        balance = balance,
                                        balanceInPrimaryCurrency = rateExchangeManager.convertTo(balance, primaryCurrency, acc.currency, rate ?: 1f).toLong(),
                                        currency = acc.currency,
                                        hasError = rate == null
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

    override suspend fun addAccount(accountName: String, accountGroupId: Int, accountAmount: Long, currency: String) {
        val entity = AccountEntity(
            name = accountName,
            accountGroupId = accountGroupId,
            additionalInfo = null,
            currency = currency
        )

        if (accountAmount == 0L) accountDao.insertAccount(entity)
        else {
            val transactionEntity = TransactionEntity(
                transactionName = "Update Account",
                accountFromId = null,
                accountToId = null,
                transactionTypeCode = "UPDATE_ACCOUNT",
                minorUnitAmount = accountAmount,
                transactionDate = LocalDate.now().toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN),
                categoryId = null,
                currency = currency,
                accountMinorUnitAmount = accountAmount,
                primaryMinorUnitAmount = 0,
                schedulerId = null
            )
            accountTransactionDao.insertAccountAndAmountTransaction(entity, transactionEntity)
        }
    }

    override suspend fun deleteAccount(accountId: Int) {
        accountDao.deleteAccount(accountId = accountId)
    }
}