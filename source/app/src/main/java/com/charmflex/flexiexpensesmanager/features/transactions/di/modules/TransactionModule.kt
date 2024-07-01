package com.charmflex.flexiexpensesmanager.features.transactions.di.modules

import androidx.core.location.LocationRequestCompat.Quality
import com.charmflex.flexiexpensesmanager.features.scheduler.di.modules.TransactionEditorProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.DefaultTransactionEditorContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import dagger.Binds
import dagger.Module
import javax.inject.Qualifier

@Module(
    includes = [
        TransactionRepositoryModule::class,
        TransactionTagModule::class
    ]
)
internal interface TransactionModule {
    @Binds
    @TransactionEditorProvider(type = TransactionEditorProvider.Type.DEFAULT)
    fun bindsDefaultTransactionEditorContentProvider(defaultTransactionEditorContentProvider: DefaultTransactionEditorContentProvider) : TransactionEditorContentProvider
}