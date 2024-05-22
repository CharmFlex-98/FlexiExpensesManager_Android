package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aay.compose.donutChart.model.PieChartData
import com.charmflex.flexiexpensesmanager.features.home.usecases.GetCategoryPercentageUseCase
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

internal class ExpensesPieChartViewModel @Inject constructor(
    private val getCategoryPercentageUseCase: GetCategoryPercentageUseCase,
    private val tagRepository: TagRepository
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
                    pieChartData = generatePieChartData(res)
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
    val tagFilter: List<TagFilterItem> = listOf(),
    val showTagFilterDialog: Boolean = false
) {
    data class TagFilterItem(
        val id: Int,
        val name: String,
        val selected: Boolean = false,
    )
}