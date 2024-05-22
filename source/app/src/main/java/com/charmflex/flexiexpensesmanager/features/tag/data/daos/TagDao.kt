package com.charmflex.flexiexpensesmanager.features.tag.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.charmflex.flexiexpensesmanager.features.tag.data.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TagDao {

    @Insert
    suspend fun insertTag(tagEntity: TagEntity)

    @Query("SELECT * FROM TagEntity")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("DELETE FROM TagEntity WHERE id = :tagId")
    suspend fun deleteTag(tagId: Int)
}