package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyTextFieldOutputFormatter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

enum class SupportingTextType {
    ERROR, INFO
}

data class SupportingText(
    val text: String,
    val supportingTextType: SupportingTextType
)

@Composable
fun SGTextField(
    modifier: Modifier,
    label: String,
    hint: String? = null,
    value: String,
    supportingText: SupportingText? = null,
    supportingTextOnClicked: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    enable: Boolean = true,
    singleLine: Boolean = true,
    maxLength: Int? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClicked: (() -> Unit)? = null,
    outputFormatter: ((String) -> String)? = null,
    suffixText: String? = null,
    onSuffixTextClicked: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    val onClick by rememberUpdatedState(newValue = onClicked)
    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Release -> if (readOnly) onClick?.let { it() }
                    else -> {}
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }
    val spText = when {
        supportingText != null -> supportingText.text
        maxLength != null -> "${value.length}/$maxLength"
        else -> null
    }
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { res ->
            val updatedValue = outputFormatter?.let {
                it(res)
            } ?: res

            if (maxLength == null) onValueChange(updatedValue)
            else if (updatedValue.length <= maxLength) onValueChange(updatedValue)
        },
        label = { Text(text = label) },
        placeholder = hint?.let { { Text(text = hint) } },
        supportingText = spText?.let {
            { Text(modifier = Modifier.clickable { supportingTextOnClicked?.invoke() }, text = it) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        readOnly = readOnly,
        enabled = enable,
        isError = supportingText?.supportingTextType == SupportingTextType.ERROR,
        interactionSource = interactionSource,
        singleLine = singleLine,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        suffix = suffixText?.let {
            {
                Text(
                    modifier = Modifier.clickable { onSuffixTextClicked?.invoke() },
                    text = it
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGAutoCompleteTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    hint: String? = null,
    suggestions: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        SGTextField(
            modifier = modifier.menuAnchor(),
            label = label,
            hint = hint,
            value = value,
            supportingText = null,
            readOnly = true,
            enable = true,
            onClicked = null,
            onValueChange = {}
        )
        ExposedDropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            suggestions.forEach {
                DropdownMenuItem(
                    modifier = modifier,
                    text = { Text(modifier = modifier, text = it) },
                    onClick = {
                        onItemSelected(it)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}