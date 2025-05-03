package com.charmflex.flexiexpensesmanager.db.migration

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.Migration_1_2

val Migration_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("BEGIN TRANSACTION;")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CategoryBudgetEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category_id` INTEGER NOT NULL, `default_budget_in_cent` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_CategoryBudgetEntity_category_id` ON `CategoryBudgetEntity` (`category_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `MonthlyCategoryBudgetEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `budget_month_year` TEXT NOT NULL, `custom_budget_in_cent` INTEGER NOT NULL, `category_budget_id` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_MonthlyCategoryBudgetEntity_budget_month_year` ON `MonthlyCategoryBudgetEntity` (`budget_month_year`)");
        db.execSQL("COMMIT;");
    }
}

val Migration_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE AccountEntity ADD COLUMN currency TEXT NOT NULL DEFAULT 'MYR'")
    }
}

val Migration_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE TransactionEntity ADD COLUMN primary_currency_rate REAL")
        db.execSQL("ALTER TABLE ScheduledTransactionEntity ADD COLUMN primary_currency_rate REAL")
    }

}

val Migration_5_6 = object  : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        /**
         * Transaction Entity
         */

        // Step 1: Create new table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `TransactionEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_name` TEXT NOT NULL, `account_from_id` INTEGER, `account_to_id` INTEGER, `transaction_type_code` TEXT NOT NULL, `minor_unit_amount` INTEGER NOT NULL, `transaction_date` TEXT NOT NULL, `category_id` INTEGER, `currency` TEXT NOT NULL, `account_currency_rate` REAL NOT NULL, `primary_currency_rate` REAL, `scheduler_id` INTEGER, FOREIGN KEY(`account_from_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`account_to_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`transaction_type_code`) REFERENCES `TransactionTypeEntity`(`code`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`category_id`) REFERENCES `TransactionCategoryEntity`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`scheduler_id`) REFERENCES `ScheduledTransactionEntity`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`currency`) REFERENCES `CurrencyMetaDataEntity`(`currencyCode`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
        )

        // Step 2: Copy data
        db.execSQL(
            "INSERT INTO TransactionEntityNew (id, transaction_name, account_from_id, account_to_id, transaction_type_code, minor_unit_amount, transaction_date, category_id, currency, account_currency_rate, primary_currency_rate, scheduler_id) SELECT id, transaction_name, account_from_id, account_to_id, transaction_type_code, amount_in_cent, transaction_date, category_id, currency, rate, primary_currency_rate, scheduler_id FROM TransactionEntity"
        )
        // Step 3: Drop old table
        db.execSQL("DROP TABLE TransactionEntity")
        // Step 4: Rename new table
        db.execSQL("ALTER TABLE TransactionEntityNew RENAME TO TransactionEntity")

        // Index recreate
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_TransactionEntity_account_from_id` ON `TransactionEntity` (`account_from_id`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_TransactionEntity_account_to_id` ON `TransactionEntity` (`account_to_id`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_TransactionEntity_transaction_type_code` ON `TransactionEntity` (`transaction_type_code`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_TransactionEntity_category_id` ON `TransactionEntity` (`category_id`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_TransactionEntity_scheduler_id` ON `TransactionEntity` (`scheduler_id`)"
        )


        /**
         * Scheduled Transaction Entity
         */
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `ScheduledTransactionEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `scheduled_transaction_name` TEXT NOT NULL, `scheduled_account_from_id` INTEGER, `scheduled_account_to_id` INTEGER, `transaction_type_code` TEXT NOT NULL, `minor_unit_amount` INTEGER NOT NULL, `start_update_date` TEXT NOT NULL, `next_update_date` TEXT NOT NULL, `category_id` INTEGER, `currency` TEXT NOT NULL, `account_currency_rate` REAL NOT NULL, `primary_currency_rate` REAL, `scheduler_period` TEXT NOT NULL, `is_deleted` INTEGER NOT NULL, FOREIGN KEY(`currency`) REFERENCES `CurrencyMetaDataEntity`(`currencyCode`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
        )
        db.execSQL(
            "INSERT INTO ScheduledTransactionEntityNew (id, scheduled_transaction_name, scheduled_account_from_id, scheduled_account_to_id, transaction_type_code, minor_unit_amount, start_update_date, next_update_date, category_id, currency, account_currency_rate, primary_currency_rate, scheduler_period, is_deleted) SELECT id, scheduled_transaction_name, scheduled_account_from_id, scheduled_account_to_id, transaction_type_code, amount_in_cent, start_update_date, next_update_date, category_id, currency, rate, primary_currency_rate, scheduler_period, is_deleted FROM ScheduledTransactionEntity"
        )
        // Step 3: Drop old table
        db.execSQL("DROP TABLE ScheduledTransactionEntity")
        // Step 4: Rename new table
        db.execSQL("ALTER TABLE ScheduledTransactionEntityNew RENAME TO ScheduledTransactionEntity")

        // Recreate index
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_ScheduledTransactionEntity_id_is_deleted` ON `ScheduledTransactionEntity` (`id`, `is_deleted`)"
        )
    }

}

@DeleteColumn.Entries (
    DeleteColumn("TransactionEntity", "account_currency_rate"),
    DeleteColumn("TransactionEntity", "primary_currency_rate"),
    DeleteColumn("ScheduledTransactionEntity", "account_currency_rate"),
    DeleteColumn("ScheduledTransactionEntity", "primary_currency_rate")
)
class RemoveAccountAndPrimaryCurrencyRateFromTransactionEntity: AutoMigrationSpec
