package com.charmflex.flexiexpensesmanager.features.account.data.daos

import androidx.room.Dao
import androidx.room.Transaction
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity

@Dao
internal interface AccountTransactionDao : TransactionDao, AccountDao {
    @Transaction
    suspend fun insertAccountAndAmountTransaction(
        accountEntity: AccountEntity,
        transaction: TransactionEntity
    ) {
        val accountId = insertAccount(accountEntity)
        val updatedTransaction =
            if (transaction.amountInCent < 0) transaction.copy(accountFromId = accountId.toInt())
            else transaction.copy(accountToId = accountId.toInt())
        insertTransaction(updatedTransaction)
    }
}