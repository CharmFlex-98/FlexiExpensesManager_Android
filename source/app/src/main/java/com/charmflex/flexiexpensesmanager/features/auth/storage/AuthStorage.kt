package com.charmflex.flexiexpensesmanager.features.auth.storage

import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import javax.inject.Inject
import kotlin.math.log

internal interface AuthStorage {
    companion object {
        const val DEVICE_ID_KEY = "DEVICE_ID_KEY"
        const val LOGIN_ID_KEY = "LOGIN_ID_KEY"
    }
    fun saveDeviceID(deviceID: String)
    fun getDeviceID(): String
    fun saveLoginID(loginID: String)
    fun getLoginID(): String
}

internal class AuthStorageImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : AuthStorage {
    override fun saveDeviceID(deviceID: String) {
        sharedPrefs.setString(AuthStorage.DEVICE_ID_KEY, deviceID)
    }

    override fun getDeviceID(): String {
        return sharedPrefs.getString(AuthStorage.DEVICE_ID_KEY, "")
    }

    override fun saveLoginID(loginID: String) {
        return sharedPrefs.setString(AuthStorage.LOGIN_ID_KEY, loginID)
    }

    override fun getLoginID(): String {
        return sharedPrefs.getString(AuthStorage.LOGIN_ID_KEY, "")
    }

}