package com.charmflex.flexiexpensesmanager.db.di.modules

import android.content.Context
import com.charmflex.flexiexpensesmanager.db.AppDatabase
import com.charmflex.flexiexpensesmanager.features.currency.data.utils.SQLQueryBuilder
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
        fun provideDB(appContext: Context, sqlQueryBuilder: SQLQueryBuilder): AppDatabase {
            return AppDatabase.Builder(appContext, sqlQueryBuilder).build()
        }

        @Singleton
        @Provides
        fun provideSQLBuilder(): SQLQueryBuilder {
            return SQLQueryBuilder()
        }
    }
}

