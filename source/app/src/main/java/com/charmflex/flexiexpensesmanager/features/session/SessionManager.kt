package com.charmflex.flexiexpensesmanager.features.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SessionManager @Inject constructor(

) {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.None)
    val sessionState = _sessionState.asStateFlow()

    fun updateSessionState(state: SessionState) {
        _sessionState.tryEmit(state)
    }
}

internal interface SessionState {
    object None : SessionState
    object Start : SessionState
}