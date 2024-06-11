package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGDatePicker(
    useCaseState: UseCaseState,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime) -> Unit,
    date: LocalDate?,
    isVisible: Boolean,
    boundary: ClosedRange<LocalDate>
) {
    CalendarDialog(
        state = useCaseState,
        selection = CalendarSelection.Date(
            withButtonView = true,
            selectedDate = date,
            onNegativeClick = onDismiss,
            onSelectDate = {
                onConfirm(it.atStartOfDay())
            }
        ),
        config = CalendarConfig(monthSelection = true, yearSelection = true, boundary = boundary)
    )

    if (isVisible) useCaseState.show()
    else useCaseState.hide()
}