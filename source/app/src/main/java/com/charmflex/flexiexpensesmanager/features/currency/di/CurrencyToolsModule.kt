package com.charmflex.flexiexpensesmanager.features.currency.di

import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorage
import com.charmflex.flexiexpensesmanager.features.currency.data.local.CurrencyKeyStorageImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface CurrencyToolsModule {

    @Binds
    @Singleton
    fun bindsCurrencyKeyStorage(currencyKeyStorageImpl: CurrencyKeyStorageImpl): CurrencyKeyStorage
}