package com.charmflex.flexiexpensesmanager.features.account.data.storage

import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import javax.inject.Inject

private val ACCOUNT_INFO_HIDE_KEY = "ACCOUNT_INFO_HIDE_KEY"

internal interface AccountStorage {
    suspend fun hideAccountInfo()

    suspend fun unHideAccountInfo()

    suspend fun getAccountInfoHidden(): Boolean
}

internal class AccountStorageImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : AccountStorage {
    override suspend fun hideAccountInfo() {
        sharedPrefs.setBoolean(ACCOUNT_INFO_HIDE_KEY, true)
    }

    override suspend fun unHideAccountInfo() {
        sharedPrefs.setBoolean(ACCOUNT_INFO_HIDE_KEY, false)
    }

    override suspend fun getAccountInfoHidden(): Boolean {
        return sharedPrefs.getBoolean(ACCOUNT_INFO_HIDE_KEY, false)
    }
}

