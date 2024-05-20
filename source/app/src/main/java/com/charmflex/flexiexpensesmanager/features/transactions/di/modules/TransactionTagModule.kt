package com.charmflex.flexiexpensesmanager.features.transactions.di.modules

import com.charmflex.flexiexpensesmanager.features.transactions.data.repositories.TransactionTagRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionTagRepository
import dagger.Binds
import dagger.Module

@Module
internal interface TransactionTagModule {

    @Binds
    fun bindsTransactionTagRepository(transactionTagRepositoryImpl: TransactionTagRepositoryImpl): TransactionTagRepository
}