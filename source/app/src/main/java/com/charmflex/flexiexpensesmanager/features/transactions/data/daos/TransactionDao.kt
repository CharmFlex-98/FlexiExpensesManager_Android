package com.charmflex.flexiexpensesmanager.features.transactions.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.responses.TransactionResponse
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TransactionDao {
    @Query("SELECT * FROM TransactionTypeEntity")
    suspend fun getAllTransactionTypes(): List<TransactionTypeEntity>

    @Insert
    suspend fun insertAllTransactions(transactions: List<TransactionEntity>): List<Long>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query(
        "SELECT t.id as transaction_id," +
                "t.transaction_name," +
                "t.account_from_id as account_from_id," +
                "afrom.name as account_from_name," +
                "t.account_to_id as account_to_id," +
                "ato.name as account_to_name," +
                "t.transaction_type_code," +
                "t.amount_in_cent," +
                "t.transaction_date," +
                "t.category_id," +
                "tc.name as category_name," +
                "t.currency," +
                "t.rate, " +
                "GROUP_CONCAT(tt.tagId, ', ') as tag_ids, " +
                "GROUP_CONCAT(tg.tag_name, ', ') as tag_names " +
                "FROM TransactionEntity t" +
                " LEFT JOIN TransactionCategoryEntity tc ON t.category_id = tc.id" +
                " LEFT JOIN AccountEntity afrom ON t.account_from_id = afrom.id" +
                " LEFT JOIN AccountEntity ato ON t.account_to_id = ato.id" +
                " LEFT JOIN TransactionTagEntity tt ON t.id = tt.transaction_id" +
                " LEFT JOIN TagEntity tg ON tg.id = tt.tagId" +
                " WHERE (:startDate IS NULL OR transaction_date >= :startDate) " +
                "AND (:noTagSelected OR tt.tagId IN (:tagFilter)) " +
                "AND (:endDate IS NULL OR transaction_date <= :endDate)" +
                " GROUP BY t.id" +
                " ORDER BY transaction_date DESC")
    fun getAllTransactions(
        startDate: String?,
        endDate: String?,
        tagFilter: List<Int>,
        noTagSelected: Boolean = tagFilter.isEmpty()
    ) : Flow<List<TransactionResponse>>


    @Query(
        "SELECT t.id as transaction_id," +
            "t.transaction_name," +
            "t.account_from_id as account_from_id," +
            "afrom.name as account_from_name," +
            "t.account_to_id as account_to_id," +
            "ato.name as account_to_name," +
            "t.transaction_type_code," +
            "t.amount_in_cent," +
            "t.transaction_date," +
            "t.category_id," +
            "tc.name as category_name," +
            "t.currency," +
            "t.rate, " +
            "GROUP_CONCAT(tt.tagId, ', ') as tag_ids, " +
            "GROUP_CONCAT(tg.tag_name, ', ') as tag_names " +
            "FROM TransactionEntity t" +
            " LEFT JOIN TransactionCategoryEntity tc ON t.category_id = tc.id" +
            " LEFT JOIN AccountEntity afrom ON t.account_from_id = afrom.id" +
            " LEFT JOIN AccountEntity ato ON t.account_to_id = ato.id" +
            " LEFT JOIN TransactionTagEntity tt ON t.id = tt.transaction_id" +
            " LEFT JOIN TagEntity tg ON tg.id = tt.tagId" +
            " WHERE (:startDate IS NULL OR transaction_date >= :startDate) " +
                "AND (:noTagSelected OR tt.tagId IN (:tagFilter)) " +
                "AND (:noFilterByAccountId OR t.account_from_id = :accountIdFilter OR t.account_to_id =:accountIdFilter) " +
                "AND (:endDate IS NULL OR transaction_date <= :endDate)" +
                " GROUP BY t.id" +
                " ORDER BY transaction_date DESC" +
            " LIMIT :limit OFFSET :offset")
    fun getTransactions(
        startDate: String?,
        endDate: String?,
        offset: Long,
        limit: Int,
        accountIdFilter: Int?,
        noFilterByAccountId: Boolean = accountIdFilter == null,
        tagFilter: List<Int>,
        noTagSelected: Boolean = tagFilter.isEmpty()
    ) : Flow<List<TransactionResponse>>

    @Query(
        "SELECT t.id as transaction_id," +
        "t.transaction_name," +
                "t.account_from_id as account_from_id," +
                "afrom.name as account_from_name," +
                "t.account_to_id as account_to_id," +
                "ato.name as account_to_name," +
                "t.transaction_type_code," +
                "t.amount_in_cent," +
                "t.transaction_date," +
                "t.category_id," +
                "tc.name as category_name, " +
                "t.currency," +
                "t.rate, " +
                "GROUP_CONCAT(tt.tagId, ', ') as tag_ids, " +
                "GROUP_CONCAT(tg.tag_name, ', ') as tag_names " +
                "FROM TransactionEntity t" +
                " LEFT JOIN TransactionCategoryEntity tc ON t.category_id = tc.id" +
                " LEFT JOIN AccountEntity afrom ON t.account_from_id = afrom.id" +
                " LEFT JOIN AccountEntity ato ON t.account_to_id = ato.id" +
                " LEFT JOIN TransactionTagEntity tt ON t.id = tt.transaction_id" +
                " LEFT JOIN TagEntity tg ON tg.id = tt.tagId" +
                " WHERE t.id = :id"
    )
    suspend fun getTransactionById(id: Long): TransactionResponse

    @Query("DELETE FROM TransactionEntity WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)
}