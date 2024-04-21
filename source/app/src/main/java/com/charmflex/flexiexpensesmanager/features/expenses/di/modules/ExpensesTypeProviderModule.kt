package com.charmflex.flexiexpensesmanager.features.expenses.di.modules

import com.charmflex.flexiexpensesmanager.features.expenses.provider.ExpensesTypeProvider
import com.charmflex.flexiexpensesmanager.features.expenses.provider.MockExpensesTypeProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
internal interface ExpensesTypeProviderModule {

    @Binds
    fun bindsMockExpensesTypeProvider(mockExpensesTypeProvider: MockExpensesTypeProvider): ExpensesTypeProvider
}

