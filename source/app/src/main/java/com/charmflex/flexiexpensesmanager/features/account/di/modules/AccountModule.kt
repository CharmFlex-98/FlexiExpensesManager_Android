package com.charmflex.flexiexpensesmanager.features.account.di.modules

import com.charmflex.flexiexpensesmanager.features.account.AccountHiderService
import com.charmflex.flexiexpensesmanager.features.account.AccountHiderServiceImpl
import com.charmflex.flexiexpensesmanager.features.account.data.storage.AccountStorage
import com.charmflex.flexiexpensesmanager.features.account.data.storage.AccountStorageImpl
import com.charmflex.flexiexpensesmanager.features.transactions.di.modules.TransactionRepositoryModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(
    includes = [
        AccountRepositoryModule::class
    ]
)
internal interface AccountModule {

    @Binds
    @Singleton
    fun bindsAccountHiderService(accountHiderServiceImpl: AccountHiderServiceImpl): AccountHiderService

    @Binds
    @Singleton
    fun bindsAccountStorage(accountStorageImpl: AccountStorageImpl): AccountStorage
}