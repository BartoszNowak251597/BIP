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
    PlaceholderScreen(
        modifier = modifier,
        title = "Settings",
        onBackClick = onBackClick
    )
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bluetooth")

        Button(
            onClick = {},
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Scan for devices")
        }

        Button(
            onClick = {},
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Connect")
        }

        Button(
            onClick = {},
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Disconnect")
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
    PlaceholderScreen(
        modifier = modifier,
        title = "Manual override",
        onBackClick = onBackClick
    )
}

@Composable
fun ProfilesScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    PlaceholderScreen(
        modifier = modifier,
        title = "Profiles",
        onBackClick = onBackClick
    )
}

@Composable
fun PrescriptionScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    PlaceholderScreen(
        modifier = modifier,
        title = "Prescription",
        onBackClick = onBackClick
    )
}

@Composable
fun DevicePowerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    PlaceholderScreen(
        modifier = modifier,
        title = "Device & power",
        onBackClick = onBackClick
    )
}

@Composable
fun PlaceholderScreen(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)

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