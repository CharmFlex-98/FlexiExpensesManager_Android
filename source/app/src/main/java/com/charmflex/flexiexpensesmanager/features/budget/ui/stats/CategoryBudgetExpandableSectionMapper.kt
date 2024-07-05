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
                it
            )
        }.filter { // Filter section with empty content
            it.contents.isNotEmpty()
        }
    }

    private suspend fun buildContent(
        section: BudgetStatViewState.CategoryBudgetExpandableSection,
        level: Int,
        node: AdjustedCategoryBudgetNode
    ): BudgetStatViewState.CategoryBudgetExpandableSection {
        // If amount is 0, we don't want to show it and it's children
        if (node.adjustedBudgetInCent == 0L) return section

        val item = BudgetStatViewState.CategoryBudgetItem(
            categoryId = node.categoryId,
            categoryName = node.categoryName,
            parentCategoryId = node.parentCategoryId,
            budget = currencyFormatter.format(
                node.adjustedBudgetInCent,
                currencyCode ?: userCurrencyRepository.getPrimaryCurrency().also { currencyCode = it }
            ),
            level = when (level) {
                1 -> BudgetStatViewState.CategoryBudgetItem.Level.FIRST
                2 -> BudgetStatViewState.CategoryBudgetItem.Level.SECOND
                else -> BudgetStatViewState.CategoryBudgetItem.Level.THIRD
            },
            expensesAmount = currencyFormatter.format(
                node.adjustedExpensesInCent,
                currencyCode ?: userCurrencyRepository.getPrimaryCurrency().also { currencyCode = it }
            ),
            expandable = node.children.isNotEmpty()
        )

        return node.children.fold(section.copy(contents = section.contents + item)) { updatedSection, n ->
            buildContent(
                updatedSection,
                level + 1,
                n
            )
        }
    }
}