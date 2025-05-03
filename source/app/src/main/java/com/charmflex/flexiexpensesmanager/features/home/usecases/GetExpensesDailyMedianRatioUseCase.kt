package com.charmflex.flexiexpensesmanager.features.home.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class GetExpensesDailyMedianRatioUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<DailyTransaction>> {
        return transactionRepository.getTransactions().map { transactions ->
            transactions
                .filter { it.transactionTypeCode == TransactionType.EXPENSES.name }
                .groupBy {
                    it.transactionDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)!!
                }
                .map {
                    DailyTransaction(
                        it.key,
                        it.value.map { transaction ->
                            transaction.primaryMinorUnitAmount
                        }.reduce { acc, i ->
                            acc + i
                        }
                    )
                }
        }
    }
}

internal data class DailyTransaction(
    val date: LocalDate,
    val primaryMinorUnitAmount: Long
)