package com.charmflex.flexiexpensesmanager.features.expenses.di.modules

import com.charmflex.flexiexpensesmanager.features.expenses.data.repositories.ExpensesRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.expenses.domain.repositories.ExpensesRepository
import dagger.Binds
import dagger.Module

@Module
internal interface ExpensesRepositoryModule {
    @Binds
    fun bindsExpensesRepository(expensesRepositoryImpl: ExpensesRepositoryImpl): ExpensesRepository
}