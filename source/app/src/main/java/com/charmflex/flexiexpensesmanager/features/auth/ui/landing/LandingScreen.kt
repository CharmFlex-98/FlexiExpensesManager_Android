package com.charmflex.flexiexpensesmanager.features.auth.ui.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.ui_common.Money3DAnimation
import com.charmflex.flexiexpensesmanager.ui_common.SGButtonGroupVertical
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x30

@Composable
internal fun LandingScreen(
    landingScreenViewModel: LandingScreenViewModel
) {
    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(grid_x2),
            contentAlignment = Alignment.Center
        ) {
            Money3DAnimation(modifier = Modifier.size(grid_x30))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            contentAlignment = Alignment.Center,

            ) {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                text = "Welcome to Your ONLY Money Manager & Start Managing Your Expenses Like a PRO!")
        }

        SGButtonGroupVertical {
            SGLargePrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Login with Google"
            ) {

            }
            SGLargeSecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Login as Guest"
            ) {
                landingScreenViewModel.onGuestLogin()
            }
        }
    }
}