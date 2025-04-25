package com.charmflex.flexiexpensesmanager.db.migration

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
// The migration here basically just change the type of categoryId from not nullable to nullable.
// Because transfer type transaction does not support category.
//val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("BEGIN TRANSACTION;")
//
//        // Create New Transaction Table
//        db.execSQL("CREATE TABLE IF NOT EXISTS `TransactionEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_name` TEXT NOT NULL, `account_from_id` INTEGER, `account_to_id` INTEGER, `transaction_type_code` TEXT NOT NULL, `amount_in_cent` INTEGER NOT NULL, `transaction_date` TEXT NOT NULL, `category_id` INTEGER, FOREIGN KEY(`account_from_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`account_to_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transaction_type_code`) REFERENCES `TransactionTypeEntity`(`code`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `TransactionCategoryEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_account_from_id` ON `TransactionEntityNew` (`account_from_id`)")
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_account_to_id` ON `TransactionEntityNew` (`account_to_id`)")
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_transaction_type_code` ON `TransactionEntityNew` (`transaction_type_code`)")
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_category_id` ON `TransactionEntityNew` (`category_id`)")
//
//        // Copy from old to new
//        db.execSQL("INSERT INTO TransactionEntityNew(id, transaction_name, account_from_id, account_to_id, transaction_type_code, amount_in_cent, transaction_date, category_id) SELECT * FROM TransactionEntity")
//
//        // Drop old table
//        db.execSQL("DROP TABLE TransactionEntity")
//
//        // Rename new table
//        db.execSQL("ALTER TABLE TransactionEntityNew RENAME TO TransactionEntity")
//
//        // End
//        db.execSQL("COMMIT;");
//    }
//}
//
//// The migration here is to add initial amount for each account created.
//val MIGRATION_2_3 = object : Migration(2, 3) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("ALTER TABLE AccountEntity ADD initial_amount INTEGER NOT NULL DEFAULT 0")
//    }
//}
//
//val MIGRATION_3_4 = object : Migration(3, 4) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS `UserCurrencyRateEntity` (`name` TEXT PRIMARY KEY NOT NULL, `rate` REAL NOT NULL)")
//    }
//}
//
//val MIGRATION_4_5 = object : Migration(4, 5) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("ALTER TABLE TransactionEntity ADD currency TEXT NOT NULL DEFAULT 'MYR'")
//        db.execSQL("ALTER TABLE TransactionEntity ADD rate REAL NOT NULL DEFAULT 1")
//    }
//}
//
//val MIGRATION_5_6 = object : Migration(5, 6) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("BEGIN TRANSACTION;")
//
//        // Create New Transaction Table
//        db.execSQL("CREATE TABLE IF NOT EXISTS `TagEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tag_name` TEXT NOT NULL)")
//
//        // Copy from old to new
//        db.execSQL("INSERT INTO TagEntityNew(id, tag_name) SELECT * FROM TagEntity")
//
//        // Drop old table
//        db.execSQL("DROP TABLE TagEntity")
//
//        // Rename new table
//        db.execSQL("ALTER TABLE TagEntityNew RENAME TO TagEntity")
//
//        // End
//        db.execSQL("COMMIT;");
//    }
//}
//
//val MIGRATION_6_7 = object : Migration(6, 7) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("BEGIN TRANSACTION;")
//
//        // Create New Transaction Table
//        db.execSQL("CREATE TABLE IF NOT EXISTS `AccountEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `account_group_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `is_deleted` INTEGER NOT NULL DEFAULT false, `remarks` TEXT, FOREIGN KEY(`account_group_id`) REFERENCES `AccountGroupEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_AccountEntityNew_account_group_id` ON `AccountEntityNew` (`account_group_id`)")
//        // Copy from old to new
//        db.execSQL("INSERT INTO AccountEntityNew(id, account_group_id, name, is_deleted, remarks) SELECT id, account_group_id, name, is_deleted, remarks FROM AccountEntity")
//
//        // Drop old table
//        db.execSQL("DROP TABLE AccountEntity")
//
//        // Rename new table
//        db.execSQL("ALTER TABLE AccountEntityNew RENAME TO AccountEntity")
//
//        // End
//        db.execSQL("COMMIT;");
//    }
//}
//
//val MIGRATION_8_9 = object  : Migration(8, 9) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        // Add column schedulerId for TransactionEntity and create index for it
//        db.execSQL("BEGIN TRANSACTION;")
//
//        db.execSQL("CREATE TABLE IF NOT EXISTS `TransactionEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_name` TEXT NOT NULL, `account_from_id` INTEGER, `account_to_id` INTEGER, `transaction_type_code` TEXT NOT NULL, `amount_in_cent` INTEGER NOT NULL, `transaction_date` TEXT NOT NULL, `category_id` INTEGER, `currency` TEXT NOT NULL, `rate` REAL NOT NULL, `scheduler_id` INTEGER, FOREIGN KEY(`account_from_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`account_to_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transaction_type_code`) REFERENCES `TransactionTypeEntity`(`code`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `TransactionCategoryEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`scheduler_id`) REFERENCES `ScheduledTransactionEntity`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_account_from_id` ON `TransactionEntityNew` (`account_from_id`)");
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_account_to_id` ON `TransactionEntityNew` (`account_to_id`)");
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_transaction_type_code` ON `TransactionEntityNew` (`transaction_type_code`)");
//        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_category_id` ON `TransactionEntityNew` (`category_id`)");
//
//        // Copy from old to new
//        db.execSQL("INSERT INTO TransactionEntityNew(id, transaction_name, account_from_id, account_to_id, transaction_type_code, amount_in_cent, transaction_date, category_id, currency, rate) SELECT * FROM TransactionEntity")
//
//        // Drop old table
//        db.execSQL("DROP TABLE TransactionEntity")
//
//        // Rename new table
//        db.execSQL("ALTER TABLE TransactionEntityNew RENAME TO TransactionEntity")
//
//
//        // Add is_deleted column for ScheduledTransactionEntity table
//        db.execSQL("ALTER TABLE ScheduledTransactionEntity ADD is_deleted INTEGER NOT NULL DEFAULT false")
//
//        // End
//        db.execSQL("COMMIT;");
//    }
//}
//
//
//// To add auto increment in primary key of ScheduledTransactionEntity
//val MIGRATION_9_10 = object  : Migration(9, 10) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        db.execSQL("BEGIN TRANSACTION;")
//
//        db.execSQL("CREATE TABLE IF NOT EXISTS `ScheduledTransactionEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `scheduled_transaction_name` TEXT NOT NULL, `scheduled_account_from_id` INTEGER, `scheduled_account_to_id` INTEGER, `transaction_type_code` TEXT NOT NULL, `amount_in_cent` INTEGER NOT NULL, `transaction_date` TEXT NOT NULL, `category_id` INTEGER, `currency` TEXT NOT NULL, `rate` REAL NOT NULL, `scheduler_period` TEXT NOT NULL, `is_deleted` INTEGER NOT NULL)");
//
//        // Copy from old to new
//        db.execSQL("INSERT INTO ScheduledTransactionEntityNew(id, scheduled_transaction_name, scheduled_account_from_id, scheduled_account_to_id, transaction_type_code, amount_in_cent, transaction_date, category_id, currency, rate, scheduler_period, is_deleted) SELECT * FROM ScheduledTransactionEntity")
//
//        // Drop old table
//        db.execSQL("DROP TABLE ScheduledTransactionEntity")
//
//        // Rename new table
//        db.execSQL("ALTER TABLE ScheduledTransactionEntityNew RENAME TO ScheduledTransactionEntity")
//
//        // End
//        db.execSQL("COMMIT;");
//    }
//}