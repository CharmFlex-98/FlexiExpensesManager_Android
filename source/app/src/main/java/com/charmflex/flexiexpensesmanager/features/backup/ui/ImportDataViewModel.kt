package com.charmflex.flexiexpensesmanager.features.backup.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.popWithHomeRefresh
import com.charmflex.flexiexpensesmanager.core.navigation.routes.AccountRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TagRoutes
import com.charmflex.flexiexpensesmanager.core.utils.FEFileProvider
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.backup.TransactionBackupManager
import com.charmflex.flexiexpensesmanager.features.backup.checker.ImportDataChecker
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTagEntity
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.ImportTransaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.poi.ss.formula.functions.Index
import java.time.LocalDateTime
import javax.inject.Inject

internal class ImportDataViewModel @Inject constructor(
    private val backupManager: TransactionBackupManager,
    private val fileProvider: FEFileProvider,
    private val importDataChecker: ImportDataChecker,
    private val routeNavigator: RouteNavigator,
    private val transactionTagRepository: TransactionRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow(ImportDataViewState())
    val viewState = _viewState.asStateFlow()

    private var awaitingMissingData: ImportedData.MissingData? = null

    init {
        viewModelScope.launch {
            backupManager.progress.collectLatest { progress ->
                _viewState.update {
                    it.copy(
                        progress = progress
                    )
                }
            }
        }
    }

    fun updateImportedData() {
        viewModelScope.launch {
            val (newImportedData, newMissingData) = importDataChecker.updateRequiredData(
                _viewState.value.missingData,
                _viewState.value.importedData
            )
            _viewState.update {
                it.copy(
                    importedData = newImportedData,
                    missingData = newMissingData
                )
            }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            toggleLoader(true)
            val currentTime = LocalDateTime.now()
            val fileName = "cache_import_file_${currentTime}"
            fileProvider.writeCacheFile(uri, fileName)
            val backupData = backupManager.read(fileName = fileName)
            val (importedData, missingData) = importDataChecker.checkRequiredData(backupData)
            _viewState.update {
                it.copy(
                    importedData = importedData,
                    missingData = missingData,
                    isLoading = false
                )
            }
        }
    }

    fun onFixError(missingData: ImportedData.MissingData) {
        awaitingMissingData = missingData
        when (missingData.dataType) {
            ImportedData.MissingData.DataType.ACCOUNT_FROM, ImportedData.MissingData.DataType.ACCOUNT_TO -> {
                routeNavigator.navigateTo(AccountRoutes.editorDestination(missingData.name))
            }

            ImportedData.MissingData.DataType.INCOME_CATEGORY -> {
                routeNavigator.navigateTo(
                    CategoryRoutes.editorDestination(
                        TransactionType.INCOME,
                        missingData.name
                    )
                )
            }

            ImportedData.MissingData.DataType.EXPENSES_CATEGORY -> {
                routeNavigator.navigateTo(
                    CategoryRoutes.editorDestination(
                        TransactionType.EXPENSES,
                        missingData.name
                    )
                )
            }

            ImportedData.MissingData.DataType.TAG -> {
                routeNavigator.navigateTo(TagRoutes.addNewTagDestination(true, missingData.name))
            }
        }
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    fun saveData() {
        viewModelScope.launch {
            toggleLoader(true)
            val validImportedData = _viewState.value.importedData.filter { it.isValid }
            val importedTransaction = validImportedData.map {
                val fromAccount = (it.accountFrom as? ImportedData.RequiredDataState.Acquired)?.id
                val toAccount = (it.accountTo as? ImportedData.RequiredDataState.Acquired)?.id
                val category = (it.categoryColumns as? ImportedData.RequiredDataState.Acquired)?.id
                val tags = it.tags.mapNotNull {
                    (it as? ImportedData.RequiredDataState.Acquired)?.id
                }
                ImportTransaction(
                    transactionName = it.transactionName,
                    transactionAccountFrom = fromAccount,
                    transactionAccountTo = toAccount,
                    transactionTypeCode = it.transactionType,
                    amountInCent = (it.amount.toBigDecimal().times(100.toBigDecimal())).toLong(),
                    currency = it.currency,
                    rate = it.currencyRate.toFloat(),
                    transactionDate = it.date,
                    transactionCategoryId = category,
                    tagIds = tags
                )
            }
            resultOf {
                transactionTagRepository.addAllImportTransactions(importedTransaction)
            }.fold(
                onSuccess = {
                    toggleLoader(false)
                    routeNavigator.pop()
                },
                onFailure = {
                    Log.d("test", it.message.toString())
                    toggleLoader(false)
                }
            )
        }
    }
}

internal data class ImportDataViewState(
    val isLoading: Boolean = false,
    val importedData: List<ImportedData> = listOf(),
    val missingData: Set<ImportedData.MissingData> = setOf(),
    val progress: Float = 0f
) {

}

internal data class ImportedData(
    val transactionName: String,
    val accountFrom: RequiredDataState?,
    val accountTo: RequiredDataState?,
    val transactionType: String,
    val currency: String,
    val currencyRate: Double,
    val amount: Double,
    val date: String,
    val categoryColumns: RequiredDataState?,
    val tags: List<RequiredDataState>
) {
    val isValid: Boolean
        get() = accountFrom !is RequiredDataState.Missing || accountTo !is RequiredDataState.Missing || categoryColumns !is RequiredDataState.Missing || tags.filterIsInstance(
            RequiredDataState.Missing::class.java
        ).isEmpty()

    sealed interface RequiredDataState {
        val name: String

        data class Acquired(
            val id: Int,
            override val name: String,
        ) : RequiredDataState

        data class Missing(
            override val name: String,
        ) : RequiredDataState
    }

    data class MissingData(
        val name: String,
        val dataType: DataType,
        val transactionIndex: Set<Int>
    ) {
        enum class DataType {
            INCOME_CATEGORY, EXPENSES_CATEGORY, ACCOUNT_FROM, ACCOUNT_TO, TAG
        }
    }
}