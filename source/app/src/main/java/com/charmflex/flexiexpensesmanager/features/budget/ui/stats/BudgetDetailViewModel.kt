package com.charmflex.flexiexpensesmanager.features.budget.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.features.budget.domain.usecases.GetAdjustedCategoryBudgetInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

internal class BudgetDetailViewModel @Inject constructor(
    private val getAdjustedCategoryBudgetInfoUseCase: GetAdjustedCategoryBudgetInfoUseCase,
    private val mapper: CategoryBudgetExpandableSectionMapper
) : ViewModel() {
    private val _viewState = MutableStateFlow(BudgetStatViewState())
    val viewState = _viewState.asStateFlow()
    private val dateFilter = DateFilter.Monthly(0)

    init {
        observeBudgetStat()
    }

    private fun observeBudgetStat() {
        viewModelScope.launch {
            getAdjustedCategoryBudgetInfoUseCase(dateFilter).collectLatest { res ->
                _viewState.update {
                    it.copy(
                        budgets = mapper.map(res)
                    )
                }
            }
        }
    }

    fun onToggleExpandable(budgetItem: BudgetStatViewState.CategoryBudgetItem) {
        val expandedCatIds = _viewState.value.expandedCategoryIds

        _viewState.update {
            it.copy(
                expandedCategoryIds = if (expandedCatIds.contains(budgetItem.categoryId)) expandedCatIds - budgetItem.categoryId
                else expandedCatIds + budgetItem.categoryId
            )
        }
    }
}


internal data class BudgetStatViewState(
    val budgets: List<CategoryBudgetExpandableSection> = emptyList(),
    // Show all root items with budget which is of 0 parent id
    val expandedCategoryIds: Set<Int> = setOf(0)
) {

    val onScreenBudgetSections: List<CategoryBudgetExpandableSection>
        get() {
            return budgets.map {
                it.copy(
                    contents = it.contents.filter { expandedCategoryIds.contains(it.parentCategoryId)  }
                )
            }
        }
    fun isItemExpanded(item: CategoryBudgetItem): Boolean {
        return item.expandable && item.categoryId in expandedCategoryIds
    }

    data class CategoryBudgetExpandableSection(
        val contents: List<CategoryBudgetItem>
    )

    data class CategoryBudgetItem(
        val categoryId: Int,
        val categoryName: String,
        val parentCategoryId: Int,
        val budget: String,
        val expensesAmount: String,
        val level: Level,
        val expandable: Boolean
    ) {
        enum class Level {
            FIRST, SECOND, THIRD
        }
    }
}