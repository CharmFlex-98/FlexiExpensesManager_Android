package com.charmflex.flexiexpensesmanager.features.scheduler.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.charmflex.flexiexpensesmanager.features.tag.data.entities.TagEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScheduledTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduled_transaction_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class ScheduledTransactionTagEntity(
    @PrimaryKey(true)
    val id: Long = 0L,
    @ColumnInfo("tagId", index = true)
    val tagId: Int,
    @ColumnInfo("scheduled_transaction_id", index = true)
    val scheduledTransactionId: Long
)