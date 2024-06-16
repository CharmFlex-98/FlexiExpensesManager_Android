package com.charmflex.flexiexpensesmanager.features.account.di

import com.charmflex.flexiexpensesmanager.features.account.ui.AccountEditorViewModel
import com.charmflex.flexiexpensesmanager.features.account.ui.account_detail.AccountDetailViewModel

internal interface AccountInjector {

    fun accountEditorViewModel(): AccountEditorViewModel

    fun accountDetailViewModelFactory(): AccountDetailViewModel.Factory
}