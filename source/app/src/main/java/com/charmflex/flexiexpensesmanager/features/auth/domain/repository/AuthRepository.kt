package com.charmflex.flexiexpensesmanager.features.auth.domain.repository

import com.charmflex.flexiexpensesmanager.features.auth.domain.model.UserInfo
import java.time.OffsetDateTime

internal interface AuthRepository {
    suspend fun upsertUser(
        uid: String,
        username: String? = null,
        email: String? = null
    ): UserInfo
}