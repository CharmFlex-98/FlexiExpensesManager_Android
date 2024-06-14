package com.charmflex.flexiexpensesmanager.features.backup.di

import com.charmflex.flexiexpensesmanager.features.backup.AppDataService
import com.charmflex.flexiexpensesmanager.features.backup.AppDataServiceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface BackupModule {
    @Binds
    @Singleton
    fun bindsAppDataService(appDataServiceImpl: AppDataServiceImpl): AppDataService
}