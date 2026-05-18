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
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onManualOverrideClick: () -> Unit,
    onProfilesClick: () -> Unit,
    onPrescriptionClick: () -> Unit,
    onDevicePowerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBluetoothClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dashboard")

        Button(onClick = onManualOverrideClick) {
            Text(text = "Manual override")
        }

        Button(onClick = onProfilesClick) {
            Text(text = "Profiles")
        }

        Button(onClick = onPrescriptionClick) {
            Text(text = "Prescription")
        }

        Button(onClick = onDevicePowerClick) {
            Text(text = "Device & power")
        }

        Button(onClick = onSettingsClick) {
            Text(text = "Settings")
        }

        Button(onClick = onBluetoothClick) {
            Text(text = "Bluetooth")
        }
    }
}
