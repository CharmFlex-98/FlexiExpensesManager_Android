package com.charmflex.flexiexpensesmanager.features.account

import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.features.account.data.storage.AccountStorage
import javax.inject.Inject

internal interface AccountHiderService {
    suspend fun toggleHideAccount(isHidden: Boolean)

    suspend fun isAccountHidden(): Boolean
}

internal class AccountHiderServiceImpl @Inject constructor(
    private val accountStorage: AccountStorage
) : AccountHiderService {
    override suspend fun toggleHideAccount(isHidden: Boolean) {
        if (isHidden) accountStorage.hideAccountInfo()
        else accountStorage.unHideAccountInfo()
    }

    override suspend fun isAccountHidden(): Boolean {
        return accountStorage.getAccountInfoHidden()
    }
}