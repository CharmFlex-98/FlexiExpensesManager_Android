package com.charmflex.flexiexpensesmanager.features.tag.domain.repositories

import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import kotlinx.coroutines.flow.Flow

internal interface TagRepository {

    suspend fun addTag(tagName: String)
    fun getAllTags(): Flow<List<Tag>>
    suspend fun deleteTag(tagId: Int)
}