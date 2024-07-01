package com.charmflex.flexiexpensesmanager.features.scheduler.di

import androidx.work.WorkerFactory
import com.charmflex.flexiexpensesmanager.features.scheduler.ui.schedulerList.SchedulerListViewModel
import com.charmflex.flexiexpensesmanager.features.scheduler.ui.scheduler_editor.SchedulerEditorViewModel

internal interface SchedulerInjector {
    fun schedulerListViewModel(): SchedulerListViewModel
    fun schedulerEditorViewModelFactory(): SchedulerEditorViewModel.Factory
//    fun workerFactory(): WorkerFactory
}