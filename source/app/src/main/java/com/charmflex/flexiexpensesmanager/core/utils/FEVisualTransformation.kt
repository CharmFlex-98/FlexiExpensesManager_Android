package com.charmflex.flexiexpensesmanager.core.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.text.isDigitsOnly
import javax.inject.Inject
import kotlin.math.max

internal class CurrencyVisualTransformation(
    private val currencyFormatter: CurrencyFormatter,
    private val outputFormatter: CurrencyTextFieldOutputFormatter,
    private val currencyCode: String,
) : VisualTransformation, OffsetMapping {

    private var originalText = ""
    private var formattedText = ""

    class Builder @Inject constructor(
        private val currencyFormatter: CurrencyFormatter,
        private val outputFormatter: CurrencyTextFieldOutputFormatter,
    ) {

        fun create(currencyCode: String): CurrencyVisualTransformation {
            return CurrencyVisualTransformation(
                currencyFormatter,
                outputFormatter,
                currencyCode
            )
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        originalText = text.text
        formattedText = getFormattedText(annotatedString = text)
        return TransformedText(AnnotatedString(formattedText), this)
    }

    private fun getFormattedText(annotatedString: AnnotatedString): String {
        val text = annotatedString.text
        val trimmedText = outputFormatter.format(text)
        if (trimmedText.isEmpty()) return trimmedText

        return try {
            currencyFormatter.format(trimmedText.toLong(), currencyCode)
        } catch (e: Exception) {
            text
        }
    }

    override fun originalToTransformed(offset: Int): Int {
        return formattedText.length
    }

    override fun transformedToOriginal(offset: Int): Int {
        return originalText.length
    }
}

internal class CurrencyTextFieldOutputFormatter @Inject constructor() {
    fun format(value: String): String {
        val trimmedText = value.trim()

        if (trimmedText.isEmpty()) return trimmedText
        if (trimmedText.isDigitsOnly().not()) return trimmedText.split(',', '.', '-', '_').first()

        return trimmedText
    }
}