package com.charmflex.flexiexpensesmanager.features.budget.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.budget.domain.models.AdjustedCategoryBudgetNode
import com.charmflex.flexiexpensesmanager.features.budget.domain.repositories.CategoryBudgetRepository
import com.charmflex.flexiexpensesmanager.features.budget.domain.usecases.GetAdjustedCategoryBudgetInfoUseCase
import com.charmflex.flexiexpensesmanager.features.budget.ui.stats.BudgetStatViewState
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.ui_common.features.SETTING_EDITOR_BUDGET_AMOUNT
import com.charmflex.flexiexpensesmanager.ui_common.features.SETTING_EDITOR_BUDGET_CATEGORY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

internal class BudgetSettingViewModel @Inject constructor(
    private val budgetRepository: CategoryBudgetRepository,
    private val transactionCategoryRepository: TransactionCategoryRepository,
    private val routeNavigator: RouteNavigator,
    private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
    private val userCurrencyRepository: UserCurrencyRepository,
    private val categoryBudgetRepository: CategoryBudgetRepository,
    private val currencyFormatter: CurrencyFormatter
) : ViewModel() {
    private val _viewState = MutableStateFlow(BudgetSettingViewState())
    val viewState = _viewState.asStateFlow()

    init {
        observeTransactionCategories()
        initPrimaryCurrency()
        observeBudgetList()
    }

    private fun initPrimaryCurrency() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    currencyCode = userCurrencyRepository.getPrimaryCurrency()
                )
            }
        }
    }

    private fun observeBudgetList() {
        viewModelScope.launch {
            categoryBudgetRepository.getAllCategoryBudgets().collectLatest { res ->
                _viewState.update {
                    it.copy(
                        budgetListItem = res.map {
                            BudgetSettingViewState.BudgetListItem(
                                it.id,
                                it.categoryName,
                                currencyFormatter.format(it.defaultBudgetInCent, userCurrencyRepository.getPrimaryCurrency())
                            )
                        }
                    )
                }
            }
        }
    }

    private fun observeTransactionCategories() {
        viewModelScope.launch {
            transactionCategoryRepository.getCategories(TransactionType.EXPENSES.name).collectLatest { res ->
                _viewState.update {
                    it.copy(
                        expensesCategories = res
                    )
                }
            }
        }
    }

    fun onDeleteBudget(budgetItem: BudgetSettingViewState.BudgetListItem) {
        viewModelScope.launch {
            categoryBudgetRepository.deleteCategoryBudget(budgetItem.id)
        }
    }

    fun toggleEditor(showEditor: Boolean) {
        _viewState.update {
            it.copy(
                isEditorOn = showEditor
            )
        }
    }

    fun onFieldValueChanged(feField: FEField, newValue: String, id: String? = null) {
        _viewState.update {
            it.copy(
                fields = it.fields.map {
                    if (it == feField) it.copy(valueItem = FEField.Value(value = newValue, id = id ?: ""))
                    else it
                }
            )
        }
    }

    fun currencyVisualTransformationBuilder(): CurrencyVisualTransformation.Builder {
        return currencyVisualTransformationBuilder
    }

    fun onCallBackAction(feField: FEField) {
        when (feField.id) {
            SETTING_EDITOR_BUDGET_CATEGORY -> {
                toggleBottomSheet(BudgetSettingViewState.BudgetSettingBottomSheetState.CategoryOption(feField))
            }
        }
    }

    fun toggleBottomSheet(bottomSheetState: BudgetSettingViewState.BudgetSettingBottomSheetState?) {
        _viewState.update {
            it.copy(
                bottomSheetState = bottomSheetState
            )
        }
    }

    fun submitBudget() {
        val categoryId = getCategoryField()?.valueItem?.id?.toInt()
        val amount = getAmountField()?.valueItem?.value?.toLong()

        if (categoryId != null && amount != null) {
            viewModelScope.launch {
                resultOf {
                    budgetRepository.addCategoryBudget(categoryId, amount)
                }.fold(
                    onSuccess = {
                        onBack()
                    },
                    onFailure = {}
                )
            }
        }
    }

    fun onBack() {
        if (viewState.value.isEditorOn) toggleEditor(false)
        else routeNavigator.pop()
    }

    private fun getCategoryField(): FEField? {
        return getField(SETTING_EDITOR_BUDGET_CATEGORY)
    }

    private fun getAmountField(): FEField? {
        return getField(SETTING_EDITOR_BUDGET_AMOUNT)
    }

    private fun getField(id: String): FEField? {
        return _viewState.value.fields.firstOrNull { it.id == id }
    }
}

internal data class BudgetSettingViewState(
    val fields: List<FEField> = listOf(
        FEField(
            id = SETTING_EDITOR_BUDGET_CATEGORY,
            labelId = R.string.setting_editor_budget_category_label,
            hintId = R.string.setting_editor_budget_category_hint,
            type = FEField.FieldType.Callback,
        ),
        FEField(
            id = SETTING_EDITOR_BUDGET_AMOUNT,
            labelId = R.string.setting_editor_budget_amount_label,
            hintId = R.string.setting_editor_budget_amount_hint,
            type = FEField.FieldType.Currency,
        )
    ),
    val bottomSheetState: BudgetSettingBottomSheetState? = null,
    val expensesCategories: TransactionCategories? = null,
    val currencyCode: String = "USD",
    val budgetListItem: List<BudgetListItem> = emptyList(),
    val isEditorOn: Boolean = false
) {
    sealed interface BudgetSettingBottomSheetState {
        val feField: FEField
        data class CategoryOption(
            override val feField: FEField
        ) : BudgetSettingBottomSheetState
    }

    data class BudgetListItem(
        val id: Int,
        val categoryName: String,
        val budget: String
    )
}