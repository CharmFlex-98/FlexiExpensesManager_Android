package com.charmflex.flexiexpensesmanager.features.currency.di

import com.charmflex.flexiexpensesmanager.features.currency.data.repositories.CurrencyRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface CurrencyRepositoryModule {

    @Singleton
    @Binds
    fun bindsCurrencyRepository(currencyRepositoryImpl: CurrencyRepositoryImpl): CurrencyRepository
}