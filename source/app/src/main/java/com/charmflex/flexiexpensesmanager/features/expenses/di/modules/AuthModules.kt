package com.charmflex.flexiexpensesmanager.features.expenses.di.modules

import dagger.Module

@Module(
    includes = [
        ExpensesTypeProviderModule::class,
        ExpensesRepositoryModule::class
    ]
)
internal interface AuthModules {
}