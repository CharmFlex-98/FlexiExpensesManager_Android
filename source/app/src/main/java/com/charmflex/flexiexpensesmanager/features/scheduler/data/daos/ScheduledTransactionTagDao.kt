package com.charmflex.flexiexpensesmanager.features.scheduler.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionTagEntity
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionEntity

@Dao
internal interface ScheduledTransactionTagDao : ScheduledTransactionDao {


    @Insert
    suspend fun insertAllScheduledTransactionTag(scheduledTransactionTagEntity: List<ScheduledTransactionTagEntity>)

    @Query("DELETE FROM ScheduledTransactionTagEntity WHERE scheduled_transaction_id = :scheduledTransactionId")
    fun deleteAllScheduledTransactionTagBySchedulerId(scheduledTransactionId: Long)

    @Transaction
    suspend fun insertScheduledTransactionAndTags(
        scheduledTransactionEntity: ScheduledTransactionEntity,
        tagIds: List<Int>
    ) : Long {
        val id = insertScheduledTransaction(scheduledTransactionEntity)
        val entities = tagIds.map {
            ScheduledTransactionTagEntity(
                scheduledTransactionId = id,
                tagId = it
            )
        }

        if (entities.isNotEmpty()) insertAllScheduledTransactionTag(entities)
        return id
    }

    @Transaction
    suspend fun updateScheduledTransactionAndTags(
        scheduledTransactionEntity: ScheduledTransactionEntity,
        tagIds: List<Int>
    ) {
        val id = scheduledTransactionEntity.id
        updateScheduledTransaction(scheduledTransactionEntity)
        deleteAllScheduledTransactionTagBySchedulerId(id)

        val entities = tagIds.map {
            ScheduledTransactionTagEntity(
                scheduledTransactionId = id,
                tagId = it
            )
        }

        if (entities.isNotEmpty()) insertAllScheduledTransactionTag(entities)
    }
}