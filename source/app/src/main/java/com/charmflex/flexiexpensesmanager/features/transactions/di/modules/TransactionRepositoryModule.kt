package com.charmflex.flexiexpensesmanager.features.transactions.di.modules

import com.charmflex.flexiexpensesmanager.features.category.category.data.repositories.TransactionCategoryRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.transactions.data.repositories.TransactionRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface TransactionRepositoryModule {
    @Binds
    @Singleton
    fun bindsTransactionRepository(transactionRepositoryImpl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    fun bindsTransactionCategoryRepository(transactionCategoryRepositoryImpl: TransactionCategoryRepositoryImpl): TransactionCategoryRepository
}