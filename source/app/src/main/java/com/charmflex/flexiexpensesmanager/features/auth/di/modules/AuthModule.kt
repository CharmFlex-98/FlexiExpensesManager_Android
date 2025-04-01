package com.charmflex.flexiexpensesmanager.features.auth.di.modules

import com.charmflex.flexiexpensesmanager.features.auth.data.AuthRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.auth.domain.repository.AuthRepository
import com.charmflex.flexiexpensesmanager.features.auth.service.sign_in.GoogleSignInService
import com.charmflex.flexiexpensesmanager.features.auth.service.sign_in.SignInService
import com.charmflex.flexiexpensesmanager.features.auth.storage.AuthStorage
import com.charmflex.flexiexpensesmanager.features.auth.storage.AuthStorageImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(
    includes = [
        AuthNetworkModule::class
    ]
)
internal interface AuthModule {

    @Binds
    @Singleton
    fun bindsSignInService(googleSignInService: GoogleSignInService): SignInService

    @Binds
    @Singleton
    fun bindsAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindsAuthStorage(authStorageImpl: AuthStorageImpl): AuthStorage

    companion object {
        @Provides
        @Singleton
        fun providesFirebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }
    }
}