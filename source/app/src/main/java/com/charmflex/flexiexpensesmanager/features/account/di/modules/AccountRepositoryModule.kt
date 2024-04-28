package com.charmflex.flexiexpensesmanager.features.account.di.modules

import com.charmflex.flexiexpensesmanager.features.account.data.repositories.AccountRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
internal interface AccountRepositoryModule {

    @Binds
    fun bindsAccountRepositoryModule(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository
}