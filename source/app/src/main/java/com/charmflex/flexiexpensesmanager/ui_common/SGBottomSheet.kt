package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.theme.FlexiExpensesManagerTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGAlertBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    actionButtonLayout: @Composable (() -> Unit)?
) {
    SGModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismiss = onDismiss,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(grid_x3))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subtitle,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(grid_x3))
            if (actionButtonLayout != null) actionButtonLayout()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericErrorBottomSheet(
    bottomSheetState: SheetState,
    onPrimaryButtonClick: () -> Unit
) {
    SGAlertBottomSheet(
        sheetState = bottomSheetState,
        title = stringResource(R.string.generic_error_bottomsheet_title),
        subtitle = stringResource(id = R.string.generic_error_bottomsheet_subtitle),
        onDismiss = { }) {
        SGLargePrimaryButton(text = stringResource(R.string.generic_ok)) {
            onPrimaryButtonClick()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview3() {
    FlexiExpensesManagerTheme {
        ModalBottomSheet(onDismissRequest = { /*TODO*/ }, contentColor = Color.Red) {
            Column(
            ) {
                Text(text = "test", fontSize = 20.sp)
                Box(modifier = Modifier.weight(1f)) {
                    Text(text = "Test", fontSize = 16.sp)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = containerColor
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(grid_x2), contentAlignment = Alignment.Center
        ) {
            content()
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

internal interface SearchItem {
    val key: String
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
internal fun SearchBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    searchFieldLabel: String,
    items: List<SearchItem>,
    errorText: String? = null,
    itemLayout: @Composable (index: Int, item: SearchItem) -> Unit,
) {
    SGModalBottomSheet(modifier = modifier, onDismiss = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxHeight(0.5f)
        ) {
            var searchKey by remember { mutableStateOf("") }
            var filteredItems by remember {
                mutableStateOf(items)
            }
            // 🔁 Debounce the search key input
            LaunchedEffect(Unit) {
                snapshotFlow { searchKey }
                    .debounce(300) // Wait 100ms before applying search
                    .distinctUntilChanged()
                    .collectLatest { query ->
                        filteredItems = items.filter {
                            it.key.contains(query, ignoreCase = true)
                        }
                    }
            }

            SGTextField(
                modifier = Modifier.fillMaxWidth(), label = searchFieldLabel, hint = "search",
                value = searchKey, supportingText = errorText?.let { SupportingText(text = it, supportingTextType = SupportingTextType.ERROR) }, onValueChange = {
                    searchKey = it
                }
            )
            Spacer(modifier = Modifier.height(grid_x1))
            Box(modifier = Modifier.wrapContentSize()) {
                ListTable(items = filteredItems) { index, item ->
                    itemLayout(index, item)
                }
            }
        }

    }
}

@Composable
@Preview
fun PreviewSearch() {
    val items = listOf("1", "2")

    SGScaffold {
        Column {
            SGTextField(
                modifier = Modifier.fillMaxWidth(), label = "search", hint = "search",
                value = "Hello", supportingText = SupportingText("", SupportingTextType.ERROR), onValueChange = {}
            )
            ListTable(items = items) { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }
                        .padding(grid_x1),
                    shape = RectangleShape
                ) {
                    Box(
                        modifier = Modifier.padding(grid_x2)
                    ) {
                        Text(text = item, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    }
                }
            }
        }
    }

}