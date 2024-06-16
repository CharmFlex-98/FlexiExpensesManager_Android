package com.charmflex.flexiexpensesmanager.features.home.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.MONTH_YEAR_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toStringWithPattern
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading5
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_5
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun AccountHomeScreen(viewModel: AccountHomeViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(grid_x2)
    ) {
        DateFilterBar(
            currentDateFilter = dateFilter,
            onDateFilterChanged = { viewModel.onDateFilterChanged(it) },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_x2)
        ) {
            Text(text = "Total Asset: ${viewState.totalAsset}")
        }
        viewState.accountsSummary.forEach {
            AccountGroupSection(it) { account ->
                viewModel.onAccountClick(account)
            }
        }
    }
}

@Composable
private fun AccountGroupSection(
    accountGroupSummary: AccountHomeViewState.AccountGroupSummaryUI,
    onAccountClick: (AccountHomeViewState.AccountGroupSummaryUI.AccountSummaryUI) -> Unit,
) {
    FeColumnContainer {
        Row(modifier = Modifier.padding(vertical = grid_x1)) {
            FEHeading3(
                modifier = Modifier.weight(1f),
                text = accountGroupSummary.accountGroupName
            )
            FEHeading5(
                text = accountGroupSummary.balance,
                color = if (accountGroupSummary.balanceInCent < 0) Color.Red else MaterialTheme.colorScheme.primary
            )

        }
        accountGroupSummary.accountsSummary.mapIndexed { index, it ->
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .clickable {
                        onAccountClick(it)
                    }
                    .padding(grid_x0_5)
            ) {
                FEBody1(
                    modifier = Modifier.weight(1f),
                    text = it.accountName
                )
                FEBody3(
                    text = it.balance,
                    color = if (accountGroupSummary.balanceInCent < 0) Color.Red else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}