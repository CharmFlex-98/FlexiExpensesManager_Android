package com.charmflex.flexiexpensesmanager.features.expenses.provider

import javax.inject.Inject

internal interface ExpensesTypeProvider {
    fun getTypes(): List<String>
}

internal class MockExpensesTypeProvider @Inject constructor(): ExpensesTypeProvider {
    override fun getTypes(): List<String> {
        return listOf(
            "Income",
            "Expenses",
            "Transfer"
        )
    }

}

internal class RemoteExpensesTypeProvider @Inject constructor(): ExpensesTypeProvider {
    override fun getTypes(): List<String> {
        return listOf()
    }

}