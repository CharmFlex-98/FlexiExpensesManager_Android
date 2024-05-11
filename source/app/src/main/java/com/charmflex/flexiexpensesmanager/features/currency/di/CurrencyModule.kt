package com.charmflex.flexiexpensesmanager.features.currency.di

import dagger.Module

@Module(
    includes = [
        NetworkModule::class,
        CurrencyRepositoryModule::class
    ]
)
internal interface CurrencyModule {

}