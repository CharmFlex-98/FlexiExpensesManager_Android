package com.charmflex.flexiexpensesmanager.features.auth.service.sign_in

sealed class SignInState {
    data class Success(
        val uid: String,
        val username: String?,
        val email: String?
    ) : SignInState()
    data class Failure(
        val message: String?
    ) : SignInState()
}
