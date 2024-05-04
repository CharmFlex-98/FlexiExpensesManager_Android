package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun SettingScreen(viewModel: SettingViewModel) {
    FeColumnContainer {
        viewModel.getSettingActionable().forEachIndexed { index, it ->
            if (index != 0) HorizontalDivider(color = Color.White, thickness = 0.5.dp)
            Box(
                modifier = Modifier
                    .padding(vertical = grid_x2)
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onTap(it.action)
                    }
            ) {
                FEHeading4(text = it.title)
            }
        }
    }
}