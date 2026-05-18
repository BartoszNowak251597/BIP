package com.example.visioncore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings")

        PlaceholderButton(text = "Stay in last mode")
        PlaceholderButton(text = "Lock to NEAR")
        PlaceholderButton(text = "Lock to FAR")
        PlaceholderButton(text = "Go neutral 0 D")
        PlaceholderButton(text = "Blink LED below 15%")

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}
