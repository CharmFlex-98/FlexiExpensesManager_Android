package com.charmflex.flexiexpensesmanager.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// The migration here basically just change the type of categoryId from not nullable to nullable.
// Because transfer type transaction does not support category.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("BEGIN TRANSACTION;")

        // Create New Transaction Table
        db.execSQL("CREATE TABLE IF NOT EXISTS `TransactionEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_name` TEXT NOT NULL, `account_from_id` INTEGER, `account_to_id` INTEGER, `transaction_type_code` TEXT NOT NULL, `amount_in_cent` INTEGER NOT NULL, `transaction_date` TEXT NOT NULL, `category_id` INTEGER, FOREIGN KEY(`account_from_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`account_to_id`) REFERENCES `AccountEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transaction_type_code`) REFERENCES `TransactionTypeEntity`(`code`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `TransactionCategoryEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_account_from_id` ON `TransactionEntityNew` (`account_from_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_account_to_id` ON `TransactionEntityNew` (`account_to_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_transaction_type_code` ON `TransactionEntityNew` (`transaction_type_code`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TransactionEntityNew_category_id` ON `TransactionEntityNew` (`category_id`)")

        // Copy from old to new
        db.execSQL("INSERT INTO TransactionEntityNew(id, transaction_name, account_from_id, account_to_id, transaction_type_code, amount_in_cent, transaction_date, category_id) SELECT * FROM TransactionEntity")

        // Drop old table
        db.execSQL("DROP TABLE TransactionEntity")

        // Rename new table
        db.execSQL("ALTER TABLE TransactionEntityNew RENAME TO TransactionEntity")

        // End
        db.execSQL("COMMIT;");
    }
}

// The migration here is to add initial amount for each account created.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE AccountEntity ADD initial_amount INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `UserCurrencyRateEntity` (`name` TEXT PRIMARY KEY NOT NULL, `rate` REAL NOT NULL)")
    }
}

val MIGRATION_4_5 = object  : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE TransactionEntity ADD currency TEXT NOT NULL DEFAULT 'MYR'")
        db.execSQL("ALTER TABLE TransactionEntity ADD rate REAL NOT NULL DEFAULT 1")
    }
}