package com.charmflex.flexiexpensesmanager.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val FEM_SHARED_PREFS_FILE_PATH = "FLEXI-EXPENSES-MANAGER-SHARED-PREFERENCES-PATH"


internal interface SharedPrefs {

    suspend fun setInt(key: String, value: Int)
    suspend fun getInt(key: String, default: Int): Int
    suspend fun setString(key: String, value: String)

    suspend fun getString(key: String, default: String): String

    suspend fun setFloat(key: String, value: Float)

    suspend fun getFloat(key: String, default: Float): Float

    suspend fun setBoolean(key: String, value: Boolean)

    suspend fun getBoolean(key: String, default: Boolean): Boolean

    suspend fun setStringSet(key: String, value: Set<String>)
    suspend fun getStringSet(key: String): Set<String>

    suspend fun clearAllData()
}

// Secure SharedPreferences wrapper
internal class SharedPrefsImpl @Inject constructor(
    private val appContext: Context,
    @Dispatcher(Dispatcher.Type.IO)
    private val dispatcher: CoroutineDispatcher
) : SharedPrefs {

    private val sharedPrefs: SharedPreferences by lazy { initSharedPreferences() }

    @Synchronized
    private fun initSharedPreferences(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            FEM_SHARED_PREFS_FILE_PATH,
            generateMainKeyAlias(),
            appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun generateMainKeyAlias(): String {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        return MasterKeys.getOrCreate(keyGenParameterSpec)
    }

    override suspend fun setInt(key: String, value: Int) {
        withContext(dispatcher) {
            sharedPrefs.edit().putInt(key, value).apply()
        }
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return withContext(dispatcher) {
            sharedPrefs.getInt(key, default) ?: default
        }
    }

    override suspend fun setString(key: String, value: String) {
        withContext(dispatcher) {
            sharedPrefs.edit().putString(key, value).apply()
        }
    }

    override suspend fun getString(key: String, default: String): String {
        return withContext(dispatcher) {
            sharedPrefs.getString(key, default) ?: default
        }
    }

    override suspend fun setFloat(key: String, value: Float) {
        return withContext(dispatcher) {
            sharedPrefs.edit().putFloat(key, value).apply()
        }
    }

    override suspend fun getFloat(key: String, default: Float): Float {
        return withContext(dispatcher) {
            sharedPrefs.getFloat(key, default)
        }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        return withContext(dispatcher) {
            sharedPrefs.edit().putBoolean(key, value).apply()
        }
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return withContext(dispatcher) {
            sharedPrefs.getBoolean(key, default)
        }
    }

    override suspend fun setStringSet(key: String, value: Set<String>) {
        return withContext(dispatcher) {
            sharedPrefs.edit().putStringSet(key, value).apply()
        }
    }

    override suspend fun getStringSet(key: String): Set<String> {
        return withContext(dispatcher) {
            sharedPrefs.getStringSet(key, setOf()) ?: setOf()
        }
    }

    override suspend fun clearAllData() {
        withContext(dispatcher) {
            sharedPrefs.edit().clear().apply()
        }
    }
}