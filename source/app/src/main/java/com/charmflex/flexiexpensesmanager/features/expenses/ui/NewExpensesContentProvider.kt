package com.charmflex.flexiexpensesmanager.features.expenses.ui

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.utils.ResourceProvider
import javax.inject.Inject

internal class NewExpensesContentProvider @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun getContent(): List<NewExpensesField> {
        return listOf(
            NewExpensesField(
                key = "new_expenses_name",
                labelId = R.string.new_expenses_name,
                hintId = R.string.new_expenses_name_hint,
                type = NewExpensesField.FieldType.TEXT
            ),
            NewExpensesField(
                key = "new_expenses_amount",
                labelId = R.string.new_expenses_amount,
                hintId = R.string.new_expenses_name_amount_hint,
                type = NewExpensesField.FieldType.NUMBER
            )
        )
    }
}