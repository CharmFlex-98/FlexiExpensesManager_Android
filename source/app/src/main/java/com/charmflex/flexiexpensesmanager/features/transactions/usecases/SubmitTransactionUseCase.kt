package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import javax.inject.Inject

internal class SubmitTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend fun submitExpenses(
        id: Long?,
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionRepository.editTransaction(
                    id = it,
                    name = name,
                    fromAccountId = fromAccountId,
                    toAccountId = null,
                    transactionType = TransactionType.EXPENSES,
                    amount = amount,
                    categoryId = categoryId,
                    transactionDate = transactionDate,
                    currency = currency,
                    accountMinorUnitAmount = accountMinorUnitAmount,
                    primaryMinorUnitAmount = primaryMinorUnitAmount,
                    tagIds = tagIds,
                    schedulerId = null
                )
            } ?: transactionRepository.addTransaction(
                name = name,
                fromAccountId = fromAccountId,
                toAccountId = null,
                transactionType = TransactionType.EXPENSES,
                amount = amount,
                categoryId = categoryId,
                transactionDate = transactionDate,
                currency = currency,
                accountMinorUnitAmount = accountMinorUnitAmount,
                primaryMinorUnitAmount = primaryMinorUnitAmount,
                tagIds = tagIds,
                schedulerId = null
            )
        }
    }

    suspend fun submitIncome(
        id: Long?,
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        primaryMinorUnitAmount: Long
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionRepository.editTransaction(
                    id = it,
                    name = name,
                    fromAccountId = null,
                    toAccountId = toAccountId,
                    transactionType = TransactionType.INCOME,
                    amount = amount,
                    categoryId = categoryId,
                    transactionDate = transactionDate,
                    currency = currency,
                    accountMinorUnitAmount = amount,
                    primaryMinorUnitAmount = primaryMinorUnitAmount,
                    tagIds = listOf(),
                    schedulerId = null
                )
            } ?: transactionRepository.addTransaction(
                name = name,
                fromAccountId = null,
                toAccountId = toAccountId,
                transactionType = TransactionType.INCOME,
                amount = amount,
                categoryId = categoryId,
                transactionDate = transactionDate,
                currency = currency,
                accountMinorUnitAmount = amount,
                primaryMinorUnitAmount = primaryMinorUnitAmount,
                tagIds = listOf(),
                schedulerId = null
            )

        }
    }

    suspend fun submitTransfer(
        id: Long?,
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        accountMinorUnitAmount: Long
    ): Result<Unit> {
        return resultOf {
            id?.let {
                transactionRepository.editTransaction(
                    id = it,
                    name = name,
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    transactionType = TransactionType.TRANSFER,
                    amount = amount,
                    categoryId = null,
                    transactionDate = transactionDate,
                    currency = currency,
                    primaryMinorUnitAmount = 0,
                    accountMinorUnitAmount = accountMinorUnitAmount,
                    tagIds = listOf(),
                    schedulerId = null
                )
            } ?: transactionRepository.addTransaction(
                name = name,
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                transactionType = TransactionType.TRANSFER,
                amount = amount,
                categoryId = null,
                transactionDate = transactionDate,
                currency = currency,
                primaryMinorUnitAmount = 0,
                accountMinorUnitAmount = accountMinorUnitAmount,
                tagIds = listOf(),
                schedulerId = null
            )
        }
    }

    suspend fun submitUpdateAccount(
        id: Long?,
        name: String,
        accountId: Int,
        isIncrement: Boolean,
        amount: Long,
        transactionDate: String
    ): Result<Unit> {
        return resultOf {
            val accountCurrency = accountRepository.getAccountById(accountId).currency
            id?.let {
                transactionRepository.editTransaction(
                    id = it,
                    name = name,
                    fromAccountId = if (isIncrement) null else accountId,
                    toAccountId = if (isIncrement) accountId else null,
                    transactionType = TransactionType.UPDATE_ACCOUNT,
                    amount = amount,
                    categoryId = null,
                    transactionDate = transactionDate,
                    currency = accountCurrency,
                    primaryMinorUnitAmount = 0,
                    accountMinorUnitAmount = amount,
                    tagIds = listOf(),
                    schedulerId = null
                )
            } ?: transactionRepository.addTransaction(
                name = name,
                fromAccountId = if (isIncrement) null else accountId,
                toAccountId = if (isIncrement) accountId else null,
                transactionType = TransactionType.UPDATE_ACCOUNT,
                amount = amount,
                categoryId = null,
                transactionDate = transactionDate,
                currency = accountCurrency,
                primaryMinorUnitAmount = 0,
                accountMinorUnitAmount = amount,
                tagIds = listOf(),
                schedulerId = null
            )
        }
    }
}