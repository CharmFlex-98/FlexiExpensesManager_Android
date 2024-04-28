package com.charmflex.flexiexpensesmanager.features.account.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountGroupEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity

@Dao
internal interface AccountDao {
    @Query(
        "SELECT * FROM AccountGroupEntity" +
                " INNER JOIN AccountEntity" +
                " ON AccountGroupEntity.id = AccountEntity.account_group_id" +
                " WHERE AccountEntity.is_deleted = 0"
    )
    suspend fun getAllAccounts(): Map<AccountGroupEntity, List<AccountEntity>>
}