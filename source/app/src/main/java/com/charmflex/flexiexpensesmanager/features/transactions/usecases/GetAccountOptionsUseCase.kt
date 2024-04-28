package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.AccountSelectionItem
import javax.inject.Inject

internal class GetAccountOptionsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    suspend operator fun invoke(): List<AccountSelectionItem> {
        return accountRepository.getAllAccounts().map {
            AccountSelectionItem(
                id = it.accountId.toString(),
                title = it.accountName
            )
        }
    }
}