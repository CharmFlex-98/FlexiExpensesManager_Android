package com.charmflex.flexiexpensesmanager.core.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
internal interface MainModule {

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