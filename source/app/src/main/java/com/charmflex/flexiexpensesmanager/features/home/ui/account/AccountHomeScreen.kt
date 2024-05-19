package com.charmflex.flexiexpensesmanager.features.home.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColor
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroup
import com.charmflex.flexiexpensesmanager.features.account.domain.model.AccountGroupSummary
import com.charmflex.flexiexpensesmanager.ui_common.FEBody1
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading5
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import kotlin.math.absoluteValue

@Composable
internal fun AccountHomeScreen(viewModel: AccountHomeViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(grid_x2)
    ) {
        viewState.accountsSummary.forEach {
            AccountGroupSection(it)
        }
    }
}

@Composable
private fun AccountGroupSection(
    accountGroupSummary: AccountHomeViewState.AccountGroupSummaryUI,
) {
    Column(
        modifier = Modifier
            .padding(vertical = grid_x2)
            .clip(RoundedCornerShape(grid_x2))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(grid_x2),
    ) {
        Row {
            FEHeading3(
                modifier = Modifier.weight(1f),
                text = accountGroupSummary.accountGroupName
            )
            FEHeading5(text = accountGroupSummary.balance, color = accountGroupSummary.textColor)

        }
        accountGroupSummary.accountsSummary.map {
            Row {
                FEBody1(
                    modifier = Modifier.weight(1f),
                    text = it.accountName
                )
                FEBody3(text = it.balance, color = it.textColor)
            }
        }
    }
}