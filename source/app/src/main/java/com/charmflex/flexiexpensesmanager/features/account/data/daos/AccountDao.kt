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
    @Query(
        "SELECT ag.id as account_group_id," +
                "ag.name as account_group_name," +
                "a.id as account_id, a.name as account_name, " +
                "a.is_deleted as is_account_deleted, " +
                "a.remarks as remarks FROM AccountGroupEntity ag" +
                " LEFT OUTER JOIN AccountEntity a" +
                " ON ag.id = a.account_group_id" +
                " WHERE a.is_deleted = 0 OR a.is_deleted IS NULL"
    )
    fun getAllAccounts(): Flow<List<AccountResponse>>

    @Query(
        "SELECT " +
                "account_group_id, " +
                "account_id, " +
                "account_group_name, " +
                "account_name, " +
                "out_amount, " +
                "COALESCE(SUM(t2.amount_in_cent * t2.rate), 0) AS in_amount, " +
                "account_initial_amount FROM " +
                "(SELECT ag.id AS account_group_id," +
                " a.id AS account_id," +
                " ag.name as account_group_name," +
                " a.name as account_name," +
                " COALESCE(SUM(t.amount_in_cent * t.rate), 0) AS out_amount," +
                " a.initial_amount as account_initial_amount" +
                " FROM AccountGroupEntity ag" +
                " LEFT JOIN AccountEntity a ON ag.id = a.account_group_id" +
                " LEFT JOIN TransactionEntity t ON t.account_from_id = a.id" +
                " GROUP BY a.id) LEFT JOIN TransactionEntity t2 ON t2.account_to_id = account_id " +
                "GROUP BY account_id"
    )
    fun getAccountsSummary(): Flow<List<AccountSummaryResponse>>

    @Insert
    suspend fun insertAccountGroup(accountGroupEntity: AccountGroupEntity)

    @Query("DELETE FROM AccountGroupEntity WHERE id = :accountGroupId")
    suspend fun deleteAccountGroup(accountGroupId: Int)

    @Insert
    suspend fun insertAccount(accountEntity: AccountEntity)

    @Query("DELETE FROM AccountEntity WHERE id = :accountId")
    suspend fun deleteAccount(accountId: Int)
}