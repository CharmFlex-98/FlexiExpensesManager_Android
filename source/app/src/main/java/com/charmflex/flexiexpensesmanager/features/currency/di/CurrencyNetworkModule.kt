package com.charmflex.flexiexpensesmanager.features.currency.di

import com.charmflex.flexiexpensesmanager.core.network.NetworkClientBuilder
import com.charmflex.flexiexpensesmanager.features.currency.data.remote.CurrencyApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal interface CurrencyNetworkModule {
    companion object {

        @Provides
        @Singleton
        fun providesCurrencyApi(networkClientBuilder: NetworkClientBuilder): CurrencyApi {
            return networkClientBuilder.buildApi(CurrencyApi::class.java)
        }
    }
}