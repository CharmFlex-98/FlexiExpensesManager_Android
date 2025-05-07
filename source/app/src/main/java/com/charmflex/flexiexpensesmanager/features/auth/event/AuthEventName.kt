package com.charmflex.flexiexpensesmanager.features.auth.event

internal object AuthEventName {
    const val USER_ON_GUEST_LOGIN = "USER_ON_GUEST_LOGIN"
    const val USER_SIGN_IN_FAILED = "USER_SIGN_IN_FAILED"
    const val USER_SIGN_IN_SUCCEEDED = "USER_SIGN_IN_SUCCEEDED"
    const val USER_TRY_SIGN_IN_FAILED = "USER_TRY_SIGN_IN_FAILED"
    const val USER_TRY_SIGN_IN_SUCCEEDED = "USER_TRY_SIGN_IN_SUCCEEDED"
    const val USER_NAVIGATE_HOME = "USER_NAVIGATE_HOME"
}