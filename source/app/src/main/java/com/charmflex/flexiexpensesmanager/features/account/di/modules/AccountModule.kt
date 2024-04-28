package com.charmflex.flexiexpensesmanager.features.account.di.modules

import com.charmflex.flexiexpensesmanager.features.transactions.di.modules.TransactionRepositoryModule
import dagger.Module

@Module(
    includes = [
        AccountRepositoryModule::class
    ]
)
internal interface AccountModule {
}