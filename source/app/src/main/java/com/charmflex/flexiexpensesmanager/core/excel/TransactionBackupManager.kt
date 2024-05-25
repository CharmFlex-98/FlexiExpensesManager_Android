package com.charmflex.flexiexpensesmanager.core.excel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.charmflex.flexiexpensesmanager.core.di.Dispatcher
import com.charmflex.flexiexpensesmanager.core.excel.elements.Sheet
import com.charmflex.flexiexpensesmanager.core.excel.elements.workbook
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategory
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import javax.inject.Inject

internal interface TransactionBackupManager {

    suspend fun read()

    suspend fun write(fileName: String)

    suspend fun share(fileName: String)
}

internal class TransactionBackupManagerImpl @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val transactionBackupDataMapper: TransactionBackupDataMapper,
    private val appContext: Context,
    @Dispatcher(Dispatcher.Type.IO)
    private val dispatcher: CoroutineDispatcher,
) : TransactionBackupManager {
    private val xssfWorkbook
        get() = XSSFWorkbook()

    override suspend fun read() {
        TODO("Not yet implemented")
    }

    override suspend fun write(fileName: String) {
        val path = appContext.cacheDir
        withContext(dispatcher) {
            val file = File(path, fileName).apply { createNewFile() }
            transactionRepository.getTransactions().firstOrNull()?.let { transactions ->
                val excelData = transactionBackupDataMapper.map(transactions)
                workbook(xssfWorkbook) {
                    sheet("guide") {}
                    sheet("record") {
                        row {
                            stringCell("Transaction Name")
                            stringCell("Account From")
                            stringCell("Account To")
                            stringCell("Transaction Type")
                            stringCell("Currency")
                            stringCell("Rate")
                            stringCell("Amount")
                            stringCell("Date")
                            stringCell("Category1")
                            stringCell("Category2")
                            stringCell("Category3")
                            stringCell("Tags")
                        }
                        Log.d("transactions", transactions.toString())
                        excelData.forEach {
                            buildRow(
                                transactionName = it.transactionName,
                                accountFrom = it.accountFrom,
                                accountTo = it.accountTo,
                                transactionType = it.transactionType,
                                currencyType = it.currencyType,
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
        val path = appContext.cacheDir
        val file = File(path, fileName)
        val fileUri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.provider",
            file
        )
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
        categoryColumns: List<TransactionCategory>,
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
                if (c <= categoryColumns.size - 1) stringCell(categoryColumns[c].name)
                else emptyCell()
            }
            tags.joinToString().let { if (it.isEmpty().not()) stringCell(it) else emptyCell() }
        }
    }

    private fun generateCategoryColumns(
        columns: MutableList<TransactionCategory>,
        transactionCategoryMap: Map<Int, TransactionCategory>,
        currentCategory: TransactionCategory?
    ): List<TransactionCategory> {
        Log.d("game", columns.toList().toString())
        return if (currentCategory == null) return emptyList()
        else if (currentCategory.parentId == 0) return columns
        else {
            val parentCategory = transactionCategoryMap[currentCategory.parentId] ?: return columns
            generateCategoryColumns(
                columns.apply { add(parentCategory) },
                transactionCategoryMap,
                parentCategory
            )
        }
    }
}

internal data class TransactionBackupData(
    val transactionName: String,
    val accountFrom: String?,
    val accountTo: String?,
    val transactionType: String,
    val currencyType: String,
    val currencyRate: Double,
    val amount: Double,
    val date: String,
    val categoryColumns: List<TransactionCategory>,
    val tags: List<String>
)

