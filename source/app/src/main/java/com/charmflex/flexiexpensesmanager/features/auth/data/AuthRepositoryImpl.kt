package com.charmflex.flexiexpensesmanager.features.auth.data

import com.charmflex.flexiexpensesmanager.features.auth.data.model.UpdateUserInfoRequest
import com.charmflex.flexiexpensesmanager.features.auth.data.model.toDeviceInfo
import com.charmflex.flexiexpensesmanager.features.auth.data.remote.AuthApi
import com.charmflex.flexiexpensesmanager.features.auth.domain.model.UserInfo
import com.charmflex.flexiexpensesmanager.features.auth.domain.repository.AuthRepository
import com.charmflex.flexiexpensesmanager.features.auth.service.device.DeviceInfoService
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val deviceInfoService: DeviceInfoService
) : AuthRepository {
    override suspend fun upsertUser(
        uid: String,
        username: String?,
        email: String?
    ) : UserInfo {
        val res = authApi.upsertUser(UpdateUserInfoRequest(uid, username, email, deviceInfoService.getDevice().toDeviceInfo()))
        return UserInfo(
            uid = uid,
            username = username,
            email = email,
            createdAt = res.createdAt,
            isNewUser = res.newUser
        )
    }
}