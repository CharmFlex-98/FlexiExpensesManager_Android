package com.charmflex.flexiexpensesmanager.features.auth.data.model

import com.charmflex.flexiexpensesmanager.features.auth.data.model.UpdateUserInfoRequest.DeviceInfo
import com.charmflex.flexiexpensesmanager.features.auth.domain.model.Device
import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
internal data class UpdateUserInfoRequest(
    val uid: String,
    val username: String?,
    val email: String?,
    val deviceInfo: DeviceInfo?
) {
    @JsonClass(generateAdapter = true)
    data class DeviceInfo(
        val deviceId: String,
        val brand: String,
        val model: String,
        val sdkVersion: Int,
        val androidVersion: String
    )
}

@JsonClass(generateAdapter = true)
internal data class UpdateUserInfoResponse(
    val createdAt: Instant,
    val newUser: Boolean
)

internal fun Device.toDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        this.deviceId,
        this.brand,
        this.model,
        this.sdkVersion,
        this.androidVersion
    )
}