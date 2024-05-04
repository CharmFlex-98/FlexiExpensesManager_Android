package com.charmflex.flexiexpensesmanager.features.home.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

internal class GetExpensesPercentageUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): Map<LocalDate, Int> {
        val res = mutableMapOf<LocalDate, Int>()
        transactionRepository.getTransactions()
            .firstOrNull()?.let { transactions ->
            transactions
                .groupBy {
                    it.transactionDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
                }
                .filter { it.key != null }
                .entries
                .forEach { (date, transactionsByDate) ->
                    val totalAmountByDate = transactionsByDate
                        .filter {
                            it.transactionTypeCode == TransactionType.EXPENSES.name
                        }
                        .map { t -> t.amountInCent }
                        .reduce { acc, i -> acc + i }
                    res[date!!] = totalAmountByDate
                }
        }

        return res
    }
}