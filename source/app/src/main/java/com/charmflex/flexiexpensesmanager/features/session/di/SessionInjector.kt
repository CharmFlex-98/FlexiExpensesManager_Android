package com.charmflex.flexiexpensesmanager.features.session.di

import com.charmflex.flexiexpensesmanager.features.session.SessionManager

internal interface SessionInjector {

    fun sessionManager(): SessionManager
}