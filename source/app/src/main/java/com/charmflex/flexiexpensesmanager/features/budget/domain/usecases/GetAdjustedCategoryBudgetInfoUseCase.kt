package com.charmflex.flexiexpensesmanager.features.budget.domain.usecases

import com.charmflex.flexiexpensesmanager.core.utils.MONTH_YEAR_DB_PATTERN
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

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(monthDate: LocalDate): Flow<List<AdjustedCategoryBudgetNode>> {
        val monthDateString = monthDate.toStringWithPattern(MONTH_YEAR_DB_PATTERN)
        return categoryBudgetRepository.getMonthlyCategoryBudgetInfo(monthDateString)
            .transformLatest { list ->
                val rootNodes = mutableListOf<AdjustedCategoryBudgetNode>()
                val nodeMap = mutableMapOf<Int, AdjustedCategoryBudgetNode>()
                // Append all category budgets into nodeMap
                list.forEach {
                    val initialNode = it.toInitialNode(monthDate)
                    if (it.categoryParentId == 0) rootNodes.add(initialNode)
                    nodeMap[it.categoryId] = initialNode
                }

                // Build tree
                list.forEach { item ->
                    if (item.categoryParentId != 0) {
                        val parentNode = nodeMap[item.categoryParentId]
                        parentNode?.let { pn ->
                            nodeMap[item.categoryId]?.let { pn.appendChildren(it) }
                        }
                    }
                }

                emit(rootNodes)
            }
    }
}

private fun CategoryBudgetFullInfo.toInitialNode(localDate: LocalDate): AdjustedCategoryBudgetNode {
    val customMonth = this.budget?.customMonthlyBudgets?.firstOrNull {
        it.budgetMonthYear.toLocalDate(MONTH_YEAR_DB_PATTERN)?.equals(localDate) ?: false
    }

    return AdjustedCategoryBudgetNode(
        category = Transaction.TransactionCategory(
            this.categoryId,
            this.categoryName
        ),
        parentCategoryId = this.categoryParentId,
        defaultBudgetInCent = customMonth?.customBudgetInCent ?: (this.budget?.defaultBudgetInCent ?: 0)
    )
}