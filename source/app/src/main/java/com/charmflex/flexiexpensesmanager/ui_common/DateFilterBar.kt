package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_YEAR_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.SHORT_MONTH_YEAR_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart.FilterMenuDropDownItem
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import java.nio.file.DirectoryStream.Filter
import java.time.LocalDate
import kotlin.reflect.KClass

@Composable
internal fun DateFilterBar(
    modifier: Modifier = Modifier,
    currentDateFilter: DateFilter,
    onDateFilterChanged: (DateFilter) -> Unit,
    onShowMonthFilter: (LocalDate) -> String = {
        it.toStringWithPattern(SHORT_MONTH_YEAR_PATTERN)
    },
    onShowCustomStartFilter: (LocalDate) -> String = {
        it.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
    },
    onShowCustomEndFilter: (LocalDate) -> String = {
        it.toStringWithPattern(DATE_ONLY_DEFAULT_PATTERN)
    },
    dateFilterConfig: DateFilterConfig = DateFilterConfig()
) {
    var dropDownExpanded by remember { mutableStateOf(false) }
    val datePickerUseCaseState = remember { UseCaseState() }
    var customDateSelection by remember { mutableStateOf<CustomDateSelection?>(null) }
    val selectedMenu = when (currentDateFilter) {
        is DateFilter.Monthly -> stringResource(id = FilterMenuDropDownItem.Monthly.titleResId)
        is DateFilter.Custom -> stringResource(id = FilterMenuDropDownItem.Custom.titleResId)
        is DateFilter.All -> stringResource(id = FilterMenuDropDownItem.All.titleResId)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (currentDateFilter) {
            is DateFilter.Monthly -> MonthlyDateSelection(
                type = currentDateFilter,
                onShowMonthFilter = onShowMonthFilter,
                onDateFilterChanged = onDateFilterChanged
            )

            is DateFilter.Custom -> CustomDateSelection(
                type = currentDateFilter,
                onShowCustomStartFilter = onShowCustomStartFilter,
                onShowCustomEndFilter = onShowCustomEndFilter,
                onStartDateBoxClicked = {
                    customDateSelection =
                        CustomDateSelection(dateFilter = currentDateFilter, isStartSelected = true)
                },
                onEndDateBoxClicked = {
                    customDateSelection =
                        CustomDateSelection(dateFilter = currentDateFilter, isStartSelected = false)
                }
            )

            else -> FECallout3(modifier = Modifier
                .padding(grid_x1), text = stringResource(id = R.string._date_filter_show_all_description))
        }

        Spacer(modifier = Modifier.weight(1f))

        DateFilterMenuSelection(
            dateFilterConfig,
            menuName = selectedMenu,
            onMenuTap = { dropDownExpanded = true },
            onDismiss = { dropDownExpanded = false },
            dropDownExpanded = dropDownExpanded
        ) {
            onDateFilterChanged(it)
        }
    }

    SGDatePicker(
        useCaseState = datePickerUseCaseState,
        onDismiss = { customDateSelection = null },
        onConfirm = { res ->
            val updatedFilter = customDateSelection?.let {
                if (it.isStartSelected) it.dateFilter.copy(from = res.toLocalDate())
                else it.dateFilter.copy(to = res.toLocalDate())
            }
            updatedFilter?.let {
                onDateFilterChanged(it)
            }
            customDateSelection = null
        },
        date = customDateSelection?.let {
            if (it.isStartSelected) it.dateFilter.from
            else it.dateFilter.to
        },
        isVisible = customDateSelection != null,
        boundary = LocalDate.now().minusYears(10)..LocalDate.now()
    )
}

@Composable
private fun DateFilterMenuSelection(
    dateFilterConfig: DateFilterConfig,
    menuName: String,
    onMenuTap: () -> Unit,
    dropDownExpanded: Boolean,
    onDismiss: () -> Unit,
    onDateFilterChanged: (DateFilter) -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onMenuTap()
            }
            .border(BorderStroke(grid_x0_25, color = Color.Black))
            .padding(
                grid_x1
            )
    ) {
        FECallout3(text = menuName)
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = onDismiss
        ) {
            dateFilterConfig.dateFilterOptions.mapNotNull {
                when (it) {
                    DateFilter.All::class -> FilterMenuDropDownItem.All
                    DateFilter.Monthly::class -> FilterMenuDropDownItem.Monthly
                    DateFilter.Custom::class -> FilterMenuDropDownItem.Custom
                    else -> null
                }
            }.forEach { item ->
                DropdownMenuItem(
                    text = {
                        FECallout3(text = stringResource(id = item.titleResId))
                    }, onClick = {
                        when (item.filterMenuItemType) {
                            FilterMenuDropDownItem.FilterMenuItemType.MONTHLY -> {
                                val newDateFilter = DateFilter.Monthly(0)
                                onDateFilterChanged(newDateFilter)
                            }

                            FilterMenuDropDownItem.FilterMenuItemType.CUSTOM -> {
                                val newDateFilter = DateFilter.Custom(
                                    from = LocalDate.now().withDayOfMonth(1),
                                    to = LocalDate.now()
                                )
                                onDateFilterChanged(newDateFilter)
                            }

                            FilterMenuDropDownItem.FilterMenuItemType.ALL -> {
                                onDateFilterChanged(DateFilter.All)
                            }
                        }
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthlyDateSelection(
    type: DateFilter.Monthly,
    onShowMonthFilter: (LocalDate) -> String,
    onDateFilterChanged: (DateFilter) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = onShowMonthFilter(LocalDate.now().minusMonths(type.monthBefore))
        IconButton(
            modifier = Modifier.height(grid_x3),
            onClick = {
                onDateFilterChanged(type.copy(monthBefore = type.monthBefore + 1))
            }
        ) {
            SGIcons.ArrowBack(modifier = Modifier.size(grid_x2))
        }
        FECallout3(
            modifier = Modifier
                .width(grid_x12)
                .padding(horizontal = grid_x1), text = text
        )
        if (type.monthBefore > 0) IconButton(
            modifier = Modifier.height(grid_x3),
            onClick = {
                onDateFilterChanged(type.copy(monthBefore = type.monthBefore - 1))
            }
        ) {
            SGIcons.NextArrow(modifier = Modifier.size(grid_x2))
        }
    }
}

@Composable
private fun CustomDateSelection(
    type: DateFilter.Custom,
    onShowCustomStartFilter: (LocalDate) -> String,
    onShowCustomEndFilter: (LocalDate) -> String,
    onStartDateBoxClicked: () -> Unit,
    onEndDateBoxClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        FECallout3(modifier = Modifier
            .clickable {
                onStartDateBoxClicked()
            }
            .padding(grid_x1), text = onShowCustomStartFilter(type.from))

        FECallout3(text = " ~ ")

        FECallout3(modifier = Modifier
            .clickable {
                onEndDateBoxClicked()
            }
            .padding(grid_x1), text = onShowCustomEndFilter(type.to))
    }
}


internal data class CustomDateSelection(
    val dateFilter: DateFilter.Custom,
    val isStartSelected: Boolean
)

internal data class DateFilterConfig (
    val dateFilterOptions: List<KClass<out DateFilter>> = listOf(DateFilter.All::class, DateFilter.Custom::class, DateFilter.Monthly::class)
)
