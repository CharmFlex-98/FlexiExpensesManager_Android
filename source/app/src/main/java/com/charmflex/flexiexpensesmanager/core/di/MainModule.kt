package com.charmflex.flexiexpensesmanager.core.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupManager
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupManagerImpl
import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.core.storage.FileStorageImpl
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefsImpl
import com.charmflex.flexiexpensesmanager.core.tracker.EventTracker
import com.charmflex.flexiexpensesmanager.core.tracker.PostHogEventTracker
import com.charmflex.flexiexpensesmanager.core.utils.FEFileProvider
import com.charmflex.flexiexpensesmanager.core.utils.FEFileProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class
    ]
)
internal interface MainModule {

    @Binds
    @Singleton
    fun bindsSharedPrefs(sharedPrefsImpl: SharedPrefsImpl): SharedPrefs

    @Binds
    @Singleton
    fun bindsFileStorage(fileStorageImpl: FileStorageImpl): FileStorage

    @Binds
    @Singleton
    fun bindsBackupManager(transactionBackupManagerImpl: TransactionBackupManagerImpl): TransactionBackupManager

    @Binds
    @Singleton
    fun bindsFEFileProvider(feFileProviderImpl: FEFileProviderImpl): FEFileProvider

    companion object {
        @Provides
        @Dispatcher(dispatcherType = Dispatcher.Type.IO)
        fun providesDispatcherIO(): CoroutineDispatcher {
            return Dispatchers.IO
        }

        @Provides
        @Singleton
        fun providesEventTracker(appContext: Context): EventTracker {
            val res = PostHogEventTracker(appContext)
            res.init()

            return res;
        }
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcherType: Type) {
    enum class Type {
        IO, DEFAULT
    }
}