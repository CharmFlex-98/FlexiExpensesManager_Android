package com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Tag
import kotlinx.coroutines.flow.Flow

internal interface TransactionTagRepository {

    suspend fun addTag(tagName: String)
    fun getAllTags(): Flow<List<Tag>>
    suspend fun deleteTag(tagId: Int)
}