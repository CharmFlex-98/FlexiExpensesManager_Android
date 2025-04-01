package com.charmflex.flexiexpensesmanager.features.auth.domain.model

internal data class Device(
    val deviceId: String,
    val brand: String,
    val model: String,
    val sdkVersion: Int,
    val androidVersion: String
)