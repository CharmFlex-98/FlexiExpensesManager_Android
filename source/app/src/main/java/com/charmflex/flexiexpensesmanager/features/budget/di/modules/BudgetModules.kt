package com.charmflex.flexiexpensesmanager.features.budget.di.modules

import com.charmflex.flexiexpensesmanager.features.budget.data.repositories.CategoryBudgetRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.budget.domain.repositories.CategoryBudgetRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface BudgetModules {
    @Binds
    @Singleton
    fun bindsBudgetRepository(budgetRepositoryImpl: CategoryBudgetRepositoryImpl): CategoryBudgetRepository
}