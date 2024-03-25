package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.runtime.Composable

sealed interface ContentState {
    object LoadingState : ContentState
    object LoadedState : ContentState
    object EmptyState: ContentState
    object ErrorState: ContentState
}
@Composable
fun WithState(
    contentState: ContentState,
    loadingState: @Composable () -> Unit,
    emptyState: @Composable () -> Unit,
    errorState: @Composable () -> Unit,
    loadedState: @Composable () -> Unit
    ) {
    when (contentState) {
        is ContentState.LoadingState -> loadingState()
        is ContentState.LoadedState -> loadedState()
        is ContentState.EmptyState -> emptyState()
        is ContentState.ErrorState -> errorState()
    }
}