package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aay.compose.donutChart.model.PieChartData
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.home.usecases.GetCategoryPercentageUseCase
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

internal class ExpensesPieChartViewModel @Inject constructor(
    private val getCategoryPercentageUseCase: GetCategoryPercentageUseCase,
    private val tagRepository: TagRepository,
    private val userCurrencyRepository: UserCurrencyRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(ExpensesPieChartViewState())
    val viewState = _viewState.asStateFlow()

    init {
        load()
        loadTagOptions()
    }

    fun load() {
        viewModelScope.launch {
            val res =
                getCategoryPercentageUseCase(
                    tagFilter = _viewState.value.tagFilter.filter { it.selected }
                        .map { it.id })
            res?.let {
                _viewState.value = _viewState.value.copy(
                    pieChartData = generatePieChartData(res),
                    barChartData = generateBarChartData(res),
                    currency = userCurrencyRepository.getPrimaryCurrency()
                )
            }
        }
    }

    fun onToggleTagDialog(isVisible: Boolean) {
        _viewState.update {
            it.copy(
                showTagFilterDialog = isVisible
            )
        }
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
                _viewState.value = _viewState.value.copy(
                    tagFilter = it.map {
                        ExpensesPieChartViewState.TagFilterItem(
                            id = it.id,
                            name = it.name,
                            selected = false
                        )
                    }
                )
            }
        }
    }

    fun onSetTagFilter(tagFilter: List<ExpensesPieChartViewState.TagFilterItem>) {
        _viewState.update {
            it.copy(
                tagFilter = tagFilter,
                showTagFilterDialog = false
            )
        }
        load()
    }

    private fun generatePieChartData(data: Map<String, Long>): List<PieChartData> {
        val res = mutableListOf<PieChartData>()
        for ((rootCategory, amount) in data.entries) {
            res.add(
                PieChartData(
                    data = amount.toDouble(),
                    color = generateRandomColor(),
                    partName = rootCategory
                )
            )
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

internal data class ExpensesPieChartViewState(
    val pieChartData: List<PieChartData> = listOf(),
    val barChartData: BarChartData = BarChartData(),
    val tagFilter: List<TagFilterItem> = listOf(),
    val showTagFilterDialog: Boolean = false,
    val chartType: ChartType = ChartType.Pie(),
    val currency: String = ""
) {
    data class TagFilterItem(
        val id: Int,
        val name: String,
        val selected: Boolean = false,
    )

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
        val producer: ChartEntryModelProducer = ChartEntryModelProducer(),
        val currencyCode: String = "",
        val categoryExpensesAmount: List<Pair<String, Long>> = listOf()
    )
}