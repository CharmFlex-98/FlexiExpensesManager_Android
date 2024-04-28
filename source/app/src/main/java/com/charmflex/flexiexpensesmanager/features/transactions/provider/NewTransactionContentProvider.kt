package com.charmflex.flexiexpensesmanager.features.transactions.provider

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.features.account.domain.model.Account
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_expenses.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.GetAvailableAccountsUseCase
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.GetAvailableCategoriesUseCase
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.GetTransactionTypeUseCase
import javax.inject.Inject

internal class NewTransactionContentProvider @Inject constructor(
    private val getAvailableAccountsUseCase: GetAvailableAccountsUseCase,
    private val getAvailableCategoriesUseCase: GetAvailableCategoriesUseCase

) {
    suspend fun getContent(transactionType: TransactionType): List<FEField> {
        val res = when (transactionType) {
            TransactionType.EXPENSES -> expensesFields()
            TransactionType.INCOME -> incomeFields()
            TransactionType.TRANSFER -> transferFields()
        }.toMutableList()
        res.addAll(
            listOf(
                FEField(
                    labelId = R.string.new_expenses_name,
                    hintId = R.string.new_expenses_name_hint,
                    type = FEField.FieldType.Text
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
        )
        return res
    }

    private suspend fun expensesFields(): List<FEField> {
        return listOf(
            FEField(
                labelId = R.string.new_transaction_account,
                hintId = R.string.new_expenses_account_hint,
                type = FEField.FieldType.SingleItemSelection(
                    options = getAvailableAccountsUseCase().map {
                        AccountSelectionItem(
                            id = it.accountId.toString(),
                            title = it.accountName
                        )
                    }
                )
            ),
            FEField(
                labelId = R.string.generic_category,
                hintId = R.string.generic_category_hint,
                type = FEField.FieldType.SingleItemSelection (
                    options = getAvailableCategoriesUseCase("EXPENSES").items.map {
                        CategorySelectionItem(
                            id = it.categoryId.toString(),
                            title = it.categoryName
                        )
                    }
                )
            )
        )
    }

    private suspend fun incomeFields(): List<FEField> {
        return listOf(
            FEField(
                labelId = R.string.new_transaction_account,
                hintId = R.string.new_income_account_hint,
                type = FEField.FieldType.SingleItemSelection(
                    options = getAvailableAccountsUseCase().map {
                        AccountSelectionItem(
                            id = it.accountId.toString(),
                            title = it.accountName
                        )
                    }
                )
            ),
            FEField(
                labelId = R.string.generic_category,
                hintId = R.string.generic_category_hint,
                type = FEField.FieldType.SingleItemSelection(
                    options = getAvailableCategoriesUseCase("INCOME").items.map {
                        CategorySelectionItem(
                            id = it.categoryId.toString(),
                            title = it.categoryName
                        )
                    }
                )
            )
        )
    }

    private suspend fun transferFields(): List<FEField> {
        return listOf(
            FEField(
                labelId = R.string.new_transaction_from_account,
                hintId = R.string.new_transaction_from_account_hint,
                type = FEField.FieldType.SingleItemSelection(
                    options = getAvailableAccountsUseCase().map {
                        AccountSelectionItem(
                            id = it.accountId.toString(),
                            title = it.accountName
                        )
                    }
                )
            ),
            FEField(
                labelId = R.string.new_transaction_to_account,
                hintId = R.string.new_transaction_to_account_hint,
                type = FEField.FieldType.SingleItemSelection(
                    options = getAvailableAccountsUseCase().map {
                        AccountSelectionItem(
                            id = it.accountId.toString(),
                            title = it.accountName
                        )
                    }
                )
            )
        )
    }
}

internal data class CategorySelectionItem(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    override val icon: Int? = null

) : FEField.FieldType.SingleItemSelection.Option

internal data class AccountSelectionItem(
    override val id: String = "",
    override val title: String,
    override val subtitle: String? = "",
    override val icon: Int? = null

) : FEField.FieldType.SingleItemSelection.Option