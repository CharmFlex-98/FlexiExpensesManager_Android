package com.charmflex.flexiexpensesmanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.charmflex.flexiexpensesmanager.db.migration.MIGRATION_1_2
import com.charmflex.flexiexpensesmanager.db.migration.MIGRATION_2_3
import com.charmflex.flexiexpensesmanager.db.migration.MIGRATION_3_4
import com.charmflex.flexiexpensesmanager.db.migration.MIGRATION_4_5
import com.charmflex.flexiexpensesmanager.features.account.data.daos.AccountDao
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountEntity
import com.charmflex.flexiexpensesmanager.features.account.data.entities.AccountGroupEntity
import com.charmflex.flexiexpensesmanager.features.currency.data.daos.CurrencyDao
import com.charmflex.flexiexpensesmanager.features.currency.data.models.UserCurrencyRateEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionCategoryDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.daos.TransactionDao
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TagEntity
import com.charmflex.flexiexpensesmanager.features.transactions.data.entities.TransactionCategoryEntity
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
        UserCurrencyRateEntity::class
    ],
    version = 5,
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao
    abstract fun getTransactionDao(): TransactionDao
    abstract fun getTransactionCategoryDao(): TransactionCategoryDao
    abstract fun getCurrencyDao(): CurrencyDao

    class Builder(
        private val appContext: Context
    ) {
        fun build(): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "FlexiExpensesManagerDB"
            )
                .addMigrations(*migrationList().toTypedArray())
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
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5
    )
}

private const val INIT_ACCOUNT_GROUP_SCRIPT =
    "INSERT INTO AccountGroupEntity (id, name) VALUES (0, 'Account'), (1, 'Cash')"
private const val INIT_ACCOUNT_SCRIPT =
    "INSERT INTO AccountEntity (name, account_group_id) VALUES ('Cash', 1)"
private const val INIT_TRANSACTION_TYPE_SCRIPT =
    "INSERT INTO TransactionTypeEntity (code) VALUES ('INCOME'), ('EXPENSES'), ('TRANSFER')"
private const val INIT_TRANSACTION_CATEGORY_SCRIPT =
    "INSERT INTO TransactionCategoryEntity (id, transaction_type_code, name, parent_id) VALUES (1, 'EXPENSES', 'Food', 0), (2, 'INCOME', 'Salary', 0), (3, 'EXPENSES', 'Rental', 0), (4, 'EXPENSES', 'Lunch', 1)"