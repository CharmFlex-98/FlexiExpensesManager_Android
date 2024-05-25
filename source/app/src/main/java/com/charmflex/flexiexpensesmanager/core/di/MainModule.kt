package com.charmflex.flexiexpensesmanager.core.di

import com.charmflex.flexiexpensesmanager.core.excel.TransactionBackupManager
import com.charmflex.flexiexpensesmanager.core.excel.TransactionBackupManagerImpl
import com.charmflex.flexiexpensesmanager.core.storage.FileStorage
import com.charmflex.flexiexpensesmanager.core.storage.FileStorageImpl
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefsImpl
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
    fun providesFileStorage(fileStorageImpl: FileStorageImpl): FileStorage

    @Binds
    @Singleton
    fun providesBackupManager(transactionBackupManagerImpl: TransactionBackupManagerImpl): TransactionBackupManager

    companion object {
        @Provides
        @Dispatcher(dispatcherType = Dispatcher.Type.IO)
        fun providesDispatcherIO(): CoroutineDispatcher {
            return Dispatchers.IO
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