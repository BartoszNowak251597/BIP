package com.example.visioncore

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.visioncore.ui.theme.VisionCoreTheme

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    VisionCoreTheme {
        OnboardingScreen(onSetUpClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    VisionCoreTheme {
        AppBackground {
            DashboardScreen(
                bluetoothCompleted = false,
                profileCompleted = false,
                prescriptionCompleted = false,
                calibrationCompleted = false,
                settingsCompleted = false,
                onManualOverrideClick = {},
                onProfilesClick = {},
                onPrescriptionClick = {},
                onDevicePowerClick = {},
                onSettingsClick = {},
                onBluetoothClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilesScreenPreview() {
    VisionCoreTheme {
        AppBackground {
            ProfilesScreen(
                profiles = listOf(
                    Profile(
                        id = 1,
                        name = "Person 1",
                        createdAt = "01.05.2026 14:00"
                    ),
                    Profile(
                        id = 2,
                        name = "Person 2",
                        createdAt = "01.05.2026 14:05"
                    )
                ),
                activeProfileId = 1,
                onProfileClick = {},
                onAddProfileClick = {},
                onDeleteActiveProfileClick = {},
                onBackClick = {},
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileDetailsScreenPreview() {
    VisionCoreTheme {
        AppBackground {
            ProfileDetailsScreen(
                profile = Profile(
                    id = 1,
                    name = "Person 1",
                    createdAt = "01.05.2026 14:00"
                ),
                onDeleteProfileClick = {},
                onBackClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothScreenPreview() {
    VisionCoreTheme {
        AppBackground {
            BluetoothScreen(
                onBackClick = {},
                onConnected = {},
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    VisionCoreTheme {
        AppBackground {
            SettingsScreen(
                onBackClick = {},
                onContinueClick = {}
            )
        }
    }
}