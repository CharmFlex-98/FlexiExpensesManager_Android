package com.charmflex.flexiexpensesmanager.features.account.data.responses

import androidx.room.ColumnInfo

internal data class AccountSummaryResponse(
    @ColumnInfo("account_group_id")
    val accountGroupId: Int,
    @ColumnInfo("account_group_name")
    val accountGroupName: String,
    @ColumnInfo("account_id")
    val accountId: Int?,
    @ColumnInfo("account_name")
    val accountName: String?,
    @ColumnInfo("currency")
    val currency: String?,
    @ColumnInfo("out_amount")
    val outAmount: Long,
    @ColumnInfo("in_amount")
    val inAmount: Long,
)