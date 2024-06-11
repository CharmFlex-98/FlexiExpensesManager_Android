package com.charmflex.flexiexpensesmanager.features.transactions.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.charmflex.flexiexpensesmanager.features.tag.data.daos.TagDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTagEntity

@Dao
internal interface TransactionTagDao : TransactionDao, TagDao {

    @Insert
    fun insertTransactionTag(transactionTagEntities: List<TransactionTagEntity>)

    @Query("DELETE FROM TransactionTagEntity WHERE transaction_id = :transactionId")
    fun deleteTransactionTagByTransactionId(transactionId: Long)

    @Transaction
    suspend fun insertTransactionAndTransactionTag(transactionEntity: TransactionEntity, tagIds: List<Int>) {
        val transactionId = insertTransaction(transactionEntity)
        val transactionTagEntities = tagIds.map {
            TransactionTagEntity(
                transactionId = transactionId.toInt(),
                tagId = it
            )
        }
        if (transactionTagEntities.isNotEmpty()) {
            insertTransactionTag(transactionTagEntities)
        }
    }


    @Transaction
    suspend fun insertAllTransactionsAndTransactionTags(transactions: List<TransactionEntity>, transactionsTagsIds: List<List<Int>>) {
        val ids = insertAllTransactions(transactions)
        val transactionTagEntities = mutableListOf<TransactionTagEntity>()
        transactionsTagsIds.forEachIndexed { index, transactionTags ->
            val items = transactionTags.map {
                TransactionTagEntity(tagId = it, transactionId = ids[index].toInt())
            }
            if (items.isNotEmpty()) transactionTagEntities.addAll(items)
        }
        insertTransactionTag(transactionTagEntities)
    }

    @Transaction
    suspend fun updateTransactionAndTransactionTags(transaction: TransactionEntity, tagIds: List<Int>) {
        updateTransaction(transaction)
        val transactionId = transaction.id
        deleteTransactionTagByTransactionId(transactionId)
        val transactionTagEntities = tagIds.map {
            TransactionTagEntity(
                transactionId = transactionId.toInt(),
                tagId = it
            )
        }
        if (transactionTagEntities.isNotEmpty()) {
            insertTransactionTag(transactionTagEntities)
        }
    }
}