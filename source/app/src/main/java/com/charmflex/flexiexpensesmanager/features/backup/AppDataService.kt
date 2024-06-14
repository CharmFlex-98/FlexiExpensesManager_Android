package com.charmflex.flexiexpensesmanager.features.backup

import android.content.Context
import android.util.Log
import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import com.charmflex.flexiexpensesmanager.db.AppDatabase
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

internal interface AppDataService {
    suspend fun clearAppData(appDataClearServiceType: AppDataClearServiceType)
}

internal enum class AppDataClearServiceType {
    TRANSACTION_ONLY, ALL
}

internal class AppDataServiceImpl @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val sharedPrefs: SharedPrefs,
    private val appContext: Context,
    private val database: AppDatabase
) : AppDataService {
    override suspend fun clearAppData(appDataClearServiceType: AppDataClearServiceType) {
        when (appDataClearServiceType) {
            AppDataClearServiceType.ALL -> {
                database.close()
                appContext.deleteDatabase("FlexiExpensesManagerDB")
                sharedPrefs.clearAllData()
            }
            AppDataClearServiceType.TRANSACTION_ONLY -> {
                transactionRepository.deleteAllTransactions()
            }
        }
    }
}