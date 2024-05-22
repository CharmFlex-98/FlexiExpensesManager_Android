package com.charmflex.flexiexpensesmanager.features.tag.data.repositories

import com.charmflex.flexiexpensesmanager.features.tag.data.daos.TagDao
import com.charmflex.flexiexpensesmanager.features.tag.data.entities.TagEntity
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override suspend fun addTag(tagName: String) {
        val tagEntity = TagEntity(tagName = tagName)
        tagDao.insertTag(tagEntity)
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map {
            it.map {
                Tag(
                    id = it.id,
                    name = it.tagName
                )
            }
        }
    }

    override suspend fun deleteTag(tagId: Int) {
        tagDao.deleteTag(tagId)
    }
}