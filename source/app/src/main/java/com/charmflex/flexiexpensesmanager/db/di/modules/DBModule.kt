package com.charmflex.flexiexpensesmanager.db.di.modules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.charmflex.flexiexpensesmanager.db.AppDatabase
import com.charmflex.flexiexpensesmanager.db.migration.MIGRATION_1_2
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

