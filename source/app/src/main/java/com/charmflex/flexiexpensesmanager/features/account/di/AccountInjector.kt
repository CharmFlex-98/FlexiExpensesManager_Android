package com.charmflex.flexiexpensesmanager.features.account.di

import com.charmflex.flexiexpensesmanager.features.account.ui.AccountEditorViewModel

internal interface AccountInjector {

    fun accountEditorViewModel(): AccountEditorViewModel
}