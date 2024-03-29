package com.charmflex.flexiexpensesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeGhostButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGLargeSecondaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x3
import com.example.compose.FlexiExpensesManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlexiExpensesManagerTheme {
                // A surface container using the 'background' color from the theme
                SGScaffold(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Greeting("Android")
                    Spacer(modifier = Modifier.weight(1f))
                    SGLargePrimaryButton(modifier = Modifier.fillMaxWidth(), text = "Primary Button") {
                        
                    }
                    Spacer(modifier = Modifier.height(grid_x2))
                    SGLargeSecondaryButton(modifier = Modifier.fillMaxWidth(), text = "Secondary Button") {
                        
                    }
                    Spacer(modifier = Modifier.height(grid_x2))
                    SGLargeGhostButton(modifier = Modifier.fillMaxWidth(), text = "ghost") {
                        
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlexiExpensesManagerTheme {
        Greeting("Android")
    }
}