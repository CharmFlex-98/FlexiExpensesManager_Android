package com.charmflex.flexiexpensesmanager.features.account.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountGroupEntity
import com.charmflex.flexiexpensesmanager.features.account.data.responses.AccountResponse
import com.charmflex.flexiexpensesmanager.features.account.data.responses.AccountSummaryResponse
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AccountDao {

    @Query("SELECT * FROM AccountEntity WHERE id = :id")
    suspend fun getAccountById(id: Int): AccountEntity

    @Query(
        "SELECT ag.id as account_group_id," +
                "ag.name as account_group_name," +
                "a.id as account_id, a.name as account_name, a.currency as currency, " +
                "a.remarks as remarks " +
                "FROM (SELECT * FROM AccountGroupEntity WHERE is_deleted = 0) ag" +
                " LEFT OUTER JOIN (SELECT * FROM AccountEntity WHERE is_deleted = 0) a" +
                " ON ag.id = a.account_group_id"
    )
    fun getAllAccounts(): Flow<List<AccountResponse>>

    @Query(
        "SELECT ag.id as account_group_id," +
                "a.id as account_id," +
                "ag.name as account_group_name," +
                "a.name as account_name," +
                "a.currency as currency," +
                "COALESCE(out_amount, 0) as out_amount," +
                "COALESCE(in_amount, 0) as in_amount FROM (SELECT * FROM AccountGroupEntity WHERE is_deleted = 0) ag" +
                " LEFT JOIN AccountEntity a on a.account_group_id = ag.id" +
                " LEFT JOIN (" +
                " SELECT account_from_id, SUM(t.amount_in_cent) as out_amount FROM TransactionEntity t" +
                " WHERE ((:startDate IS NULL) OR (date(transaction_date) >= date(:startDate))) AND ((:endDate IS NULL) OR date(transaction_date) <= date(:endDate))" +
                " Group by account_from_id) as out_amount_subquery on out_amount_subquery.account_from_id = a.id" +
                " LEFT JOIN (" +
                " SELECT account_to_id, SUM(t.amount_in_cent) as in_amount FROM TransactionEntity t" +
                " WHERE ((:startDate IS NULL) OR (date(transaction_date) >= date(:startDate))) AND ((:endDate IS NULL) OR date(transaction_date) <= date(:endDate))" +
                " Group by account_to_id) as in_amount_subquery on  in_amount_subquery.account_to_id = a.id"
    )
    fun getAccountsSummary(
        startDate: String? = null,
        endDate: String? = null
    ): Flow<List<AccountSummaryResponse>>

    @Insert
    suspend fun insertAccountGroup(accountGroupEntity: AccountGroupEntity)

    @Query("UPDATE AccountGroupEntity SET is_deleted = 1 WHERE id = :accountGroupId")
    suspend fun deleteAccountGroup(accountGroupId: Int)

    @Insert
    suspend fun insertAccount(accountEntity: AccountEntity): Long

    @Query("UPDATE AccountEntity SET is_deleted = 1 WHERE id = :accountId")
    suspend fun deleteAccount(accountId: Int)
}