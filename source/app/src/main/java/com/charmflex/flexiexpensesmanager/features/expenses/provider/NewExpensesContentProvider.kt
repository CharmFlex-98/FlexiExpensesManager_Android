package com.charmflex.flexiexpensesmanager.features.expenses.provider

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

internal class NewExpensesContentProvider @Inject constructor(
    // TODO: Change this to provide by remote later
    private val expensesTypeProvider: ExpensesTypeProvider
) {
    fun getContent(): List<FEField> {
        return listOf(
            FEField(
                labelId = R.string.new_expenses_name,
                hintId = R.string.new_expenses_name_hint,
                type = FEField.FieldType.Text
            ),
            FEField(
                labelId = R.string.new_expenses_type,
                hintId = R.string.new_expenses_type_hint,
                type = FEField.FieldType.SingleSelection(
                    options = expensesTypeProvider.getTypes()
                )
            ),
            FEField(
                labelId = R.string.new_expenses_amount,
                hintId = R.string.new_expenses_name_amount_hint,
                type = FEField.FieldType.Number
            ),
            FEField(
                labelId = R.string.new_expenses_date,
                hintId = R.string.new_expenses_date_hint,
                type = FEField.FieldType.Date
            )
        )
    }
}