package com.charmflex.flexiexpensesmanager.core.storage

import android.content.Context
import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import javax.inject.Inject

internal interface FileStorage {

    suspend fun write(fileName: String, byteArray: ByteArray)

    suspend fun read(fileName: String): String

}

internal class FileStorageImpl @Inject constructor(
    private val appContext: Context,
    @Dispatcher(dispatcherType = Dispatcher.Type.IO)
    private val dispatcher: CoroutineDispatcher
) : FileStorage {
    override suspend fun write(fileName: String, byteArray: ByteArray) = withContext(dispatcher) {
        val filesDir = appContext.filesDir
        val file = File(filesDir, fileName).apply { createNewFile() }
        FileOutputStream(file).use {
            it.write(byteArray)
        }
    }

    override suspend fun read(fileName: String): String = withContext(dispatcher) {
        val filesDir = appContext.filesDir
        val file = File(filesDir, fileName)
        FileInputStream(file).bufferedReader().use {
            it.readText()
        }
    }
}

internal const val FOLDER_NAME = "FE_FOLDER"
