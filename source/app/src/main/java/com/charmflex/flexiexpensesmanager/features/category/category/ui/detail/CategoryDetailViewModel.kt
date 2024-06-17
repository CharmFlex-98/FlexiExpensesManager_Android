package com.charmflex.flexiexpensesmanager.features.category.category.ui.detail

import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getEndDate
import com.charmflex.flexiexpensesmanager.core.utils.getStartDate
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.CategoryHolder
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.GetTransactionListByCategoryUseCase
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.buildCategoryToRootMapping
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.TransactionHistoryViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_history.mapper.TransactionHistoryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CategoryDetailViewModel(
    private val transactionRepository: TransactionRepository,
    mapper: TransactionHistoryMapper,
    private val routeNavigator: RouteNavigator,
    private val getTransactionListByCategoryUseCase: GetTransactionListByCategoryUseCase,
    private val categoryId: Int,
    val categoryName: String,
    private val transactionType: TransactionType
) : TransactionHistoryViewModel(mapper, routeNavigator) {
    private val _dateFilter: MutableStateFlow<DateFilter> = MutableStateFlow(DateFilter.Monthly(0))
    val dateFilter = _dateFilter.asStateFlow()

    private val _categoryDetailViewState = MutableStateFlow(CategoryDetailViewState(categoryName = categoryName))
    val categoryDetailViewState = _categoryDetailViewState.asStateFlow()

    class Factory @Inject constructor(
        private val transactionRepository: TransactionRepository,
        private val mapper: TransactionHistoryMapper,
        private val routeNavigator: RouteNavigator,
        private val getTransactionListByCategoryUseCase: GetTransactionListByCategoryUseCase
    ) {
        fun create(categoryId: Int, categoryName: String, transactionType: String): CategoryDetailViewModel {
            return CategoryDetailViewModel(
                transactionRepository,
                mapper,
                routeNavigator,
                getTransactionListByCategoryUseCase,
                categoryId,
                categoryName,
                TransactionType.fromString(transactionType)
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
        return getTransactionListByCategoryUseCase(dbData, categoryId, _categoryDetailViewState.value.categoryName, transactionType)
    }

    fun onDateChanged(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }
}

internal data class CategoryDetailViewState(
    val categoryName: String,
) {
}