package com.charmflex.flexiexpensesmanager.features.currency.di

import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatterImpl
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

    @Binds
    fun bindsCurrencyFormatter(currencyFormatterImpl: CurrencyFormatterImpl): CurrencyFormatter
}