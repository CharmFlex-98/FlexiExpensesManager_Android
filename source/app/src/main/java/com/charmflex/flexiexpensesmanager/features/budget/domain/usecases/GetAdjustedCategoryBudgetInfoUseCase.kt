package com.charmflex.flexiexpensesmanager.features.budget.domain.usecases

import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_YEAR_DB_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.AdjustedCategoryBudgetNode
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.CategoryBudgetFullInfo
import com.charmflex.flexiexpensesmanager.features.budget.domain.repositories.CategoryBudgetRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.processNextEventInCurrentThread
import java.time.LocalDate
import javax.inject.Inject

internal class GetAdjustedCategoryBudgetInfoUseCase @Inject constructor(
    private val categoryBudgetRepository: CategoryBudgetRepository
) {

    operator fun invoke(
        dateFilter: DateFilter
    ): Flow<List<AdjustedCategoryBudgetNode>> {
        val startDate = dateFilter.getStartDate()
        val endDate = dateFilter.getEndDate()

        // TODO: Better handling here
        return categoryBudgetRepository.getMonthlyCategoryBudgetInfo(startDate!!, endDate!!)
    }
}