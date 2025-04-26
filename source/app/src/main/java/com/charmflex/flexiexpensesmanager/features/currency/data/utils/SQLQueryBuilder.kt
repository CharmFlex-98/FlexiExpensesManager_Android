package com.charmflex.flexiexpensesmanager.features.currency.data.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import javax.inject.Inject

internal class SQLQueryBuilder {
    fun build(jsonObject: JsonObject): String {
        val stringBuilder = StringBuilder()
        val currencyArray = jsonObject.entries.toTypedArray()
        for (index in currencyArray.indices) {
            val (key, value) = currencyArray[index]
            stringBuilder.append("(")
            val currencyCode: String = key
            val metadata: JsonObject? = value as? JsonObject
            metadata?.let {
                val name = it["name"] as? JsonPrimitive
                val defaultFractionDigit = it["ISOdigits"] as? JsonPrimitive

                stringBuilder.append("\'${currencyCode}\'")
                stringBuilder.append(", ")
                stringBuilder.append("\'${name?.content ?: ""}\'")
                stringBuilder.append(", ")
                stringBuilder.append(defaultFractionDigit?.content?.toIntOrNull() ?: 0)
            }


            stringBuilder.append(")")

            // If last, we don't need to append comma
            if (index != currencyArray.size - 1) {
                stringBuilder.append(", ")
            }
        }

        return stringBuilder.toString()
    }
}