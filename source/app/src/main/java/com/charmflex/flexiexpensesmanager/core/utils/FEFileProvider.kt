package com.charmflex.flexiexpensesmanager.core.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal interface FEFileProvider {

    suspend fun writeCacheFile(uri: Uri, fileName: String): File

    suspend fun createCacheFile(fileName: String): File

    suspend fun getCacheFile(fileName: String): File

    suspend fun getUriFromFile(file: File): Uri
}

internal class FEFileProviderImpl @Inject constructor(
    private val appContext: Context,
    @Dispatcher(Dispatcher.Type.IO)
    private val dispatcher: CoroutineDispatcher
) : FEFileProvider {
    override suspend fun writeCacheFile(uri: Uri, fileName: String): File {
        return withContext(dispatcher) {
            val path = appContext.cacheDir
            val file = File(path, fileName).apply { createNewFile() }
            val outputStream = FileOutputStream(file)
            appContext.contentResolver.openInputStream(uri).use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            file
        }
    }

    override suspend fun createCacheFile(fileName: String): File {
        return withContext(dispatcher) {
            val path = appContext.cacheDir
            val file = File(path, fileName).apply { createNewFile() }
            file
        }
    }

    override suspend fun getCacheFile(fileName: String): File {
        val path = appContext.cacheDir
        return File(path, fileName)
    }

    override suspend fun getUriFromFile(file: File): Uri {
        return withContext(dispatcher) {
            FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.provider",
                file
            )
        }
    }
}