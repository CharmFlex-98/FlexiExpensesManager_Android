package com.charmflex.flexiexpensesmanager.features.home.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.ui_common.DateFilterBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FECallout3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading5
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.grid_x0_5
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun AccountHomeScreen(viewModel: AccountHomeViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val totalAsset = if (viewState.hideInfo) HIDE_INFO_LABEL else viewState.totalAsset

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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${stringResource(id = R.string.generix_total_asset)}: $totalAsset")
            IconButton(
                modifier = Modifier.padding(horizontal = grid_x1),
                onClick = { viewModel.toggleHideInfo() }) {
                if (viewState.hideInfo) SGIcons.VisibilityOff() else SGIcons.VisibilityOn()
            }
        }
        viewState.accountsSummary.forEach {
            AccountGroupSection(viewState.hideInfo, it) { account ->
                viewModel.onAccountClick(account)
            }
        }
    }
}

@Composable
private fun AccountGroupSection(
    hideInfo: Boolean,
    accountGroupSummary: AccountHomeViewState.AccountGroupSummaryUI,
    onAccountClick: (AccountHomeViewState.AccountGroupSummaryUI.AccountSummaryUI) -> Unit,
) {
    FeColumnContainer(
        modifier = Modifier.padding(vertical = grid_x2)
    ) {
        Row(modifier = Modifier.padding(vertical = grid_x1)) {
            FEHeading3(
                modifier = Modifier.weight(1f),
                text = accountGroupSummary.accountGroupName
            )
            FEHeading5(
                text = if (hideInfo) HIDE_INFO_LABEL else accountGroupSummary.balance,
                color = if (hideInfo) Color.Black else if (accountGroupSummary.balanceInCent < 0) Color.Red else MaterialTheme.colorScheme.primary
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

                Column {

                    FEBody3(
                        text = if (hideInfo) HIDE_INFO_LABEL else it.balance,
                        color = if (hideInfo) Color.Black else if (accountGroupSummary.balanceInCent < 0) Color.Red else MaterialTheme.colorScheme.primary
                    )

                    if (!it.isCurrencyPrimary) {
                        FECallout3(
                            text = if (hideInfo) HIDE_INFO_LABEL else it.mainCurrencyBalance,
                            color = if (hideInfo) Color.Black else if (accountGroupSummary.balanceInCent < 0) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private const val HIDE_INFO_LABEL = "*****"