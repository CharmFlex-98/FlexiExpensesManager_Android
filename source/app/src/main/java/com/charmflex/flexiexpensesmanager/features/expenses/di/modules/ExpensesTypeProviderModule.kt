package com.charmflex.flexiexpensesmanager.features.expenses.di.modules

import com.charmflex.flexiexpensesmanager.features.expenses.provider.ExpensesTypeProvider
import com.charmflex.flexiexpensesmanager.features.expenses.provider.MockExpensesTypeProvider
import com.charmflex.flexiexpensesmanager.features.expenses.provider.RemoteExpensesTypeProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Qualifier

@Module
internal interface ExpensesTypeProviderModule {

    @Binds
    @Named("mock")
    fun bindsMockExpensesTypeProvider(mockExpensesTypeProvider: MockExpensesTypeProvider): ExpensesTypeProvider

    @Binds
    @Named("remote")
    fun bindsRemoteExpensesTypeProvider(remoteExpensesTypeProvider: RemoteExpensesTypeProvider): ExpensesTypeProvider
}

