package com.charmflex.flexiexpensesmanager.features.transactions.data.mapper

import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.features.transactions.data.responses.TransactionResponse
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import javax.inject.Inject

internal class TransactionMapper @Inject constructor() : Mapper<TransactionResponse, Transaction> {
    override suspend fun map(from: TransactionResponse): Transaction {
        return Transaction(
            transactionId = from.transactionId,
            transactionName = from.transactionName,
            transactionDate = from.transactionDate,
            transactionTypeCode = from.transactionTypeCode,
            amountInCent = from.amountInCent,
            currency = from.currency,
            rate = from.rate,
            transactionCategory = getCategory(from.categoryId, from.categoryName),
            transactionAccountFrom = getTransactionAccount(from.accountFromId, from.accountFromName),
            transactionAccountTo = getTransactionAccount(from.accountToId, from.accountToName),
            // TODO: Not supported yet
            tags = listOf()
        )
    }

    private fun getTransactionAccount(accountId: Int?, accountName: String?): Transaction.TransactionAccount? {
        if (accountId == null) return null
        if (accountName == null) return null

        return Transaction.TransactionAccount(
            id = accountId,
            name = accountName
        )
    }

    private fun getCategory(categoryId: Int?, categoryName: String?): Transaction.TransactionCategory? {
        if (categoryId == null) return null
        if (categoryName == null) return null

        return Transaction.TransactionCategory(
            id = categoryId,
            name = categoryName
        )
    }

}