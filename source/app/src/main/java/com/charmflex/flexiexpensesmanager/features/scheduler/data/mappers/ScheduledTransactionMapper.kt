package com.charmflex.flexiexpensesmanager.features.scheduler.data.mappers

import com.charmflex.flexiexpensesmanager.core.utils.Mapper
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionResponse
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.ScheduledTransaction
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType

internal class ScheduledTransactionMapper :
    Mapper<ScheduledTransactionResponse, ScheduledTransaction> {
    override fun map(from: ScheduledTransactionResponse): ScheduledTransaction {
        return ScheduledTransaction(
            id = from.schedulerId.toInt(),
            transactionName = from.scheduledTransactionName,
            accountFrom = getAccount(from.scheduledAccountFromId,from.scheduledAccountFromName, from.scheduledAccountFromCurrency),
            accountTo = getAccount(from.scheduledAccountFromId, from.scheduledAccountToName, from.scheduledAccountToCurrency),
            transactionType = TransactionType.fromString(from.transactionTypeCode),
            startUpdateDate = from.startUpdateDate,
            nextUpdateDate = from.nextUpdateDate,
            category = getCategory(from.categoryId, from.categoryName),
            amountInCent = from.minorUnitAmount,
            schedulerPeriod = SchedulerPeriod.fromString(from.schedulerPeriod),
            tags = getTags(from.tagIds?.split(", "), from.tagNames?.split(", ")),
            currency = from.currency,
            rate = from.accountCurrencyRate,
            primaryCurrencyRate = from.primaryCurrencyRate
        )
    }
}


private fun getAccount(id: Int?, name: String?, currency: String?): AccountGroup.Account? {
    if (id == null || name == null || currency == null) return null
    return AccountGroup.Account(id, name, currency)
}

private fun getCategory(id: Int?, name: String?): Transaction.TransactionCategory? {
    if (id == null || name == null) return null
    return Transaction.TransactionCategory(id, name)
}

private fun getTags(id: List<String>?, name: List<String>?): List<Tag> {
    if (id.isNullOrEmpty() || name.isNullOrEmpty()) return listOf()
    if (id.size != name.size) return listOf()

    return id.mapIndexed { ind, item ->
        Tag(item.toInt(), name[ind])
    }
}