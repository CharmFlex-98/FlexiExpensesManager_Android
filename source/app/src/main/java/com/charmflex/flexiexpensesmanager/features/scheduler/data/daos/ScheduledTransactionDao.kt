package com.charmflex.flexiexpensesmanager.features.scheduler.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionEntity
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionResponse
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ScheduledTransactionDao {

    @Insert
    suspend fun insertScheduledTransaction(scheduledTransaction: ScheduledTransactionEntity): Long

    @Query(
        "SELECT st.id as scheduler_id," +
                "st.scheduled_transaction_name," +
                "st.scheduled_account_from_id," +
                "afrom.name as account_from_name," +
                "st.scheduled_account_to_id," +
                "ato.name as account_to_name," +
                "st.transaction_type_code," +
                "st.minor_unit_amount," +
                "st.start_update_date," +
                "st.next_update_date," +
                "st.category_id," +
                "tc.name as category_name," +
                "st.currency," +
                "st.account_currency_rate, " +
                "st.primary_currency_rate, " +
                "st.account_minor_unit_amount, " +
                "st.primary_minor_unit_amount, " +
                "GROUP_CONCAT(stt.tagId, ', ') as tag_ids, " +
                "GROUP_CONCAT(tg.tag_name, ', ') as tag_names, " +
                "st.scheduler_period " +
                "FROM ScheduledTransactionEntity st" +
                " LEFT JOIN TransactionCategoryEntity tc ON st.category_id = tc.id" +
                " LEFT JOIN AccountEntity afrom ON st.scheduled_account_from_id = afrom.id" +
                " LEFT JOIN AccountEntity ato ON st.scheduled_account_to_id = ato.id" +
                " LEFT JOIN ScheduledTransactionTagEntity stt ON st.id = stt.scheduled_transaction_id" +
                " LEFT JOIN TagEntity tg ON tg.id = stt.tagId" +
                " WHERE st.id = :id AND st.is_deleted = 0"
    )
    suspend fun getScheduledTransactionById(id: Long): ScheduledTransactionResponse?

    @Update
    suspend fun updateScheduledTransaction(scheduledTransaction: ScheduledTransactionEntity)

    @Query(
        "SELECT st.id as scheduler_id," +
                "st.scheduled_transaction_name," +
                "st.scheduled_account_from_id," +
                "afrom.name as account_from_name," +
                "st.scheduled_account_to_id," +
                "ato.name as account_to_name," +
                "st.transaction_type_code," +
                "st.minor_unit_amount," +
                "st.start_update_date," +
                "st.next_update_date," +
                "st.category_id," +
                "tc.name as category_name," +
                "st.currency," +
                "st.account_currency_rate, " +
                "st.account_minor_unit_amount, " +
                "st.primary_minor_unit_amount, " +
                "GROUP_CONCAT(stt.tagId, ', ') as tag_ids, " +
                "GROUP_CONCAT(tg.tag_name, ', ') as tag_names, " +
                "st.scheduler_period " +
                "FROM ScheduledTransactionEntity st" +
                " LEFT JOIN TransactionCategoryEntity tc ON st.category_id = tc.id" +
                " LEFT JOIN AccountEntity afrom ON st.scheduled_account_from_id = afrom.id" +
                " LEFT JOIN AccountEntity ato ON st.scheduled_account_to_id = ato.id" +
                " LEFT JOIN ScheduledTransactionTagEntity stt ON st.id = stt.scheduled_transaction_id" +
                " LEFT JOIN TagEntity tg ON tg.id = stt.tagId" +
                " WHERE st.is_deleted = 0" +
                " GROUP BY st.id" +
                " ORDER BY st.id DESC"
    )
    fun getAllTransactionScheduler(): Flow<List<ScheduledTransactionResponse>>

    @Query(
        "UPDATE ScheduledTransactionEntity SET is_deleted = 1 WHERE id = :id"
    )
    suspend fun deleteSchedulerById(id: Long)
}