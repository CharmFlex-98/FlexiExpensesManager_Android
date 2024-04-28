package com.charmflex.flexiexpensesmanager.features.transactions.di.modules

import dagger.Module

@Module(
    includes = [
        TransactionRepositoryModule::class,
    ]
)
internal interface TransactionModule {
}