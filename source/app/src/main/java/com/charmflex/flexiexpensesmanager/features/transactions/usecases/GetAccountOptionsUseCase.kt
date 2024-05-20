package com.charmflex.flexiexpensesmanager.features.transactions.usecases

import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import javax.inject.Inject

internal class GetAccountOptionsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {

//    suspend operator fun invoke(): List<AccountSelectionItem> {
//        val res = mutableListOf<AccountSelectionItem>()
//        accountRepository.getAllAccounts().forEach {
//            res.addAll(
//                it.accounts.map {
//                    AccountSelectionItem(
//                        id = it.accountId.toString(),
//                        title = it.accountName
//                    )
//                }
//            )
//        }
//
//        return res
//    }
}