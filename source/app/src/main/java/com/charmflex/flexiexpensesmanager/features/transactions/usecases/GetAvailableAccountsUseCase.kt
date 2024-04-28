package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.features.account.domain.model.Account
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import javax.inject.Inject

internal class GetAvailableAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    suspend operator fun invoke(): List<Account> {
        return accountRepository.getAllAccounts()
    }
}