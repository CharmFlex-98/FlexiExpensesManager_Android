package com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.YEAR_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal abstract class TransactionHistoryViewModel(
    private val mapper: TransactionHistoryMapper,
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private var job: Job = SupervisorJob()
        get() {
            if (field.isCancelled) field = SupervisorJob()
            return field
        }

    private val _tabState = MutableStateFlow(TabState())
    val tabState = _tabState.asStateFlow()

    private val _offset = MutableStateFlow(0L)
    val offset = _offset.asStateFlow()

    private val _viewState = MutableStateFlow(TransactionHistoryViewState())
    val viewState = _viewState.asStateFlow()

    private fun toggleLoadMoreLoader(isLoadMore: Boolean) {
        _viewState.update {
            it.copy(
                isLoadMore = isLoadMore
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

     fun observeTransactionList() {
        job.cancel()
        toggleLoader(true)
        viewModelScope.launch(job) {
            getTransactionListFlow(offset = 0)
                .catch {
                    toggleLoader(false)
                }
                .collectLatest { list ->
                    _offset.value = list.size.toLong()
                    updateList(list = list)
                }
        }
    }

    abstract fun getTransactionListFlow(offset: Long): Flow<List<Transaction>>

    fun getNextTransactions() {
        toggleLoadMoreLoader(true)
        viewModelScope.launch {
            delay(1000) // mimic fetching data :)
            getTransactionListFlow(offset = offset.value)
                .catch {
                    // TODO: Need to do something?
                    toggleLoadMoreLoader(false)
                }
                .firstOrNull()?.let { list ->
                    _offset.value += list.size
                    updateList(list = list, clearOldList = false)
                }
        }
    }

    private fun updateList(list: List<Transaction>, clearOldList: Boolean = true) {
        viewModelScope.launch {
            val updatedList = mapper.map(list)
            _viewState.update { it ->
                it.copy(
                    items = if (clearOldList) updatedList else appendsTransactionHistoryItems(updatedList),
                    isLoading = false,
                    isLoadMore = false
                )
            }
            updateTabList(_viewState.value.items)
        }
    }

    private fun appendsTransactionHistoryItems(appendItems: List<TransactionHistoryItem>): List<TransactionHistoryItem> {
        return _viewState.value.items.toMutableList()
            .apply ori@ {
                val duplicateDateHeader =
                    this.lastOrNull { it is TransactionHistoryHeader }?.dateKey?.let {
                        it == appendItems.firstOrNull { it is TransactionHistoryHeader }?.dateKey
                    } ?: false
                val newUpdatedList = appendItems.toMutableList().apply {
                    if (duplicateDateHeader) {
                        // Remove new list first date header
                        removeAt(0)
                        // Remove the new list first content
                        val toMergeItems = (removeAt(0) as TransactionHistorySection).items

                        // Modifier previous section items
                        val lastSection = this@ori.removeLast() as TransactionHistorySection
                        val lastSectionMergedItems = lastSection.items.toMutableList().apply {
                            if (toMergeItems.isNotEmpty()) {
                                addAll(toMergeItems)
                            }
                        }
                        this@ori.add(lastSection.copy(dateKey = lastSection.dateKey, items = lastSectionMergedItems))
                    }
                }
                addAll(newUpdatedList)
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

    fun onNavigateTransactionDetail(transactionId: Long) {
        routeNavigator.navigateTo(TransactionRoute.transactionDetailDestination(transactionId))
    }
}


internal data class TransactionHistoryViewState(
    val items: List<TransactionHistoryItem> = listOf(),
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
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
        val type: String,
        @DrawableRes
        val iconResId: Int
    )
}