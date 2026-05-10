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
import androidx.compose.runtime.mutableStateListOf
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
    Dashboard,
    Settings,
    Bluetooth,
    ManualOverride,
    Profiles,
    Prescription,
    DevicePower
}

@Composable
fun VisionCoreApp() {
    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

    val profiles = remember { mutableStateListOf("Person 1") }
    var activeProfile by remember { mutableStateOf("Person 1") }
    var nextProfileNumber by remember { mutableStateOf(2) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (currentScreen) {
            Screen.Dashboard -> {
                DashboardScreen(
                    modifier = Modifier.padding(innerPadding),
                    onManualOverrideClick = {
                        currentScreen = Screen.ManualOverride
                    },
                    onProfilesClick = {
                        currentScreen = Screen.Profiles
                    },
                    onPrescriptionClick = {
                        currentScreen = Screen.Prescription
                    },
                    onDevicePowerClick = {
                        currentScreen = Screen.DevicePower
                    },
                    onSettingsClick = {
                        currentScreen = Screen.Settings
                    },
                    onBluetoothClick = {
                        currentScreen = Screen.Bluetooth
                    }
                )
            }

            Screen.Settings -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                SettingsScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }

            Screen.Bluetooth -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                BluetoothScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }

            Screen.ManualOverride -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                ManualOverrideScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }

            Screen.Profiles -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                ProfilesScreen(
                    modifier = Modifier.padding(innerPadding),
                    profiles = profiles,
                    activeProfile = activeProfile,
                    onProfileClick = { profileName ->
                        activeProfile = profileName
                    },
                    onAddProfileClick = {
                        val newProfileName = "Person $nextProfileNumber"
                        nextProfileNumber++
                        profiles.add(newProfileName)
                        activeProfile = newProfileName
                    },
                    onDeleteActiveProfileClick = {
                        if (profiles.size > 1) {
                            profiles.remove(activeProfile)
                            activeProfile = profiles.first()
                        }
                    },
                    onBackClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }

            Screen.Prescription -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                PrescriptionScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }

            Screen.DevicePower -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                DevicePowerScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }
        }
    }
}

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

        Button(onClick = {}) {
            Text(text = "Stay in last mode")
        }

        Button(onClick = {}) {
            Text(text = "Lock to NEAR")
        }

        Button(onClick = {}) {
            Text(text = "Lock to FAR")
        }

        Button(onClick = {}) {
            Text(text = "Go neutral 0 D")
        }

        Button(onClick = {}) {
            Text(text = "Blink LED below 15%")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun BluetoothScreen(
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
        Text(text = "Bluetooth")

        Button(onClick = {}) {
            Text(text = "Scan for devices")
        }

        Button(onClick = {}) {
            Text(text = "Connect")
        }

        Button(onClick = {}) {
            Text(text = "Disconnect")
        }

        Button(onClick = {}) {
            Text(text = "Send test command")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun ManualOverrideScreen(
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
        Text(text = "Manual override")

        Button(onClick = {}) {
            Text(text = "Hold near")
        }

        Button(onClick = {}) {
            Text(text = "Hold far")
        }

        Button(onClick = {}) {
            Text(text = "Resume auto")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun ProfilesScreen(
    modifier: Modifier = Modifier,
    profiles: List<String>,
    activeProfile: String,
    onProfileClick: (String) -> Unit,
    onAddProfileClick: () -> Unit,
    onDeleteActiveProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profiles")

        profiles.forEach { profileName ->
            Button(
                onClick = {
                    onProfileClick(profileName)
                }
            ) {
                if (profileName == activeProfile) {
                    Text(text = "$profileName · ACTIVE")
                } else {
                    Text(text = profileName)
                }
            }
        }

        Button(onClick = onAddProfileClick) {
            Text(text = "+ New")
        }

        Button(onClick = onDeleteActiveProfileClick) {
            Text(text = "Delete profile")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun PrescriptionScreen(
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
        Text(text = "Prescription")

        Button(onClick = {}) {
            Text(text = "Same value for both eyes")
        }

        Button(onClick = {}) {
            Text(text = "Scan prescription card")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun DevicePowerScreen(
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
        Text(text = "Device & power")

        Button(onClick = {}) {
            Text(text = "Recalibrate head")
        }

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
fun DashboardScreenPreview() {
    VisionCoreTheme {
        DashboardScreen(
            onManualOverrideClick = {},
            onProfilesClick = {},
            onPrescriptionClick = {},
            onDevicePowerClick = {},
            onSettingsClick = {},
            onBluetoothClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilesScreenPreview() {
    VisionCoreTheme {
        ProfilesScreen(
            profiles = listOf("Person 1", "Person 2"),
            activeProfile = "Person 1",
            onProfileClick = {},
            onAddProfileClick = {},
            onDeleteActiveProfileClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothScreenPreview() {
    VisionCoreTheme {
        BluetoothScreen(onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    VisionCoreTheme {
        SettingsScreen(onBackClick = {})
    }
}