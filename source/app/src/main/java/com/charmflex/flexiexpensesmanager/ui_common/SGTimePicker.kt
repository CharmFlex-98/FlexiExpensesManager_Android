package com.charmflex.flexiexpensesmanager.ui_common

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGTimePicker(
    timePickerState: UseCaseState,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, min: Int) -> Unit,
    isVisible: Boolean
) {

    ClockDialog(
        state = timePickerState,
        selection = ClockSelection.HoursMinutes(
            onNegativeClick = onDismiss,
            onPositiveClick = { h, m ->
                onConfirm(h, m)
            }
        ),
        config = ClockConfig(is24HourFormat = true)
    )

    if (isVisible) timePickerState.show()
    else timePickerState.hide()
}