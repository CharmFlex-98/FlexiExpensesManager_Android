package com.charmflex.flexiexpensesmanager.features.budget.ui.stats

import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.SuspendableMapper
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.AdjustedCategoryBudgetNode
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import javax.inject.Inject

internal class CategoryBudgetExpandableSectionMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val userCurrencyRepository: UserCurrencyRepository
) :
    SuspendableMapper<List<AdjustedCategoryBudgetNode>, List<BudgetStatViewState.CategoryBudgetExpandableSection>> {
    private var currencyCode: String? = null

    override suspend fun map(from: List<AdjustedCategoryBudgetNode>): List<BudgetStatViewState.CategoryBudgetExpandableSection> {
        return from.map {
            buildContent(
                BudgetStatViewState.CategoryBudgetExpandableSection(
                    contents = listOf()
                ),
                1,
                setOf(),
                it
            )
        }.filter { // Filter section with empty content
            it.contents.isNotEmpty()
        }
    }

    private suspend fun buildContent(
        section: BudgetStatViewState.CategoryBudgetExpandableSection,
        level: Int,
        parentIds: Set<Int>,
        node: AdjustedCategoryBudgetNode
    ): BudgetStatViewState.CategoryBudgetExpandableSection {
        // If amount is 0, we don't want to show it and it's children
        if (node.adjustedBudgetInCent == 0L) return section

        val adjustedBudgetInCent = node.adjustedBudgetInCent
        val adjustedExpensesInCent = node.adjustedExpensesInCent

        val item = BudgetStatViewState.CategoryBudgetItem(
            categoryId = node.categoryId,
            categoryName = node.categoryName,
            parentCategoryIds = parentIds + node.parentCategoryId,
            budget = currencyFormatter.format(
                adjustedBudgetInCent,
                currencyCode ?: userCurrencyRepository.getPrimaryCurrency().also { currencyCode = it }
            ),
            level = when (level) {
                1 -> BudgetStatViewState.CategoryBudgetItem.Level.FIRST
                2 -> BudgetStatViewState.CategoryBudgetItem.Level.SECOND
                else -> BudgetStatViewState.CategoryBudgetItem.Level.THIRD
            },
            expensesAmount = currencyFormatter.format(
                adjustedExpensesInCent,
                currencyCode ?: userCurrencyRepository.getPrimaryCurrency().also { currencyCode = it }
            ),
            expensesBudgetRatio = adjustedExpensesInCent/adjustedBudgetInCent.toFloat(),
            expandable = run {
                val childrenTotalBudget = node.children.map { it.adjustedBudgetInCent }.reduceOrNull { acc, l -> acc + l }
                childrenTotalBudget != null && childrenTotalBudget > 0L
            }
        )

        return node.children.fold(section.copy(contents = section.contents + item)) { updatedSection, n ->
            buildContent(
                updatedSection,
                level + 1,
                item.parentCategoryIds,
                n
            )
        }
    }
}