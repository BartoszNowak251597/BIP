package com.example.visioncore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun VisionCoreApp() {
    var currentScreen by rememberSaveable { mutableStateOf(Screen.Onboarding) }

    val profiles = remember {
        mutableStateListOf(
            Profile(
                id = 1,
                name = "Person 1",
                createdAt = getCurrentDateText()
            )
        )
    }

    var activeProfileId by rememberSaveable { mutableIntStateOf(1) }
    var selectedProfileId by rememberSaveable { mutableIntStateOf(1) }
    var nextProfileId by rememberSaveable { mutableIntStateOf(2) }

    var bluetoothCompleted by rememberSaveable { mutableStateOf(false) }
    var profileCompleted by rememberSaveable { mutableStateOf(false) }
    var prescriptionCompleted by rememberSaveable { mutableStateOf(false) }
    var calibrationCompleted by rememberSaveable { mutableStateOf(false) }
    var settingsCompleted by rememberSaveable { mutableStateOf(false) }

    var setupConfig by remember { mutableStateOf(SetupConfig()) }
    var devicePowerReturnScreen by rememberSaveable { mutableStateOf(Screen.Dashboard) }
    var prescriptionReturnScreen by rememberSaveable { mutableStateOf(Screen.Dashboard) }

    fun saveSetupConfig() {
        val activeProfile = profiles.firstOrNull { it.id == activeProfileId }

        setupConfig = SetupConfig(
            bluetoothDeviceName = if (bluetoothCompleted) "VisionCore-A2FI" else "",
            activeProfileName = activeProfile?.name.orEmpty(),
            nearLeft = activeProfile?.nearLeft ?: "+1.50",
            nearRight = activeProfile?.nearRight ?: "+1.50",
            farLeft = activeProfile?.farLeft ?: "-2.00",
            farRight = activeProfile?.farRight ?: "-2.00",
            calibrationCompleted = calibrationCompleted,
            deadBatteryMode = if (settingsCompleted) "Configured" else "Stay in last mode"
        )
    }

    fun deleteProfile(profileId: Int) {
        if (profiles.size > 1) {
            profiles.removeAll { it.id == profileId }

            if (activeProfileId == profileId) {
                activeProfileId = profiles.first().id
            }

            if (selectedProfileId == profileId) {
                selectedProfileId = activeProfileId
            }
        }
    }

    fun editProfile(profileId: Int, newName: String) {
        val cleanedName = newName.trim()
        if (cleanedName.isBlank()) return

        val index = profiles.indexOfFirst { it.id == profileId }
        if (index != -1) {
            profiles[index] = profiles[index].copy(name = cleanedName)
        }
    }

    fun updateProfile(updatedProfile: Profile) {
        val index = profiles.indexOfFirst { it.id == updatedProfile.id }
        if (index != -1) {
            profiles[index] = updatedProfile
        }
    }

    fun goBack() {
        currentScreen = when (currentScreen) {
            Screen.Dashboard -> Screen.Dashboard
            Screen.Onboarding -> Screen.Onboarding
            Screen.DevicePower -> devicePowerReturnScreen
            Screen.Prescription -> prescriptionReturnScreen
            Screen.Profiles,
            Screen.Settings,
            Screen.Bluetooth,
            Screen.ManualOverride,
            Screen.ProfileDetails -> Screen.Dashboard
            Screen.CreateProfile -> Screen.Profiles
            Screen.AllSet -> Screen.Dashboard
        }
    }

    BackHandler { goBack() }

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
                        bluetoothCompleted = bluetoothCompleted,
                        profileCompleted = profileCompleted,
                        prescriptionCompleted = prescriptionCompleted,
                        calibrationCompleted = calibrationCompleted,
                        settingsCompleted = settingsCompleted,
                        onManualOverrideClick = {
                            saveSetupConfig()
                            currentScreen = Screen.ManualOverride
                        },
                        onProfilesClick = { currentScreen = Screen.Profiles },
                        onPrescriptionClick = {
                            prescriptionReturnScreen = Screen.Dashboard
                            currentScreen = Screen.Prescription
                        },
                        onDevicePowerClick = {
                            devicePowerReturnScreen = Screen.Dashboard
                            currentScreen = Screen.DevicePower
                        },
                        onSettingsClick = { currentScreen = Screen.Settings },
                        onBluetoothClick = { currentScreen = Screen.Bluetooth }
                    )
                }
            }

            Screen.Settings -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    SettingsScreen(
                        onBackClick = { currentScreen = Screen.Dashboard },
                        onContinueClick = {
                            settingsCompleted = true
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }

            Screen.Bluetooth -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    BluetoothScreen(
                        onBackClick = { currentScreen = Screen.Dashboard },
                        onConnected = { bluetoothCompleted = true },
                        onContinueClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }

            Screen.ManualOverride -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    ManualOverrideScreen(
                        setupConfig = setupConfig,
                        profiles = profiles,
                        activeProfileId = activeProfileId,
                        onProfileSelected = { profile ->
                            activeProfileId = profile.id
                            selectedProfileId = profile.id
                            profileCompleted = true
                            saveSetupConfig()
                        },
                        onProfileEdited = { updatedProfile ->
                            updateProfile(updatedProfile)
                            activeProfileId = updatedProfile.id
                            selectedProfileId = updatedProfile.id
                            profileCompleted = true
                            saveSetupConfig()
                        },
                        onRecalibrateClick = {
                            devicePowerReturnScreen = Screen.ManualOverride
                            currentScreen = Screen.DevicePower
                        },
                        onDioptriesClick = {
                            prescriptionReturnScreen = Screen.ManualOverride
                            currentScreen = Screen.Prescription
                        },
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }

            Screen.Profiles -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    ProfilesScreen(
                        profiles = profiles,
                        activeProfileId = activeProfileId,
                        onProfileClick = { profile ->
                            activeProfileId = profile.id
                            selectedProfileId = profile.id
                            profileCompleted = true
                            saveSetupConfig()
                        },
                        onAddProfileClick = { currentScreen = Screen.CreateProfile },
                        onEditProfileClick = { profile, newName ->
                            editProfile(profile.id, newName)
                            saveSetupConfig()
                        },
                        onDeleteProfileClick = { profile ->
                            deleteProfile(profile.id)
                            saveSetupConfig()
                        },
                        onContinueClick = {
                            profileCompleted = true
                            saveSetupConfig()
                            currentScreen = Screen.Dashboard
                        },
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }

            Screen.CreateProfile -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    CreateProfileScreen(
                        onBackClick = { currentScreen = Screen.Profiles },
                        onAddProfile = { name, age ->
                            val finalName = name.ifBlank { "Unnamed profile" }
                            val newProfile = Profile(
                                id = nextProfileId,
                                name = "$finalName ($age)",
                                createdAt = getCurrentDateText()
                            )

                            profiles.add(newProfile)
                            activeProfileId = newProfile.id
                            selectedProfileId = newProfile.id
                            profileCompleted = true
                            nextProfileId++
                            currentScreen = Screen.Profiles
                        }
                    )
                }
            }

            Screen.ProfileDetails -> {
                val selectedProfile = profiles.firstOrNull { it.id == selectedProfileId }

                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    if (selectedProfile == null) {
                        LaunchedEffect(Unit) {
                            currentScreen = Screen.Profiles
                        }
                    } else {
                        ProfileDetailsScreen(
                            profile = selectedProfile,
                            onDeleteProfileClick = {
                                deleteProfile(selectedProfile.id)
                                currentScreen = Screen.Profiles
                            },
                            onBackClick = { currentScreen = Screen.Profiles }
                        )
                    }
                }
            }

            Screen.Prescription -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    PrescriptionScreen(
                        onBackClick = {
                            currentScreen = prescriptionReturnScreen
                        },
                        onContinueClick = {
                            prescriptionCompleted = true
                            saveSetupConfig()
                            currentScreen = prescriptionReturnScreen
                        }
                    )
                }
            }

            Screen.DevicePower -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    DevicePowerScreen(
                        onBackClick = {
                            currentScreen = devicePowerReturnScreen
                        },
                        onCompleted = {
                            calibrationCompleted = true
                            saveSetupConfig()
                            currentScreen = devicePowerReturnScreen
                        },
                        onSkipClick = {
                            currentScreen = devicePowerReturnScreen
                        }
                    )
                }
            }

            Screen.AllSet -> {
                AllSetScreen(
                    onBackClick = { currentScreen = Screen.Dashboard },
                    onContinueClick = { currentScreen = Screen.ManualOverride }
                )
            }
        }
    }
}
