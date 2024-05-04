package com.charmflex.flexiexpensesmanager.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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