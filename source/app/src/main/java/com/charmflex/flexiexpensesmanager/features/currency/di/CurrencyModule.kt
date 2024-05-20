package com.charmflex.flexiexpensesmanager.features.currency.di

import dagger.Module

@Module(
    includes = [
        CurrencyNetworkModule::class,
        CurrencyRepositoryModule::class,
        CurrencyToolsModule::class
    ]
)
internal interface CurrencyModule {

}