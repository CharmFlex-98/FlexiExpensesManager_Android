package com.charmflex.flexiexpensesmanager.features.home.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.YEAR_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper.TransactionHistoryMapper
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TransactionHistoryViewModel(
    private val mapper: TransactionHistoryMapper,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val routeNavigator: RouteNavigator,
    private val accountIdFilter: Int? = null
) : ViewModel() {

    private val _tabState = MutableStateFlow(TabState())
    val tabState = _tabState.asStateFlow()

    private val _offset = MutableStateFlow(0)
    val offset = _offset.asStateFlow()

    private val _viewState = MutableStateFlow(TransactionHistoryViewState())
    val viewState = _viewState.asStateFlow()

    class Factory @Inject constructor(
        private val mapper: TransactionHistoryMapper,
        private val accountRepository: AccountRepository,
        private val transactionRepository: TransactionRepository,
        private val routeNavigator: RouteNavigator,
    ) {
        fun create(accountIdFilter: Int? = null): TransactionHistoryViewModel {
            return TransactionHistoryViewModel(
                mapper, accountRepository, transactionRepository, routeNavigator, accountIdFilter
            )
        }
    }

    init {
        initTitle()
        observeTransactionList()
    }

    private fun initTitle() {
        accountIdFilter?.let {
            viewModelScope.launch {
                val account = accountRepository.getAccountById(it)
                _viewState.update {
                    it.copy(
                        title = account.accountName
                    )
                }
            }
        }
    }

    private fun observeTransactionList() {
        viewModelScope.launch {
            transactionRepository.getTransactions(limit = 100, accountIdFilter = accountIdFilter)
                .transform {
                    val items = it.filter {
                        it.transactionAccountFrom?.id == accountIdFilter ||
                                it.transactionAccountTo?.id == accountIdFilter
                    }
                    emit(items)
                }
                .collectLatest { list ->
                    _offset.value += list.size
                    updateList(list = list)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            toggleLoader(true)
            transactionRepository.getTransactions(offset = offset.value, limit = 100, accountIdFilter = accountIdFilter)
                .catch {
                    toggleLoader(false)
                }
                .firstOrNull()?.let { list ->
                    updateList(list = list)
                }
        }
    }

    fun isAccountStatusFlow() = accountIdFilter != null

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

    fun getNextTransactions() {
        toggleLoadMoreLoader(true)
        viewModelScope.launch {
            delay(1000) // mimic fetching data :)
            transactionRepository.getTransactions(offset = offset.value, limit = 100, accountIdFilter = accountIdFilter)
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
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
    val title: String = ""
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