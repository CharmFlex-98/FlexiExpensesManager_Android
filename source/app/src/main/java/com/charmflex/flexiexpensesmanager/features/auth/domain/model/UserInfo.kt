package com.charmflex.flexiexpensesmanager.features.auth.domain.model

import java.time.Instant

internal data class UserInfo (
    val uid: String,
    val username: String? = null,
    val email: String? = null,
    val createdAt: Instant? = null,
    val isNewUser: Boolean
)