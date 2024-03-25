package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun SGTextField(
    modifier: Modifier,
    label: String,
    hint: String? = null,
    value: String,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    enable: Boolean = true,
    singleLine: Boolean = true,
    maxLength: Int? = null,
    onClicked: (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Release -> if (readOnly && onClicked != null) onClicked()
                    else -> {}
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }
    val supportingText =  when {
        errorText != null -> errorText
        maxLength != null -> "${value.length}/$maxLength"
        else -> null
    }
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            if (maxLength == null) onValueChange(it)
            else if (it.length <= maxLength) onValueChange(it)
        },
        label = { Text(text = label) },
        placeholder = hint?.let { {Text(text = hint)} },
        supportingText = supportingText?.let {
            { Text(text = supportingText) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        readOnly = readOnly,
        enabled = enable,
        isError = errorText != null,
        interactionSource = interactionSource,
        singleLine = singleLine
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
            errorText = null,
            readOnly = true,
            enable = true,
            onClicked = null,
            onValueChange = {}
        )
        ExposedDropdownMenu(modifier = modifier, expanded = expanded, onDismissRequest = { expanded = false }) {
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