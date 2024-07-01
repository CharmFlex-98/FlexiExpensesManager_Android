package com.charmflex.flexiexpensesmanager.features.scheduler.ui.scheduler_editor

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_SCHEDULER_PERIOD
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import javax.inject.Inject

internal class ScheduledTransactionEditorContentProvider @Inject constructor() : TransactionEditorContentProvider() {

    override fun getContent(transactionType: TransactionType): List<FEField> {
        val list = mutableListOf(
            FEField(
                id = TRANSACTION_SCHEDULER_PERIOD,
                labelId = R.string.transaction_scheduler_period,
                hintId = R.string.transaction_scheduler_period_hint,
                type = FEField.FieldType.Callback
            )
        )
        val others = super.getContent(transactionType)
        list.addAll(others)
        return list
    }
}