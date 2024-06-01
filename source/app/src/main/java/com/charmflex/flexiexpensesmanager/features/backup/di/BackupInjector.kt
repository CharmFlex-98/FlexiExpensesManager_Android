package com.charmflex.flexiexpensesmanager.features.backup.di

import com.charmflex.flexiexpensesmanager.features.backup.ui.ImportDataViewModel

internal interface BackupInjector {
    fun importDataViewModel(): ImportDataViewModel
}