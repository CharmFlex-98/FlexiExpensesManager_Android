package com.charmflex.flexiexpensesmanager.features.auth.service.sign_in

import android.content.Context

interface SignInService {
    suspend fun signIn(context: Context): SignInState
    suspend fun trySignIn(context: Context): SignInState
}