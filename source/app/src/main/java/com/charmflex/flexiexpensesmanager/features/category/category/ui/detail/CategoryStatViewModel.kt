package com.charmflex.flexiexpensesmanager.features.category.category.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.features.category.category.usecases.GetEachRootCategoryAmountUseCase
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

internal class CategoryStatViewModel @Inject constructor(
    private val getEachRootCategoryAmountUseCase: GetEachRootCategoryAmountUseCase,
    private val userCurrencyRepository: UserCurrencyRepository,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {
    private var job = SupervisorJob()
        get() {
            if (field.isCancelled) field = SupervisorJob()
            return field
        }
    private val _dateFilter = MutableStateFlow<DateFilter>(DateFilter.Monthly(0))
    val dateFilter = _dateFilter.asStateFlow()


    private val _viewState = MutableStateFlow(CategoryStatViewState())
    val viewState = _viewState.asStateFlow()

    init {
        observeDateFilter()
    }

    private fun observeDateFilter() {
        viewModelScope.launch {
            _dateFilter.collectLatest {
                refresh()
            }
        }
    }

    private fun refresh() {
        job.cancel()
        toggleLoader(true)
        viewModelScope.launch {
            getEachRootCategoryAmountUseCase(dateFilter = _dateFilter.value).collectLatest { res ->
                Log.d("collect", res.toString())
                val totalAmount =
                    res?.values?.reduceOrNull { acc, l -> acc + l } ?: return@collectLatest
                if (totalAmount == 0L) return@collectLatest
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        categoryParentStats = res.entries.map { res ->
                            val isPositive = res.value >= 0
                            val sign = if (isPositive) "" else "-"

                            CategoryStatViewState.CategoryStat(
                                name = res.key,
                                isPositive = isPositive,
                                amount = "${sign}${
                                    currencyFormatter.format(
                                        res.value,
                                        userCurrencyRepository.getPrimaryCurrency()
                                    )
                                }",
                                percentage = "${
                                    round((res.value.toDouble()/totalAmount)*100).toInt()
                                }%",
                                amountInCent = res.value
                            )
                        }.sortedByDescending { stat ->
                            stat.amountInCent
                        }
                    )
                }
            }
        }
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onDateFilterChanged(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }
}

internal data class CategoryStatViewState(
    val categoryParentStats: List<CategoryStat> = listOf(),
    val isLoading: Boolean = false,
    val currencyCode: String = ""
) {
    data class CategoryStat(
        val name: String,
        val isPositive: Boolean,
        val amountInCent: Long,
        val amount: String,
        val percentage: String,
    )
}