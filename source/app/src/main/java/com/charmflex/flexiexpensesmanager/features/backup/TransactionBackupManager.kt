package com.charmflex.flexiexpensesmanager.features.backup

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.core.utils.FEFileProvider
import com.charmflex.flexiexpensesmanager.features.backup.data.mapper.TransactionBackupDataMapper
import com.charmflex.flexiexpensesmanager.features.backup.elements.Sheet
import com.charmflex.flexiexpensesmanager.features.backup.elements.workbook
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import javax.inject.Inject

internal interface TransactionBackupManager {
    val progress: Flow<Float>

    suspend fun read(fileName: String): List<TransactionBackupData>

    suspend fun write(fileName: String)

    suspend fun share(fileName: String)
}

internal class TransactionBackupManagerImpl @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val transactionBackupDataMapper: TransactionBackupDataMapper,
    private val fileProvider: FEFileProvider,
    private val appContext: Context,
    @Dispatcher(Dispatcher.Type.IO)
    private val dispatcher: CoroutineDispatcher,
) : TransactionBackupManager {
    private val xssfWorkbook
        get() = XSSFWorkbook()

    private val _progress = MutableSharedFlow<Float>(extraBufferCapacity = 1)
    override val progress: Flow<Float>
        get() = _progress.asSharedFlow()

    override suspend fun read(fileName: String): List<TransactionBackupData> {
        return withContext(dispatcher) {
            val file = fileProvider.getCacheFile(fileName)
            val uri = fileProvider.getUriFromFile(file)
            val data = mutableListOf<TransactionBackupData>()
            appContext.contentResolver.openInputStream(uri).use {
                val workbook = XSSFWorkbook(it)
                val sheet = workbook.getSheet("record")
                val totalRowNum = sheet.lastRowNum
                for (row in sheet.rowIterator()) {
                    if (row.rowNum == 0) continue
                    _progress.tryEmit(row.rowNum.toFloat()/totalRowNum)
                    val transactionName = row.safeGetCell(0).stringCellValue
                    val accountFrom = row.safeGetCell(1).stringCellValue
                    val accountTo = row.safeGetCell(2).stringCellValue
                    val transactionType = row.safeGetCell(3).stringCellValue
                    val currency = row.safeGetCell(4).stringCellValue
                    val currencyRate = row.safeGetCell(5).numericCellValue
                    val amount = row.safeGetCell(6).numericCellValue
                    val date = row.safeGetCell(7).stringCellValue
                    val categoryColumns = listOf(
                        row.safeGetCell(8).stringCellValue,
                        row.safeGetCell(9).stringCellValue,
                        row.safeGetCell(10).stringCellValue
                    )
                    val tags = row.safeGetCell(11).stringCellValue.split(",")
                    data.add(
                        TransactionBackupData(
                            transactionName = transactionName,
                            accountFrom = accountFrom?.ifBlank { null },
                            accountTo = accountTo?.ifBlank { null },
                            transactionType = transactionType,
                            currency = currency,
                            currencyRate = currencyRate,
                            amount = amount,
                            date = date,
                            categoryColumns = categoryColumns.filter { it.isNotBlank() },
                            tags = tags.filter { it.isNotBlank() }
                        )
                    )
                }
            }
            data
        }
    }

    override suspend fun write(fileName: String) {
        val file = fileProvider.getCacheFile(fileName = fileName)
        withContext(dispatcher) {
            transactionRepository.getTransactions().firstOrNull()?.let { transactions ->
                val excelData = transactionBackupDataMapper.map(transactions)
                workbook(xssfWorkbook) {
                    sheet("guide") {}
                    sheet("record") {
                        val columnNames = getColumns().map { it.columnName }
                        row {
                            for (element in columnNames) {
                                stringCell(element)
                            }
                        }
                        excelData.forEach {
                            buildRow(
                                transactionName = it.transactionName,
                                accountFrom = it.accountFrom,
                                accountTo = it.accountTo,
                                transactionType = it.transactionType,
                                currencyType = it.currency,
                                currencyRate = it.currencyRate,
                                amount = it.amount,
                                date = it.date,
                                categoryColumns = it.categoryColumns,
                                tags = it.tags
                            )
                        }
                    }
                }.write(file)
            }
        }
    }

    override suspend fun share(fileName: String) {
        val file = fileProvider.getCacheFile(fileName)
        val fileUri = fileProvider.getUriFromFile(file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(Intent.createChooser(shareIntent, "Share").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun Sheet.buildRow(
        transactionName: String,
        accountFrom: String?,
        accountTo: String?,
        transactionType: String,
        currencyType: String,
        currencyRate: Double,
        amount: Double,
        date: String,
        categoryColumns: List<String>,
        tags: List<String>
    ) {
        row {
            stringCell(transactionName)
            if (accountFrom != null) stringCell(accountFrom) else emptyCell()
            if (accountTo != null) stringCell(accountTo) else emptyCell()
            stringCell(transactionType)
            stringCell(currencyType)
            doubleCell(currencyRate)
            doubleCell(amount)
            stringCell(date)
            for (c in 0..2) {
                if (c <= categoryColumns.size - 1) stringCell(categoryColumns[c])
                else emptyCell()
            }
            tags.joinToString().let { if (it.isEmpty().not()) stringCell(it) else emptyCell() }
        }
    }

    private fun getColumns(): List<ExcelColumn> {
        return listOf(
            ExcelColumn(0, "Transaction Name"),
            ExcelColumn(1, "Account From"),
            ExcelColumn(2, "Account To"),
            ExcelColumn(3, "Transaction Type"),
            ExcelColumn(4, "Currency"),
            ExcelColumn(5, "Rate"),
            ExcelColumn(6, "Amount"),
            ExcelColumn(7, "Date"),
            ExcelColumn(8, "Category1"),
            ExcelColumn(9, "Category2"),
            ExcelColumn(10, "Category3"),
            ExcelColumn(11, "Tags"),
        )
    }
}

internal data class TransactionBackupData(
    val transactionName: String,
    val accountFrom: String?,
    val accountTo: String?,
    val transactionType: String,
    val currency: String,
    val currencyRate: Double,
    val amount: Double,
    val date: String,
    val categoryColumns: List<String>,
    val tags: List<String>
)

private data class ExcelColumn(
    val index: Int,
    val columnName: String
)

private fun Row.safeGetCell(index: Int): Cell {
    return this.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
}

