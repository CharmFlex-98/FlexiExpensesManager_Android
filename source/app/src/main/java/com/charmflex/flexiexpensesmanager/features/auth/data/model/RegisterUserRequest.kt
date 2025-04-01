package com.charmflex.flexiexpensesmanager.features.auth.data.model

data class RegisterUserRequest(
    val uid: String,
    val lastLoginTime: String
)