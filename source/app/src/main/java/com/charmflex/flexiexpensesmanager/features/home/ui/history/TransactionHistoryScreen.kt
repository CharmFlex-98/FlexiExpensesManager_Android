package com.charmflex.flexiexpensesmanager.features.home.ui.history

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FECallout3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.ListTable
import com.charmflex.flexiexpensesmanager.ui_common.SGAnimatedTransition
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_5
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x8
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionHistoryScreen(
    transactionHistoryViewModel: TransactionHistoryViewModel
) {
    val viewState by transactionHistoryViewModel.viewState.collectAsState()
    val scrollItems = viewState.items
    val scrollState = rememberLazyListState()
    val tabScrollState = rememberScrollState()
    val tabState by transactionHistoryViewModel.tabState.collectAsState()
    val tabHeight = with(LocalDensity.current) { grid_x8.roundToPx().toFloat() }
    val showMonthTab by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 2 }
    }
    val firstVisibleItemIndex by remember {
        derivedStateOf {
            scrollState.layoutInfo.visibleItemsInfo.lastOrNull {
                it.offset < tabHeight
            }?.index ?: 0
        }
    }
    val isScrollingUp = scrollState.isScrollingUp()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(firstVisibleItemIndex, isScrollingUp) {
        transactionHistoryViewModel.onReachHistoryItem(scrollItems.getOrNull(firstVisibleItemIndex))
    }

    ListTable(
        modifier = Modifier.fillMaxSize().padding(grid_x2),
        items = scrollItems,
        scrollState = scrollState
    ) { index, item ->
        when (item) {
            is TransactionHistoryHeader -> {
                Spacer(modifier = Modifier.height(grid_x2))
                ExpensesHistoryDateHeaderView(month = item.title, date = item.subtitle)
            }

            is TransactionHistorySection -> ExpensesHistorySectionView(items = item.items)
        }
    }

    if (showMonthTab) {
        SGAnimatedTransition(
            enter = slideInVertically(initialOffsetY = { -it }) +
                    expandVertically(expandFrom = Alignment.Top),
            exit = slideOutVertically(targetOffsetY = { -it }) +
                    shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            SecondaryScrollableTabRow(
                selectedTabIndex = tabState.selectedTabIndex,
                scrollState = tabScrollState,
            ) {
                tabState.tabs.forEachIndexed { index, item ->
                    Tab(
                        modifier = Modifier
                            .height(grid_x8)
                            .padding(grid_x1),
                        selected = index == tabState.selectedTabIndex,
                        onClick = {
                            coroutineScope.launch {
                                val scrollToIndex = transactionHistoryViewModel.findFirstItemIndexByTab(item)
                                scrollState.scrollToItem(scrollToIndex)
                                scrollState.scrollBy(-tabHeight)
                            }
                        }
                    ) {
                        FEBody3(text = item.year)
                        FEBody2(text = item.month)
                    }
                }
            }
        }
    }


    if (viewState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ExpensesHistoryDateHeaderView(
    month: String? = null,
    date: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            month?.let { FEBody3(text = it) }
            FECallout3(text = date)
        }
    }
}

@Composable
private fun ExpensesHistorySectionView(
    items: List<TransactionHistorySection.SectionItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(grid_x2))
            .padding(vertical = grid_x1)
    ) {
        items.forEachIndexed { index, it ->
            ExpensesHistoryItem(
                name = it.name,
                amount = it.amount,
                category = it.category,
                type = it.type
            )
            if (index != items.size - 1) HorizontalDivider(color = Color.Gray, thickness = 0.5.dp)
        }
    }
}

@Composable
private fun ExpensesHistoryItem(
    name: String,
    amount: String,
    category: String,
    type: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.tertiaryContainer
            )
            .padding(grid_x1)
    ) {
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            FEBody1(text = category)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = grid_x0_5),
            contentAlignment = Alignment.Center
        ) {
            FEHeading4(text = name)
        }
        Box(
            contentAlignment = Alignment.Center
        ) {
            FEBody1(text = amount)
        }
    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}