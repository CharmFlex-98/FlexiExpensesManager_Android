package com.charmflex.flexiexpensesmanager.features.transactions.provider

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import javax.inject.Inject

internal const val TRANSACTION_NAME = "TRANSACTION_NAME"
internal const val TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT"
internal const val TRANSACTION_DATE = "TRANSACTION_DATE"
internal const val TRANSACTION_FROM_ACCOUNT = "TRANSACTION_FROM_ACC"
internal const val TRANSACTION_TO_ACCOUNT = "TRANSACTION_TO_ACC"
internal const val TRANSACTION_CATEGORY = "TRANSACTION_CAT"
internal const val TRANSACTION_CURRENCY = "TRANSACTION_CURRENCY"



internal class NewTransactionContentProvider @Inject constructor() {
    fun getContent(transactionType: TransactionType): List<FEField> {
        val res = when (transactionType) {
            TransactionType.EXPENSES -> expensesFields()
            TransactionType.INCOME -> incomeFields()
            TransactionType.TRANSFER -> transferFields()
        }.toMutableList()
        res.addAll(
            listOf(
                FEField(
                    id = TRANSACTION_NAME,
                    labelId = R.string.new_expenses_name,
                    hintId = R.string.new_expenses_name_hint,
                    type = FEField.FieldType.Text
                ),
                FEField(
                    id = TRANSACTION_CURRENCY,
                    labelId = R.string.new_transaction_currency_label,
                    hintId = R.string.new_transaction_currency_hint,
                    type = FEField.FieldType.Callback
                ),
                FEField(
                    id = TRANSACTION_AMOUNT,
                    labelId = R.string.new_expenses_amount,
                    hintId = R.string.new_expenses_name_amount_hint,
                    type = FEField.FieldType.Number
                ),
                FEField(
                    id = TRANSACTION_DATE,
                    labelId = R.string.new_expenses_date,
                    hintId = R.string.new_expenses_date_hint,
                    type = FEField.FieldType.Callback
                )
            )
        )
        return res
    }

    private fun expensesFields(): List<FEField> {
        return listOf(
            FEField(
                id = TRANSACTION_FROM_ACCOUNT,
                labelId = R.string.new_transaction_account,
                hintId = R.string.new_expenses_account_hint,
                type = FEField.FieldType.Callback
            ),
            FEField(
                id = TRANSACTION_CATEGORY,
                labelId = R.string.generic_category,
                hintId = R.string.generic_category_hint,
                type = FEField.FieldType.Callback
            )
        )
    }

    private fun incomeFields(): List<FEField> {
        return listOf(
            FEField(
                id = TRANSACTION_TO_ACCOUNT,
                labelId = R.string.new_transaction_account,
                hintId = R.string.new_income_account_hint,
                type = FEField.FieldType.Callback
            ),
            FEField(
                id = TRANSACTION_CATEGORY,
                labelId = R.string.generic_category,
                hintId = R.string.generic_category_hint,
                type = FEField.FieldType.Callback
            )
        )
    }

    private fun transferFields(): List<FEField> {
        return listOf(
            FEField(
                id = TRANSACTION_FROM_ACCOUNT,
                labelId = R.string.new_transaction_from_account,
                hintId = R.string.new_transaction_from_account_hint,
                type = FEField.FieldType.Callback
            ),
            FEField(
                id = TRANSACTION_TO_ACCOUNT,
                labelId = R.string.new_transaction_to_account,
                hintId = R.string.new_transaction_to_account_hint,
                type = FEField.FieldType.Callback
            )
        )
    }
}