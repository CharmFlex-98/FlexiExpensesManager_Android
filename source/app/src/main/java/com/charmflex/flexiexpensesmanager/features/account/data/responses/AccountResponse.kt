package com.charmflex.flexiexpensesmanager.features.account.data.responses

import androidx.room.ColumnInfo
import androidx.room.Embedded

internal data class AccountResponse(
    @ColumnInfo("account_group_id")
    val accountGroupId: Int,
    @ColumnInfo("account_group_name")
    val accountGroupName: String,
    @Embedded
    val account: Account?

) {
    data class Account(
        @ColumnInfo("account_id")
        val accountId: Int,
        @ColumnInfo("account_name")
        val accountName: String,
        @ColumnInfo("remarks")
        val remarks: String?
    )
}