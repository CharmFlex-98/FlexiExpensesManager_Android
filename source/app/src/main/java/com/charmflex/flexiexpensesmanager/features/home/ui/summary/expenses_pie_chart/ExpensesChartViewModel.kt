package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aay.compose.donutChart.model.PieChartData
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BudgetRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TransactionRoute
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.features.category.category.domain.usecases.GetEachRootCategoryAmountUseCase
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.random.Random

internal class ExpensesChartViewModel @Inject constructor(
    private val getEachRootCategoryAmountUseCase: GetEachRootCategoryAmountUseCase,
    private val tagRepository: TagRepository,
    private val userCurrencyRepository: UserCurrencyRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {

    private var job = SupervisorJob()
        get() {
            if (field.isCancelled) field = SupervisorJob()
            return field
        }

    private val _viewState = MutableStateFlow(ExpensesPieChartViewState())
    val viewState = _viewState.asStateFlow()

    private val _tagFilter = MutableStateFlow<List<TagFilterItem>>(emptyList())
    val tagFilter = _tagFilter.asStateFlow()

    private val _dateFilter: MutableStateFlow<DateFilter> = MutableStateFlow(DateFilter.Monthly(0))
    val dateFilter = _dateFilter.asStateFlow()

    val producer: ChartEntryModelProducer = ChartEntryModelProducer()

    init {
        loadTagOptions()
        observeTagFilterChanged()
        observeDateFilterChanged()
        refresh()
    }

    fun refresh() {
        job.cancel()
        viewModelScope.launch(job) {
            getEachRootCategoryAmountUseCase(
                dateFilter = _dateFilter.value,
                tagFilter = _tagFilter.value.filter { it.selected }.map { it.id },
                transactionType = TransactionType.EXPENSES
            ).collectLatest {
                it?.let { res ->
                    _viewState.value = _viewState.value.copy(
                        pieChartData = generatePieChartData(res.mapKeys { it.key.name }),
                        barChartData = generateBarChartData(res.mapKeys { it.key.name }),
                        currency = userCurrencyRepository.getPrimaryCurrency()
                    )
                }
            }
        }
    }

    fun onNavigateExpensesDetailPage() {
        val args = mapOf(CategoryRoutes.Args.CATEGORY_DATE_FILTER to _dateFilter.value)
        routeNavigator.navigateTo(CategoryRoutes.STAT, args)
    }

    private fun observeTagFilterChanged() {
        viewModelScope.launch {
            tagFilter.drop(1).collectLatest {
                refresh()
            }
        }
    }

    private fun observeDateFilterChanged() {
        viewModelScope.launch {
            dateFilter.drop(1).collectLatest {
                refresh()
            }
        }
    }

    fun onDateFilterChanged(dateFilter: DateFilter) {
        _dateFilter.value = dateFilter
    }

    fun onToggleTagDialog(isVisible: Boolean) {
        _viewState.update {
            it.copy(
                showTagFilterDialog = isVisible
            )
        }
    }

    fun onNavigateBudgetDetail() {
        routeNavigator.navigateTo(BudgetRoutes.budgetDetailRoute)
    }

    fun toggleChartType(chartType: ExpensesPieChartViewState.ChartType) {
        _viewState.update {
            it.copy(
                chartType = chartType
            )
        }
    }

    private fun loadTagOptions() {
        viewModelScope.launch {
            tagRepository.getAllTags().firstOrNull()?.let {
                _tagFilter.value = it.map {
                    TagFilterItem(
                        id = it.id,
                        name = it.name,
                        selected = false
                    )
                }
            }
        }
    }

    fun onSetTagFilter(tagFilter: List<TagFilterItem>) {
        _tagFilter.value = tagFilter
    }

    private fun generatePieChartData(data: Map<String, Long>): List<PieChartData> {
        val res = mutableListOf<PieChartData>()
        for ((rootCategory, amount) in data.entries) {
            if (amount > 0) {
                res.add(
                    PieChartData(
                        data = amount.toDouble(), // Use max to eliminate negative value.
                        color = generateRandomColor(),
                        partName = rootCategory
                    )
                )
            }
        }
        return res
    }

    private suspend fun generateBarChartData(data: Map<String, Long>): ExpensesPieChartViewState.BarChartData {
        val sorted = data.toList().sortedByDescending { it.second }
        val res = mutableListOf<LineComponent>()
        res.add(
            LineComponent(
                color = generateRandomColor().toArgb(),
                shape = Shapes.roundedCornerShape()
            )
        )

        return ExpensesPieChartViewState.BarChartData(
            currencyCode = userCurrencyRepository.getPrimaryCurrency(),
            categoryExpensesAmount = sorted,
        )
    }

    private fun generateRandomColor(): Color {
        val random = Random.Default
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color(red, green, blue)
    }
}

internal data class TagFilterItem(
    val id: Int,
    val name: String,
    val selected: Boolean = false,
)

internal data class ExpensesPieChartViewState(
    val pieChartData: List<PieChartData> = listOf(),
    val barChartData: BarChartData = BarChartData(),
    val showTagFilterDialog: Boolean = false,
    val chartType: ChartType = ChartType.Pie(),
    val currency: String = ""
) {

    sealed interface ChartType {
        val index: Int
        val name: String

        data class Pie(
            override val index: Int = 0,
            override val name: String = "PIE"
        ) : ChartType

        data class Bar(
            override val index: Int = 1,
            override val name: String = "BAR"
        ) : ChartType
    }

    data class BarChartData(
        val currencyCode: String = "",
        val categoryExpensesAmount: List<Pair<String, Long>> = listOf()
    )
}