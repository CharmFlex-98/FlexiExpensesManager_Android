package com.charmflex.flexiexpensesmanager.core.domain

import androidx.annotation.DrawableRes

internal data class FEField(
    val id: String = "",
    val labelId: Int,
    val hintId: Int,
    val valueItem: Value = Value(),
    val type: FieldType,
    val allowClear: Boolean = false,
    val supportingText: String? = null
) {
    data class Value(
        val id: String = "",
        val value: String = ""
    )
    val isEnable: Boolean
        get() {
            return type !is FieldType.Selection
        }

    sealed interface FieldType {
        data object Text : FieldType

        data object Number : FieldType
        data object Currency : FieldType

        sealed interface Selection : FieldType
        data object Callback : Selection

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

