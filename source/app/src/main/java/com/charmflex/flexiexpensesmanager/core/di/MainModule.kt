package com.charmflex.flexiexpensesmanager.core.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefsImpl
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
internal interface MainModule {

    companion object {
        @Provides
        @Dispatcher(dispatcherType = Dispatcher.Type.IO)
        fun providesDispatcherIO(): CoroutineDispatcher {
            return Dispatchers.IO
        }

        @Provides
        @Singleton
        fun providesSharedPrefs(appContext: Context): SharedPrefs {
            return SharedPrefsImpl(appContext = appContext)
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