package com.charmflex.flexiexpensesmanager.db.di.modules

import com.charmflex.flexiexpensesmanager.db.AppDatabase
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.currency.data.daos.CurrencyDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionCategoryDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import dagger.Module
import dagger.Provides

@Module
internal interface DaoModule {

    companion object {
        @Provides
        fun accountDao(db: AppDatabase): AccountDao {
            return db.getAccountDao()
        }

        @Provides
        fun transactionDao(db: AppDatabase): TransactionDao {
            return db.getTransactionDao()
        }

        @Provides
        fun transactionCategoryDao(db: AppDatabase): TransactionCategoryDao {
            return db.getTransactionCategoryDao()
        }

        @Provides
        fun currencyDao(db: AppDatabase): CurrencyDao {
            return db.getCurrencyDao()
        }
    }
}