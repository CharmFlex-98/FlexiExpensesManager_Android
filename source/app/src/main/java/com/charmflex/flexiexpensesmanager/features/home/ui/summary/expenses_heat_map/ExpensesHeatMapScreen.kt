package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapWeek
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

@Composable
internal fun ColumnScope.ExpensesHeatMapScreen(
    viewModel: ExpensesHeatMapViewModel
) {
    val data = viewModel.heatMapState

    Box(
        modifier = Modifier.padding(vertical = grid_x2),
        contentAlignment = Alignment.Center
    ) {
        val endDate = remember { LocalDate.now() }
        // GitHub only shows contributions for the past 12 months
        val startDate = remember { endDate.minusMonths(12) }
        var selection by remember { mutableStateOf<Pair<LocalDate, Color>?>(null) }
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            val state = rememberHeatMapCalendarState(
                startMonth = startDate.yearMonth,
                endMonth = endDate.yearMonth,
                firstVisibleMonth = endDate.yearMonth,
                firstDayOfWeek = firstDayOfWeekFromLocale(),
            )
            HeatMapCalendar(
                modifier = Modifier.padding(vertical = 10.dp),
                state = state,
                contentPadding = PaddingValues(end = 6.dp),
                dayContent = { day, week ->
                    Day(
                        day = day,
                        startDate = startDate,
                        endDate = endDate,
                        week = week,
                        color = data.value[day.date] ?: Level.Zero.color,
                    ) { clicked ->
                        selection = Pair(clicked, data.value[clicked] ?: Level.Zero.color)
                    }
                },
                weekHeader = { WeekHeader(it) },
                monthHeader = { MonthHeader(it, endDate, state) },
            )
            CalendarInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 44.dp),
            )
        }
    }
}
 enum class Level(val color: Color) {
    Zero(Color(0xFFEBEDF0)),
    One(Color(0xFF9BE9A8)),
    Two(Color(0xFF40C463)),
    Three(Color(0xFF30A14E)),
    Four(Color(0xFF216E3A)),
}

private val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

@Composable
private fun BottomContent(
    modifier: Modifier = Modifier,
    selection: Pair<LocalDate, Level>? = null,
    refresh: (() -> Unit),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (selection != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = "Clicked: ${formatter.format(selection.first)}")
                LevelBox(color = selection.second.color)
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = refresh,
        ) {
            Text(
                text = "Generate random data",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CalendarInfo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = "Less", fontSize = 10.sp)
        Level.values().forEach { level ->
            LevelBox(level.color)
        }
        Text(text = "More", fontSize = 10.sp)
    }
}

private val daySize = 18.dp

@Composable
private fun Day(
    day: CalendarDay,
    startDate: LocalDate,
    endDate: LocalDate,
    week: HeatMapWeek,
    color: Color,
    onClick: (LocalDate) -> Unit,
) {
    // We only want to draw boxes on the days that are in the
    // past 12 months. Since the calendar is month-based, we ignore
    // the future dates in the current month and those in the start
    // month that are older than 12 months from today.
    // We draw a transparent box on the empty spaces in the first week
    // so the items are laid out properly as the column is top to bottom.
    val weekDates = week.days.map { it.date }
    if (day.date in startDate..endDate) {
        LevelBox(color) { onClick(day.date) }
    } else if (weekDates.contains(startDate)) {
        LevelBox(Color.Transparent)
    }
}

@Composable
private fun LevelBox(color: Color, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(daySize) // Must set a size on the day.
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color = color)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
    )
}

@Composable
private fun WeekHeader(dayOfWeek: DayOfWeek) {
    Box(
        modifier = Modifier
            .height(daySize) // Must set a height on the day of week so it aligns with the day.
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = dayOfWeek.name,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun MonthHeader(
    calendarMonth: CalendarMonth,
    endDate: LocalDate,
    state: HeatMapCalendarState,
) {
    val density = LocalDensity.current
    val firstFullyVisibleMonth by remember {
        // Find the first index with at most one box out of bounds.
        derivedStateOf { getMonthWithYear(state.layoutInfo, daySize, density) }
    }
    if (calendarMonth.weekDays.first().first().date <= endDate) {
        val month = calendarMonth.yearMonth
        val title = if (month == firstFullyVisibleMonth) {
            month.month.name
        } else {
            month.month.name
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp, start = 2.dp),
        ) {
            Text(text = title, fontSize = 10.sp)
        }
    }
}

// Find the first index with at most one box out of bounds.
private fun getMonthWithYear(
    layoutInfo: CalendarLayoutInfo,
    daySize: Dp,
    density: Density,
): YearMonth? {
    val visibleItemsInfo = layoutInfo.visibleMonthsInfo
    return when {
        visibleItemsInfo.isEmpty() -> null
        visibleItemsInfo.count() == 1 -> visibleItemsInfo.first().month.yearMonth
        else -> {
            val firstItem = visibleItemsInfo.first()
            val daySizePx = with(density) { daySize.toPx() }
            if (
                firstItem.size < daySizePx * 3 || // Ensure the Month + Year text can fit.
                firstItem.offset < layoutInfo.viewportStartOffset && // Ensure the week row size - 1 is visible.
                (layoutInfo.viewportStartOffset - firstItem.offset > daySizePx)
            ) {
                visibleItemsInfo[1].month.yearMonth
            } else {
                firstItem.month.yearMonth
            }
        }
    }
}

private fun generateRandomData(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Level> {
    val levels = Level.values()
    return (0..ChronoUnit.DAYS.between(startDate, endDate))
        .associateTo(hashMapOf()) { count ->
            startDate.plusDays(count) to levels.random()
        }
}

