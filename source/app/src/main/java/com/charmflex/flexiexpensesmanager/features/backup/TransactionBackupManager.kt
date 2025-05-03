package com.charmflex.flexiexpensesmanager.features.backup

import android.content.Context
import android.content.Intent
import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.FEFileProvider
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.backup.data.mapper.TransactionBackupDataMapper
import com.charmflex.flexiexpensesmanager.features.backup.elements.Sheet
import com.charmflex.flexiexpensesmanager.features.backup.elements.workbook
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate
import javax.inject.Inject

internal interface TransactionBackupManager {
    val progress: Flow<Float>

    suspend fun read(fileName: String): List<TransactionBackupData>

    suspend fun write(fileName: String)

    suspend fun share(fileName: String)
}

internal class TransactionBackupManagerImpl @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val transactionCategoryRepository: TransactionCategoryRepository,
    private val transactionBackupDataMapper: TransactionBackupDataMapper,
    private val fileProvider: FEFileProvider,
    private val appContext: Context,
    private val currencyFormatter: CurrencyFormatter,
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
                    val columnsItemsMap = getColumns()
                    val transactionName = row.safeGetCell(columnsItemsMap[ExcelColumns.TRANSACTION_NAME]!!.index).stringCellValue
                    val accountFrom = row.safeGetCell(columnsItemsMap[ExcelColumns.ACCOUNT_FROM]!!.index).stringCellValue
                    val accountTo = row.safeGetCell(columnsItemsMap[ExcelColumns.ACCOUNT_TO]!!.index).stringCellValue
                    val transactionType = row.safeGetCell(columnsItemsMap[ExcelColumns.TRANSACTION_TYPE]!!.index).stringCellValue
                    val currency = row.safeGetCell(columnsItemsMap[ExcelColumns.CURRENCY]!!.index).stringCellValue
                    val amount = row.safeGetCell(columnsItemsMap[ExcelColumns.AMOUNT]!!.index).numericCellValue
                    val accountMinorUnitAmount = row.safeGetCell(columnsItemsMap[ExcelColumns.ACCOUNT_AMOUNT]!!.index).numericCellValue
                    val primaryMinorUnitAmount = row.safeGetCell(columnsItemsMap[ExcelColumns.PRIMARY_AMOUNT]!!.index).numericCellValue
                    val date = row.safeGetCell(columnsItemsMap[ExcelColumns.DATE]!!.index).dateCellValue?.toLocalDate()
                    val categoryColumns = listOf(
                        row.safeGetCell(columnsItemsMap[ExcelColumns.CATEGORY1]!!.index).stringCellValue,
                        row.safeGetCell(columnsItemsMap[ExcelColumns.CATEGORY2]!!.index).stringCellValue,
                        row.safeGetCell(columnsItemsMap[ExcelColumns.CATEGORY3]!!.index).stringCellValue
                    )
                    val tags = row.safeGetCell(columnsItemsMap[ExcelColumns.TAGS]!!.index).stringCellValue.split("#").map {
                        it.trim()
                    }


                    // Break if there is no value anymore.
                    if (date == null) break

                    data.add(
                        TransactionBackupData(
                            transactionName = transactionName,
                            accountFrom = accountFrom?.ifBlank { null },
                            accountTo = accountTo?.ifBlank { null },
                            transactionType = transactionType,
                            currency = currency,
                            accountAmount = accountMinorUnitAmount,
                            primaryAmount = primaryMinorUnitAmount,
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
            val categoriesMap = transactionCategoryRepository.getAllCategoriesIncludedDeleted().groupBy {
                it.id
            }.mapValues {
                // only 1 s=unique id per category
                it.value[0]
            }
            transactionRepository.getTransactions().firstOrNull()?.let { transactions ->
                val excelData = transactionBackupDataMapper.map(transactions to categoriesMap)
                workbook(xssfWorkbook) {
                    sheet("guide") {}
                    sheet("record") {
                        val columnNames = getColumns().values
                        row {
                            for (element in columnNames) {
                                stringCell(element.columnName)
                            }
                        }
                        excelData.forEach {
                            buildRow(
                                transactionName = it.transactionName,
                                accountFrom = it.accountFrom,
                                accountTo = it.accountTo,
                                transactionType = it.transactionType,
                                currencyType = it.currency,
                                amount = it.amount,
                                accountAmount = it.accountAmount,
                                primaryAmount = it.primaryAmount,
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
        amount: Double,
        accountAmount: Double,
        primaryAmount: Double,
        date: LocalDate,
        categoryColumns: List<String>,
        tags: List<String>
    ) {
        row {
            stringCell(transactionName)
            if (accountFrom != null) stringCell(accountFrom) else emptyCell()
            if (accountTo != null) stringCell(accountTo) else emptyCell()
            stringCell(transactionType)
            stringCell(currencyType)
            doubleCell(amount)
            doubleCell(accountAmount)
            doubleCell(primaryAmount)
            dateCell(date)
            for (c in 0..2) {
                if (c <= categoryColumns.size - 1) stringCell(categoryColumns[c])
                else emptyCell()
            }
            tags.joinTags().let { if (it.isEmpty().not()) stringCell(it) else emptyCell() }
        }
    }

    private fun getColumns(): Map<String, ExcelColumn> {
        return mapOf(
            ExcelColumns.TRANSACTION_NAME to ExcelColumn(0, ExcelColumns.TRANSACTION_NAME),
            ExcelColumns.ACCOUNT_FROM to ExcelColumn(1, ExcelColumns.ACCOUNT_FROM),
            ExcelColumns.ACCOUNT_TO to ExcelColumn(2, ExcelColumns.ACCOUNT_TO),
            ExcelColumns.TRANSACTION_TYPE to ExcelColumn(3, ExcelColumns.TRANSACTION_TYPE),
            ExcelColumns.CURRENCY to ExcelColumn(4, ExcelColumns.CURRENCY),
            ExcelColumns.AMOUNT to ExcelColumn(5, ExcelColumns.AMOUNT),
            ExcelColumns.ACCOUNT_AMOUNT to ExcelColumn(6, ExcelColumns.ACCOUNT_AMOUNT),
            ExcelColumns.PRIMARY_AMOUNT to ExcelColumn(7, ExcelColumns.PRIMARY_AMOUNT),
            ExcelColumns.DATE to ExcelColumn(8, ExcelColumns.DATE),
            ExcelColumns.CATEGORY1 to ExcelColumn(9, ExcelColumns.CATEGORY1),
            ExcelColumns.CATEGORY2 to ExcelColumn(10, ExcelColumns.CATEGORY2),
            ExcelColumns.CATEGORY3 to ExcelColumn(11, ExcelColumns.CATEGORY3),
            ExcelColumns.TAGS to ExcelColumn(12, ExcelColumns.TAGS)
        )
    }

    private fun List<String>.joinTags(): String {
        if (this.isEmpty()) return ""

        return buildString {
            this@joinTags.forEachIndexed { index, tag ->
                append("#$tag")
                // If it is not the last, append another space for next hashtag #
                if (index != this@joinTags.size - 1) {
                    append(" ")
                }
            }
        }
    }
}

internal data class TransactionBackupData(
    val transactionName: String,
    val accountFrom: String?,
    val accountTo: String?,
    val transactionType: String,
    val currency: String,
    val accountAmount: Double,
    val primaryAmount: Double,
    val amount: Double,
    val date: LocalDate,
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

private object ExcelColumns {
    const val TRANSACTION_NAME = "Transaction Name"
    const val ACCOUNT_FROM = "Account From"
    const val ACCOUNT_TO = "Account To"
    const val TRANSACTION_TYPE = "Transaction Type"
    const val CURRENCY = "Currency"
    const val RATE = "Rate"
    const val PRIMARY_CURRENCY_RATE = "Primary Currency Rate"
    const val AMOUNT = "Amount"
    const val ACCOUNT_AMOUNT = "Account Amount"
    const val PRIMARY_AMOUNT = "Primary Amount"
    const val DATE = "Date"
    const val CATEGORY1 = "Category1"
    const val CATEGORY2 = "Category2"
    const val CATEGORY3 = "Category3"
    const val TAGS = "Tags"
}

