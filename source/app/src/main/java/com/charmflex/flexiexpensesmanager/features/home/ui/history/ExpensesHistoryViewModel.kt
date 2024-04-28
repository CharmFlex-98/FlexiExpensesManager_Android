package com.charmflex.flexiexpensesmanager.features.home.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.YEAR_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.home.domain.mapper.ExpensesHistoryMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class ExpensesHistoryViewModel @Inject constructor(
    private val mapper: ExpensesHistoryMapper,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _tabState = MutableStateFlow(TabState())
    val tabState = _tabState.asStateFlow()

    private val _viewState = MutableStateFlow(ExpensesHistoryViewState())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            toggleLoader(true)
            delay(1000)
            resultOf {
                transactionRepository.getHistory()
            }.fold(
                onSuccess = {
                    val res = mapper.map(it)
                    _viewState.update {
                        it.copy(
                            items = res
                        )
                    }
                    updateTabList(res)
                    toggleLoader(false)
                },
                onFailure = {
                    toggleLoader(false)
                }
            )
        }
    }

    private fun updateTabList(items: List<ExpensesHistoryItem>) {
        val tabs = items.map {
            val localDate = it.dateKey.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
            TabState.TabItem(
                year = localDate.toStringWithPattern(YEAR_ONLY_DEFAULT_PATTERN),
                month = localDate.toStringWithPattern(MONTH_ONLY_DEFAULT_PATTERN)
            )
        }
            .distinct()
            .reversed()

        _tabState.update {
            it.copy(
                tabs = tabs
            )
        }
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    fun findFirstItemIndexByTab(tab: TabState.TabItem): Int {
        return _viewState.value.items.indexOfFirst {
            val localDate = it.dateKey.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
            val year = localDate.toStringWithPattern(YEAR_ONLY_DEFAULT_PATTERN)
            val month = localDate.toStringWithPattern(MONTH_ONLY_DEFAULT_PATTERN)
            tab.month == month && tab.year == year
        }
    }

    fun onReachHistoryItem(item: ExpensesHistoryItem?) {
        if (item == null) return

        val localDate = item.dateKey.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
        val year = localDate.toStringWithPattern(YEAR_ONLY_DEFAULT_PATTERN)
        val month = localDate.toStringWithPattern(MONTH_ONLY_DEFAULT_PATTERN)
        _tabState.update {
            it.copy(
                selectedTabIndex = it.tabs.indexOfFirst { tabItem ->
                    tabItem.year == year && tabItem.month == month
                }
            )
        }
    }
}

internal data class ExpensesHistoryViewState(
    val items: List<ExpensesHistoryItem> = listOf(),
    val isLoading: Boolean = false
)

internal data class TabState(
    val selectedTabIndex: Int = 0,
    val tabs: List<TabItem> = listOf(),
) {
    data class TabItem(
        val year: String,
        val month: String
    )
}

internal sealed interface ExpensesHistoryItem {
    val dateKey: String
}

internal data class ExpensesHistoryHeader(
    override val dateKey: String,
    val title: String?,
    val subtitle: String
) : ExpensesHistoryItem

internal data class ExpensesHistorySection(
    override val dateKey: String,
    val items: List<SectionItem>
) : ExpensesHistoryItem {
    data class SectionItem(
        val name: String,
        val amount: String,
        val category: String,
        val type: String
    )
}