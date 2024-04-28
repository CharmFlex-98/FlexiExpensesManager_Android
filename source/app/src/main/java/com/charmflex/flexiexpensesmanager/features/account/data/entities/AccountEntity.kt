package com.charmflex.flexiexpensesmanager.features.account.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Delete account should not real delete it, because transaction need a reference to it
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class AccountEntity(
    @PrimaryKey(true)
    val id: Int = 0,
    @ColumnInfo("account_group_id", index = true)
    val accountGroupId: Int,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("is_deleted", defaultValue = "false")
    val isDeleted: Boolean,
    @Embedded
    val additionalInfo: AdditionalInfo?
) {
    data class AdditionalInfo(
        @ColumnInfo("remarks")
        val remarks: String? = null
    )
}