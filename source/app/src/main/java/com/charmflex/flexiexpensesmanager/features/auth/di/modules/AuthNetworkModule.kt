package com.charmflex.flexiexpensesmanager.features.auth.di.modules

import com.charmflex.flexiexpensesmanager.core.network.NetworkClientBuilder
import com.charmflex.flexiexpensesmanager.features.auth.data.remote.AuthApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AuthNetworkModule {

    @Provides
    @Singleton
    fun providesAuthApi(networkClientBuilder: NetworkClientBuilder): AuthApi {
        return networkClientBuilder.buildApi(AuthApi::class.java)
    }

}