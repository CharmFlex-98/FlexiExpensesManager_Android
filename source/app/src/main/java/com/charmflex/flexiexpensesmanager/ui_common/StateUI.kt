package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.state.StateDialog
import com.maxkeppeler.sheets.state.models.ProgressIndicator
import com.maxkeppeler.sheets.state.models.State
import com.maxkeppeler.sheets.state.models.StateConfig
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateUI() {
    val progress = remember { mutableStateOf(0f) }
    val progressAnimated = animateFloatAsState(targetValue = progress.value, tween(1000)).value
    LaunchedEffect(Unit) {
        progress.value = 1f
        delay(1000)
    }

    val state = State.Loading(
        "Wait a moment",
        ProgressIndicator.Circular(progressAnimated)
    )
    StateDialog(
        state = rememberUseCaseState(visible = true, onCloseRequest = { }),
        config = StateConfig(state = state)
    )
}