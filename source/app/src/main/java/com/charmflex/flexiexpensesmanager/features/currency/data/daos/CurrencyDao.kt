package com.charmflex.flexiexpensesmanager.features.currency.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.currency.data.models.UserCurrencyRateEntity
import com.charmflex.flexiexpensesmanager.features.currency.data.models.UserCurrencyRateResponse

@Dao
internal interface CurrencyDao {

    @Query(
        "SELECT rate FROM UserCurrencyRateEntity WHERE name = :currencyCode"
    )
    suspend fun getUserCurrency(currencyCode: String): UserCurrencyRateResponse?

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertUserCurrency(currencyRateEntity: UserCurrencyRateEntity)

    @Query("DELETE FROM UserCurrencyRateEntity WHERE name = :currencyName")
    suspend fun deleteUserCurrency(currencyName: String)
    @Query("DELETE FROM UserCurrencyRateEntity")
    suspend fun removeAllUserCurrency()
}