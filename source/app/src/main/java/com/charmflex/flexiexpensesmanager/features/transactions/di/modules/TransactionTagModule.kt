package com.charmflex.flexiexpensesmanager.features.transactions.di.modules

import com.charmflex.flexiexpensesmanager.features.tag.data.repositories.TagRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import dagger.Binds
import dagger.Module

@Module
internal interface TransactionTagModule {

    @Binds
    fun bindsTransactionTagRepository(transactionTagRepositoryImpl: TagRepositoryImpl): TagRepository
}