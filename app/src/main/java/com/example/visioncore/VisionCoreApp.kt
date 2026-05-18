package com.example.visioncore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun VisionCoreApp() {
    var currentScreen by remember { mutableStateOf(Screen.Onboarding) }

    val profiles = remember {
        mutableStateListOf(
            Profile(
                id = 1,
                name = "Person 1",
                createdAt = getCurrentDateText()
            )
        )
    }

    var activeProfileId by remember { mutableIntStateOf(1) }
    var selectedProfileId by remember { mutableIntStateOf(1) }
    var nextProfileId by remember { mutableIntStateOf(2) }

    fun deleteProfile(profileId: Int) {
        if (profiles.size > 1) {
            profiles.removeAll { profile -> profile.id == profileId }

            if (activeProfileId == profileId) {
                activeProfileId = profiles.first().id
            }

            if (selectedProfileId == profileId) {
                selectedProfileId = activeProfileId
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        when (currentScreen) {
            Screen.Onboarding -> {
                OnboardingScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSetUpClick = { currentScreen = Screen.Dashboard }
                )
            }

            Screen.Dashboard -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    DashboardScreen(
                        onManualOverrideClick = { currentScreen = Screen.ManualOverride },
                        onProfilesClick = { currentScreen = Screen.Profiles },
                        onPrescriptionClick = { currentScreen = Screen.Prescription },
                        onDevicePowerClick = { currentScreen = Screen.DevicePower },
                        onSettingsClick = { currentScreen = Screen.Settings },
                        onBluetoothClick = { currentScreen = Screen.Bluetooth }
                    )
                }
            }

            Screen.Settings -> {
                BackHandler { currentScreen = Screen.Dashboard }
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    SettingsScreen(onBackClick = { currentScreen = Screen.Dashboard })
                }
            }

            Screen.Bluetooth -> {
                BackHandler { currentScreen = Screen.Dashboard }
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    BluetoothScreen(onBackClick = { currentScreen = Screen.Dashboard })
                }
            }

            Screen.ManualOverride -> {
                BackHandler { currentScreen = Screen.Dashboard }
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    ManualOverrideScreen(onBackClick = { currentScreen = Screen.Dashboard })
                }
            }

            Screen.Profiles -> {
                BackHandler { currentScreen = Screen.Dashboard }
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    ProfilesScreen(
                        profiles = profiles,
                        activeProfileId = activeProfileId,
                        onProfileClick = { profile ->
                            activeProfileId = profile.id
                            selectedProfileId = profile.id
                            currentScreen = Screen.ProfileDetails
                        },
                        onAddProfileClick = { profileName ->
                            val finalProfileName = profileName.ifBlank { "Unnamed profile" }
                            val newProfile = Profile(
                                id = nextProfileId,
                                name = finalProfileName,
                                createdAt = getCurrentDateText()
                            )

                            profiles.add(newProfile)
                            activeProfileId = newProfile.id
                            selectedProfileId = newProfile.id
                            nextProfileId++
                        },
                        onDeleteActiveProfileClick = { deleteProfile(activeProfileId) },
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }

            Screen.ProfileDetails -> {
                BackHandler { currentScreen = Screen.Profiles }

                val selectedProfile = profiles.firstOrNull { profile ->
                    profile.id == selectedProfileId
                }

                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    if (selectedProfile != null) {
                        ProfileDetailsScreen(
                            profile = selectedProfile,
                            onDeleteProfileClick = {
                                deleteProfile(selectedProfile.id)
                                currentScreen = Screen.Profiles
                            },
                            onBackClick = { currentScreen = Screen.Profiles }
                        )
                    } else {
                        currentScreen = Screen.Profiles
                    }
                }
            }

            Screen.Prescription -> {
                BackHandler { currentScreen = Screen.Dashboard }
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    PrescriptionScreen(onBackClick = { currentScreen = Screen.Dashboard })
                }
            }

            Screen.DevicePower -> {
                BackHandler { currentScreen = Screen.Dashboard }
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    DevicePowerScreen(onBackClick = { currentScreen = Screen.Dashboard })
                }
            }
        }
    }
}
