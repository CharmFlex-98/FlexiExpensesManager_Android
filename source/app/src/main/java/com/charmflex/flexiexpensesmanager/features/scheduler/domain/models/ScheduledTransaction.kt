package com.charmflex.flexiexpensesmanager.features.scheduler.domain.models

import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionDomainInput
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import kotlinx.serialization.Serializable

@Serializable
internal data class ScheduledTransaction(
    val id: Int = 0,
    val transactionName: String,
    val accountFrom: AccountGroup.Account?,
    val accountTo: AccountGroup.Account?,
    val transactionType: TransactionType,
    val minorUnitAmount: Long,
    val startUpdateDate: String,
    val nextUpdateDate: String,
    val category: Transaction.TransactionCategory?,
    val currency: String,
    val rate: Float,
    val primaryCurrencyRate: Float?,
    val accountMinorUnitAmount: Long,
    val primaryMinorUnitAmount: Long,
    val schedulerPeriod: SchedulerPeriod,
    val tags: List<Tag>
) : SchedulerDomainModel {
    fun toTransactionDomainInput(transactionDate: String, schedulerId: Int): TransactionDomainInput {
        return TransactionDomainInput(
            transactionName = this.transactionName,
            transactionAccountFrom = this.accountFrom?.accountId,
            transactionAccountTo = this.accountTo?.accountId,
            transactionTypeCode = transactionType.name,
            minorUnitAmount = minorUnitAmount,
            transactionCategoryId = category?.id,
            transactionDate = transactionDate,
            currency = currency,
            rate = rate,
            primaryCurrencyRate = primaryCurrencyRate,
            accountMinorUnitAmount = accountMinorUnitAmount,
            primaryMinorUnitAmount = primaryMinorUnitAmount,
            tagIds = tags.map { it.id },
            schedulerId = schedulerId
        )
    }
}