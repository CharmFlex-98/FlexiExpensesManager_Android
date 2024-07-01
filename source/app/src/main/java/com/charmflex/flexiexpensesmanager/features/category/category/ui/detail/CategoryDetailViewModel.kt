package com.charmflex.flexiexpensesmanager.features.category.category.ui.detail

import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.SHORT_MONTH_YEAR_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.CategoryHolder
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.GetTransactionListByCategoryUseCase
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.buildCategoryToRootMapping
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.marker.Marker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

internal class CategoryDetailViewModel(
    private val transactionRepository: TransactionRepository,
    mapper: TransactionHistoryMapper,
    private val routeNavigator: RouteNavigator,
    private val getTransactionListByCategoryUseCase: GetTransactionListByCategoryUseCase,
    private val categoryId: Int,
    val categoryName: String,
    val transactionType: TransactionType,
    dateFilter: DateFilter?,
    private val currencyFormatter: CurrencyFormatter,
    private val userCurrencyRepository: UserCurrencyRepository
) : TransactionHistoryViewModel(mapper, routeNavigator) {
    private val _dateFilter: MutableStateFlow<DateFilter> =
        MutableStateFlow(dateFilter ?: DateFilter.Monthly(0))
    val dateFilter = _dateFilter.asStateFlow()

    private val _categoryDetailViewState =
        MutableStateFlow(CategoryDetailViewState(categoryName = categoryName, totalAmount = ""))
    val categoryDetailViewState = _categoryDetailViewState.asStateFlow()

    val chartEntryModelProducer = ChartEntryModelProducer()

    class Factory @Inject constructor(
        private val transactionRepository: TransactionRepository,
        private val mapper: TransactionHistoryMapper,
        private val routeNavigator: RouteNavigator,
        private val getTransactionListByCategoryUseCase: GetTransactionListByCategoryUseCase,
        private val currencyFormatter: CurrencyFormatter,
        private val userCurrencyRepository: UserCurrencyRepository
    ) {
        fun create(
            categoryId: Int,
            categoryName: String,
            transactionType: String,
            dateFilter: DateFilter?
        ): CategoryDetailViewModel {
            return CategoryDetailViewModel(
                transactionRepository,
                mapper,
                routeNavigator,
                getTransactionListByCategoryUseCase,
                categoryId,
                categoryName,
                TransactionType.fromString(transactionType),
                dateFilter,
                currencyFormatter,
                userCurrencyRepository
            )
        }
    }


    init {
        observeDateFilter()
    }

    private fun observeDateFilter() {
        viewModelScope.launch {
            dateFilter.collectLatest {
                refresh()
            }
        }
    }

    override fun refresh() {
        super.refresh()
        updateTransactionDetail()
    }

    private fun updateTransactionDetail() {
        viewModelScope.launch(job) {

        }
    }

    fun onTransactionTap(transactionId: Long) {
        routeNavigator.navigateTo(TransactionRoute.transactionDetailDestination(transactionId))
    }

    override fun getDBTransactionListFlow(offset: Long): Flow<List<Transaction>> {
        val startDate = _dateFilter.value.getStartDate()
        val endDate = _dateFilter.value.getEndDate()
        return transactionRepository.getTransactions(
            startDate = startDate,
            endDate = endDate,
            offset = offset
        )
    }

    override suspend fun filter(dbData: List<Transaction>): List<Transaction> {
        return getTransactionListByCategoryUseCase(
            dbData,
            categoryId,
            _categoryDetailViewState.value.categoryName,
            transactionType
        )
    }

    override suspend fun onReceivedFilteredData(data: List<Transaction>, clearOldList: Boolean) {
        // We don't need to take care of getting next transactions.
        if (clearOldList.not()) return

        val totalAmount = data.map { it.amountInCent }.reduceOrNull { acc, l -> acc + l } ?: 0
        val startDate =
            _dateFilter.value.getStartDate()?.toLocalDate(DATE_ONLY_DEFAULT_PATTERN) ?: return
        val endDate =
            _dateFilter.value.getEndDate()?.toLocalDate(DATE_ONLY_DEFAULT_PATTERN) ?: return
        val dates = generateSequence(startDate) { date ->
            date.takeIf { it.isBefore(endDate) }?.plusDays(1)
        }.toList()
        val dayToTotalAmountMap = data.groupBy {
            it.transactionDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)
        }.mapValues {
            it.value.map { it.amountInCent }.reduceOrNull { acc, l -> acc + l } ?: 0
        }
        val newEntries = dates.map { localDate ->
            val dayWithAmount = dayToTotalAmountMap[localDate]
            val epochSecond = localDate.atStartOfDay().atZone(ZoneId.systemDefault())
                .toInstant().epochSecond

            dayWithAmount?.let { amount ->
                epochSecond to amount
            } ?: (epochSecond to 0L)
        }
        _categoryDetailViewState.update { state ->
            state.copy(
                totalAmount = currencyFormatter.format(
                    totalAmount,
                    userCurrencyRepository.getPrimaryCurrency()
                ),
                lineChartData = CategoryDetailViewState.LineChartData(
                    entries = if (clearOldList) newEntries else state.lineChartData.entries.toMutableList()
                        .apply { addAll(newEntries) }
                )
            )
        }
    }

    fun onDateChanged(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }
}

internal data class CategoryDetailViewState(
    val categoryName: String,
    val totalAmount: String,
    val lineChartData: LineChartData = LineChartData()
) {
    data class LineChartData(
        val entries: List<Pair<Long, Long>> = listOf()
    )
}