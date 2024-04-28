package com.charmflex.flexiexpensesmanager.core.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal data class FEField(
    val id: String = "",
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
        data class SingleItemSelection(
            val options: List<Option>
        ) : Selection {
            interface Option {
                val id: String
                val title: String
                val subtitle: String?
                @get:DrawableRes
                val icon: Int?
            }
        }
    }
}