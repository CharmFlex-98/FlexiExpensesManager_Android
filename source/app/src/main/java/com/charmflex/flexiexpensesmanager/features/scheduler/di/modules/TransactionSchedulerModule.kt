package com.charmflex.flexiexpensesmanager.features.scheduler.di.modules

import com.charmflex.flexiexpensesmanager.features.scheduler.ScheduledTransactionHandler
import com.charmflex.flexiexpensesmanager.features.scheduler.ScheduledTransactionHandlerImpl
import com.charmflex.flexiexpensesmanager.features.scheduler.data.mappers.ScheduledTransactionMapper
import com.charmflex.flexiexpensesmanager.features.scheduler.data.repositories.TransactionSchedulerRepositoryImpl
import com.charmflex.flexiexpensesmanager.features.scheduler.data.storage.TransactionSchedulerStorage
import com.charmflex.flexiexpensesmanager.features.scheduler.data.storage.TransactionSchedulerStorageImpl
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository.TransactionSchedulerRepository
import com.charmflex.flexiexpensesmanager.features.scheduler.ui.scheduler_editor.ScheduledTransactionEditorContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
internal interface TransactionSchedulerModule {

    @Binds
    @Singleton
    fun bindsTransactionSchedulerRepository(transactionSchedulerRepositoryImpl: TransactionSchedulerRepositoryImpl): TransactionSchedulerRepository

//    @Binds
//    @Singleton
//    fun bindsTransactionSchedulerService(transactionSchedulerService: TransactionSchedulerService): FEScheduler<*>

   /* @Binds
    @Singleton
    fun bindsWorkerFactory(workerFactory: TransactionSchedulerWorkerFactory): WorkerFactory*/

    @Binds
    @Singleton
    fun bindsTransactionSchedulerStorage(transactionSchedulerStorageImpl: TransactionSchedulerStorageImpl): TransactionSchedulerStorage

    @Binds
    @Singleton
    fun bindsTransactionSchedulerHandler(scheduledTransactionHandlerImpl: ScheduledTransactionHandlerImpl): ScheduledTransactionHandler

    @Binds
    @TransactionEditorProvider(type = TransactionEditorProvider.Type.SCHEDULER)
    fun bindsSchedulerTransactionContentProvider(schedulerTransactionEditorContentProvider: ScheduledTransactionEditorContentProvider) : TransactionEditorContentProvider

    companion object {
        @Provides
        fun providesTransactionSchedulerMapper(): ScheduledTransactionMapper {
            return ScheduledTransactionMapper()
        }
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TransactionEditorProvider(val type: Type) {
    enum class Type {
        DEFAULT, SCHEDULER
    }
}