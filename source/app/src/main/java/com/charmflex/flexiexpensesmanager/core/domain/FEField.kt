package com.charmflex.flexiexpensesmanager.core.domain

import androidx.annotation.StringRes

internal data class FEField(
    @StringRes
    val labelId: Int,
    @StringRes
    val hintId: Int,
    val value: String = "",
    val type: FieldType,
) {
    val isEnable: Boolean
        get() {
            return type !is FieldType.Selection
        }

    sealed interface FieldType {
        object Text : FieldType
        object Number : FieldType

        sealed interface Selection : FieldType
        object Date : Selection
        data class SingleSelection(
            val options: List<String>
        ) : Selection
    }
}