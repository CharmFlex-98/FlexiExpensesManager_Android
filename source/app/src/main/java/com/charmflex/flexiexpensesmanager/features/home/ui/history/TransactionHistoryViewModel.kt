package com.charmflex.flexiexpensesmanager.features.home.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.business_core.transactions.domain.Transaction
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.YEAR_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.home.domain.mapper.TransactionHistoryMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class TransactionHistoryViewModel @Inject constructor(
    private val mapper: TransactionHistoryMapper,
    private val transactionRepository: TransactionRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {

    private val _tabState = MutableStateFlow(TabState())
    val tabState = _tabState.asStateFlow()

    private val _offset = MutableStateFlow(0)
    val offset = _offset.asStateFlow()

    private val _viewState = MutableStateFlow(TransactionHistoryViewState())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            offset.flatMapLatest {
                transactionRepository.getTransactions(offset = it)
            }.collectLatest { list ->
                _viewState.update {
                    it.copy(
                        items = mapper.map(list),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            toggleLoader(true)
            transactionRepository.getTransactions(offset = offset.value)
                .catch {
                    toggleLoader(false)
                }
                .firstOrNull()?.let { list ->
                    _viewState.update {
                        it.copy(
                            items = mapper.map(list),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun updateTabList(items: List<TransactionHistoryItem>) {
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

    fun onNavigateTransactionDetail(transactionId: Long) {
        routeNavigator.navigateTo(TransactionRoute.transactionDetailDestination(transactionId))
    }

    fun findFirstItemIndexByTab(tab: TabState.TabItem): Int {
        return _viewState.value.items.indexOfFirst {
            val localDate = it.dateKey.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
            val year = localDate.toStringWithPattern(YEAR_ONLY_DEFAULT_PATTERN)
            val month = localDate.toStringWithPattern(MONTH_ONLY_DEFAULT_PATTERN)
            tab.month == month && tab.year == year
        }
    }

    fun onReachHistoryItem(item: TransactionHistoryItem?) {
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

internal data class TransactionHistoryViewState(
    val items: List<TransactionHistoryItem> = listOf(),
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

internal sealed interface TransactionHistoryItem {
    val dateKey: String
}

internal data class TransactionHistoryHeader(
    override val dateKey: String,
    val title: String?,
    val subtitle: String
) : TransactionHistoryItem

internal data class TransactionHistorySection(
    override val dateKey: String,
    val items: List<SectionItem>
) : TransactionHistoryItem {
    data class SectionItem(
        val id: Long,
        val name: String,
        val amount: String,
        val category: String,
        val type: String
    )
}