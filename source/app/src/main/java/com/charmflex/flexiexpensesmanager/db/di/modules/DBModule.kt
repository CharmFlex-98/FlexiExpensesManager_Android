package com.charmflex.flexiexpensesmanager.db.di.modules

import android.content.Context
import com.charmflex.flexiexpensesmanager.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        DaoModule::class
    ]
)
internal interface DBModule {

    companion object {
        @Singleton
        @Provides
        fun provideDB(appContext: Context): AppDatabase {
            return AppDatabase.Builder(appContext).build()
        }
    }
}

