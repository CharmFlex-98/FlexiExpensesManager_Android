package com.charmflex.flexiexpensesmanager.features.account.ui.account_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountDetailViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val routeNavigator: RouteNavigator,
    private val accountId: Int
) : ViewModel() {
    class Factory @Inject constructor(
        private val accountRepository: AccountRepository,
        private val routeNavigator: RouteNavigator
    ) {
        fun create(accountId: Int): AccountDetailViewModel {
            return AccountDetailViewModel(accountRepository, routeNavigator, accountId)
        }
    }


    private val _viewState: MutableStateFlow<AccountDetailViewState> = MutableStateFlow(AccountDetailViewState())
    val viewState = _viewState.asStateFlow()


    init {
        viewModelScope.launch {
            val account = accountRepository.getAccountById(accountId)
            _viewState.update {
                it.copy(
                    title = account.accountName
                )
            }
        }
    }
}

internal data class AccountDetailViewState(
    val title: String = ""
)