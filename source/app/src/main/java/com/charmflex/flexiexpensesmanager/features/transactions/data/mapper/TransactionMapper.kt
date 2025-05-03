package com.charmflex.flexiexpensesmanager.features.transactions.data.mapper

import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.transactions.data.responses.TransactionResponse
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import javax.inject.Inject

internal class TransactionMapper @Inject constructor() : Mapper<TransactionResponse, Transaction> {
    override fun map(from: TransactionResponse): Transaction {
        return Transaction(
            transactionId = from.transactionId,
            transactionName = from.transactionName,
            transactionDate = from.transactionDate,
            transactionTypeCode = from.transactionTypeCode,
            minorUnitAmount = from.minorUnitAmount,
            accountMinorUnitAmount = from.accountMinorUnitAmount,
            primaryMinorUnitAmount = from.primaryMinorUnitAmount,
            currency = from.currency,
            transactionCategory = getCategory(from.categoryId, from.categoryName),
            transactionAccountFrom = getTransactionAccount(from.accountFromId, from.accountFromName, from.accountFromCurrency),
            transactionAccountTo = getTransactionAccount(from.accountToId, from.accountToName, from.accountToCurrency),
            // TODO: Not supported yet
            tags = from.tagIds?.let {
                it.split(", ").mapIndexed { index, it ->
                    Tag(
                        id = it.toInt(),
                        name = from.tagNames?.split(", ")?.get(index) ?: ""
                    )
                }
            } ?: listOf()
        )
    }

    private fun getTransactionAccount(accountId: Int?, accountName: String?, currency: String?): AccountGroup.Account? {
        if (accountId == null) return null
        if (accountName == null) return null
        if (currency == null) return null;

        return AccountGroup.Account(
            accountId = accountId,
            accountName = accountName,
            currency = currency
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