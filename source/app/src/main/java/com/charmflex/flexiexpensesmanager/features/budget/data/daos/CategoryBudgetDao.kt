package com.charmflex.flexiexpensesmanager.features.budget.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.charmflex.flexiexpensesmanager.features.budget.data.entities.CategoryBudgetEntity
import com.charmflex.flexiexpensesmanager.features.budget.data.responses.CategoryBudgetResponse
import com.charmflex.flexiexpensesmanager.features.budget.data.responses.MonthlyCategoryBudgetInfoResponse
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryBudgetDao {

    @Insert
    suspend fun addCategoryBudget(input: CategoryBudgetEntity): Long

    @Query(
        "SELECT cb.id, " +
                "tc.id as category_id, " +
                "tc.name as category_name, " +
                "cb.default_budget_in_cent FROM CategoryBudgetEntity cb " +
                "INNER JOIN TransactionCategoryEntity tc ON cb.category_id = tc.id"
    )
    fun getAllCategoryBudgets(): Flow<List<CategoryBudgetResponse>>

    @Update
    suspend fun updateCategoryBudget(input: CategoryBudgetEntity)

    @Query(
        "DELETE FROM CategoryBudgetEntity WHERE id = :id"
    )
    suspend fun deleteCategoryBudget(id: Int)

    @Query(
        "SELECT subtable.category_id," +
                "subtable.category_name," +
                "subtable.category_parent_id, " +
                "subtable.expenses_amount_in_cent, " +
                "subtable.category_budget_id," +
                "subtable.default_budget_in_cent, " +
                "mcb.budget_month_year," +
                "mcb.custom_budget_in_cent FROM " +
                "(SELECT tc.id as category_id, tc.name as category_name, tc.parent_id as category_parent_id, COALESCE(SUM(t.amount_in_cent * t.rate), 0) as expenses_amount_in_cent, cb.id as category_budget_id, cb.default_budget_in_cent " +
                "FROM TransactionCategoryEntity tc " +
                "LEFT JOIN CategoryBudgetEntity cb ON cb.category_id = tc.id " +
                "LEFT JOIN (SELECT * FROM TransactionEntity WHERE transaction_type_code = 'EXPENSES' " +
                " AND (:startDate IS NULL OR date(transaction_date) >= date(:startDate))" +
                " AND (:endDate IS NULL OR date(transaction_date) <= date(:endDate))) t ON t.category_id = tc.id GROUP BY tc.id) subtable " +
                "LEFT JOIN MonthlyCategoryBudgetEntity mcb ON subtable.category_budget_id = mcb.category_budget_id"
    )
    fun getMonthlyCategoryBudget(
        startDate: String,
        endDate: String
    ): Flow<List<MonthlyCategoryBudgetInfoResponse>>
}