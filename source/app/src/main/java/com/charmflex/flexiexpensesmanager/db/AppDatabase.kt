package com.charmflex.flexiexpensesmanager.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.charmflex.flexiexpensesmanager.db.migration.Migration_1_2
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountTransactionDao
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountGroupEntity
import com.charmflex.flexiexpensesmanager.features.budget.data.daos.CategoryBudgetDao
import com.charmflex.flexiexpensesmanager.features.budget.data.entities.CategoryBudgetEntity
import com.charmflex.flexiexpensesmanager.features.budget.data.entities.MonthlyCategoryBudgetEntity
import com.charmflex.flexiexpensesmanager.features.currency.data.daos.CurrencyDao
import com.charmflex.flexiexpensesmanager.features.currency.data.models.UserCurrencyRateEntity
import com.charmflex.flexiexpensesmanager.features.scheduler.data.daos.ScheduledTransactionDao
import com.charmflex.flexiexpensesmanager.features.scheduler.data.daos.ScheduledTransactionTagDao
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionEntity
import com.charmflex.flexiexpensesmanager.features.scheduler.data.entities.ScheduledTransactionTagEntity
import com.charmflex.flexiexpensesmanager.features.tag.data.daos.TagDao
import com.charmflex.flexiexpensesmanager.features.category.category.data.daos.TransactionCategoryDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.tag.data.entities.TagEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionTagDao
import com.charmflex.flexiexpensesmanager.features.category.category.data.entities.TransactionCategoryEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTagEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionTypeEntity

@Database(
    entities = [
        AccountEntity::class,
        AccountGroupEntity::class,
        TagEntity::class,
        TransactionCategoryEntity::class,
        TransactionEntity::class,
        TransactionTagEntity::class,
        TransactionTypeEntity::class,
        UserCurrencyRateEntity::class,
        ScheduledTransactionEntity::class,
        ScheduledTransactionTagEntity::class,
        CategoryBudgetEntity::class,
        MonthlyCategoryBudgetEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao
    abstract fun getTransactionDao(): TransactionDao
    abstract fun getTransactionCategoryDao(): TransactionCategoryDao
    abstract fun getCurrencyDao(): CurrencyDao
    abstract fun getTagDao(): TagDao
    abstract fun getTransactionTagDao(): TransactionTagDao
    abstract fun getAccountTransactionDao(): AccountTransactionDao
    abstract fun getScheduledTransactionDao(): ScheduledTransactionDao
    abstract fun getScheduledTransactionTagDao(): ScheduledTransactionTagDao
    abstract fun getCategoryBudgetDao(): CategoryBudgetDao

    class Builder(
        private val appContext: Context
    ) {
        fun build(): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "FlexiExpensesManagerDB"
            )
//                .addMigrations(*migrationList().toTypedArray())
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL(INIT_ACCOUNT_GROUP_SCRIPT)
                        db.execSQL(INIT_ACCOUNT_SCRIPT)
                        db.execSQL(INIT_TRANSACTION_TYPE_SCRIPT)
                        db.execSQL(INIT_TRANSACTION_CATEGORY_SCRIPT)
                    }
                })
                .build()
        }
    }
}

private fun migrationList(): List<Migration> {
    return listOf(
        Migration_1_2,
    )
}

private const val INIT_ACCOUNT_GROUP_SCRIPT =
    "INSERT INTO AccountGroupEntity (id, name) VALUES (0, '${AccountGroupName.BANK_ACCOUNT}'), (1, '${AccountGroupName.CASH}'), (2, '${AccountGroupName.CREDIT_CARD}'), (3, '${AccountGroupName.DEBIT_CARD}'), (4, '${AccountGroupName.INSURANCE}'), (5, '${AccountGroupName.INVESTMENT}'), (6, '${AccountGroupName.LOAN}'), (7, '${AccountGroupName.PREPAID}'), (8, '${AccountGroupName.SAVING}'), (9, '${AccountGroupName.OTHERS}')"
private const val INIT_ACCOUNT_SCRIPT =
    "INSERT INTO AccountEntity (name, account_group_id) VALUES ('Cash', 1)"
private const val INIT_TRANSACTION_TYPE_SCRIPT =
    "INSERT INTO TransactionTypeEntity (code) VALUES ('INCOME'), ('EXPENSES'), ('TRANSFER'), ('UPDATE_ACCOUNT')"
private const val INIT_TRANSACTION_CATEGORY_SCRIPT =
    "INSERT INTO TransactionCategoryEntity (id, transaction_type_code, name, parent_id) VALUES (1, 'EXPENSES', 'Food', 0), (2, 'INCOME', 'Salary', 0), (3, 'EXPENSES', 'Rental', 0), (4, 'EXPENSES', 'Lunch', 1)"