package com.example.visioncore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.visioncore.ui.theme.VisionCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VisionCoreTheme {
                VisionCoreApp()
            }
        }
    }
}

enum class Screen {
    Home,
    Settings
}

@Composable
fun VisionCoreApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (currentScreen) {
            Screen.Home -> {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSettingsClick = {
                        currentScreen = Screen.Settings
                    }
                )
            }

            Screen.Settings -> {
                BackHandler {
                    currentScreen = Screen.Home
                }

                SettingsScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = {
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home")

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Settings")
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings")

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    VisionCoreTheme {
        HomeScreen(onSettingsClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    VisionCoreTheme {
        SettingsScreen(onBackClick = {})
    }
}