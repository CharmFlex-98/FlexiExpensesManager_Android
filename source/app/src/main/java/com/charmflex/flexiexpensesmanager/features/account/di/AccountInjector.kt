package com.charmflex.flexiexpensesmanager.features.account.di

import com.charmflex.flexiexpensesmanager.features.account.ui.AccountEditorViewModel
import com.charmflex.flexiexpensesmanager.features.account.ui.account_detail.AccountDetailViewModel
import com.charmflex.flexiexpensesmanager.features.account.ui.account_detail.AccountTransactionHistoryViewModel

internal interface AccountInjector {

    fun accountEditorViewModel(): AccountEditorViewModel

    fun accountTransactionHistoryViewModelFactory(): AccountTransactionHistoryViewModel.Factory

    fun accountDetailViewModelFactory(): AccountDetailViewModel.Factory
}