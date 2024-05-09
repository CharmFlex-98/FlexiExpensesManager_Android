package com.charmflex.flexiexpensesmanager.features.home.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

internal class GetExpensesDailyMedianRatioUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): List<DailyTransaction> {
        return transactionRepository.getTransactions()
            .firstOrNull()?.let { transactions ->
                transactions
                    .groupBy {
                        it.transactionDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
                    }
                    .filter { it.key != null }
                    .map {
                        DailyTransaction(
                            it.key!!,
                            it.value.map { transaction ->
                                transaction.amountInCent
                            }.reduce { acc, i ->
                                acc + i
                            }
                        )
                    }
            } ?: return listOf()
    }
}

internal data class DailyTransaction(
    val date: LocalDate,
    val amountInCent: Int
)