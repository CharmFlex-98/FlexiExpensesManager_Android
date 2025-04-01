package com.charmflex.flexiexpensesmanager.features.auth.service.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.charmflex.flexiexpensesmanager.features.auth.domain.model.Device
import com.charmflex.flexiexpensesmanager.features.auth.storage.AuthStorage
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject



internal class DeviceInfoService @Inject constructor(
    private val appContext: Context,
    private val authStorage: AuthStorage
) {
    fun getDevice(): Device {
        return Device(
            deviceId = getDeviceId(),
            brand = Build.BRAND,
            model = Build.MODEL,
            sdkVersion = Build.VERSION.SDK_INT,
            androidVersion = Build.VERSION.RELEASE
        )
    }

    private fun getDeviceId(): String {
        val storedId = authStorage.getDeviceID()

        if (storedId.isNotBlank()) {
            return storedId  // Return stored ID if already exists
        }

        // Generate a new device ID
        val newDeviceId = generateDeviceId()

        // Store it persistently in SharedPreferences
        authStorage.saveDeviceID(newDeviceId)

        return newDeviceId
    }

    private fun generateDeviceId(): String {
        val rawDeviceId = listOf(
            Build.BOARD, Build.BRAND, Build.DEVICE, Build.HARDWARE,
            Build.ID, Build.MANUFACTURER, Build.MODEL, Build.PRODUCT,
            Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
        ).joinToString("-")

        return hashString(rawDeviceId)
    }

    private fun hashString(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) } // Convert to hex
        } catch (e: Exception) {
            UUID.randomUUID().toString() // Fallback to random UUID
        }
    }
}
