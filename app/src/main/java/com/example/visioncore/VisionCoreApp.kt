package com.example.visioncore

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS_NAME = "vision_core_profiles_v2"
private const val KEY_PROFILES = "profiles"
private const val KEY_NEXT_PROFILE_ID = "next_profile_id"
private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"

@Composable
fun VisionCoreApp() {
    val context = LocalContext.current

    var currentScreen by rememberSaveable { mutableStateOf(Screen.Onboarding) }
    var setupConfig by remember { mutableStateOf(SetupConfig()) }

    val profiles = remember {
        mutableStateListOf<Profile>().apply {
            addAll(loadProfiles(context))
        }
    }

    var activeProfileId by rememberSaveable {
        mutableIntStateOf(loadActiveProfileId(context, profiles))
    }

    var selectedProfileId by rememberSaveable {
        mutableIntStateOf(activeProfileId)
    }

    var nextProfileId by rememberSaveable {
        mutableIntStateOf(loadNextProfileId(context, profiles))
    }

    var bluetoothCompleted by rememberSaveable { mutableStateOf(false) }
    var profileCompleted by rememberSaveable { mutableStateOf(profiles.isNotEmpty()) }
    var prescriptionCompleted by rememberSaveable { mutableStateOf(false) }
    var calibrationCompleted by rememberSaveable { mutableStateOf(false) }
    var settingsCompleted by rememberSaveable { mutableStateOf(false) }

    fun saveProfilesNow() {
        saveProfiles(
            context = context,
            profiles = profiles,
            nextProfileId = nextProfileId,
            activeProfileId = activeProfileId
        )
    }

    fun updateSetupConfigFromActiveProfile(profile: Profile) {
        setupConfig = setupConfig.copy(
            activeProfileName = profile.name,
            nearLeft = profile.nearLeft,
            nearRight = profile.nearRight,
            farLeft = profile.farLeft,
            farRight = profile.farRight
        )
    }

    fun isSetupCompleted(): Boolean {
        return bluetoothCompleted &&
                profileCompleted &&
                prescriptionCompleted &&
                calibrationCompleted &&
                settingsCompleted
    }

    fun goToDashboardOrAllSet() {
        currentScreen = if (isSetupCompleted()) {
            Screen.AllSet
        } else {
            Screen.Dashboard
        }
    }

    fun deleteProfile(profileId: Int) {
        profiles.removeAll { it.id == profileId }

        if (profiles.isEmpty()) {
            activeProfileId = 0
            selectedProfileId = 0
            profileCompleted = false
            setupConfig = setupConfig.copy(
                activeProfileName = ""
            )
        } else {
            if (activeProfileId == profileId) {
                activeProfileId = profiles.first().id
                updateSetupConfigFromActiveProfile(profiles.first())
            }

            if (selectedProfileId == profileId) {
                selectedProfileId = activeProfileId
            }

            profileCompleted = true
        }

        saveProfilesNow()
    }

    fun editProfileName(profile: Profile, newName: String) {
        val index = profiles.indexOfFirst { it.id == profile.id }

        if (index != -1) {
            profiles[index] = profiles[index].copy(
                name = newName.trim().ifBlank { profile.name }
            )

            selectedProfileId = profile.id
            activeProfileId = profile.id
            profileCompleted = true
            updateSetupConfigFromActiveProfile(profiles[index])

            saveProfilesNow()
        }
    }

    fun editFullProfile(updatedProfile: Profile) {
        val index = profiles.indexOfFirst { it.id == updatedProfile.id }

        if (index != -1) {
            profiles[index] = updatedProfile

            selectedProfileId = updatedProfile.id
            activeProfileId = updatedProfile.id
            profileCompleted = true
            updateSetupConfigFromActiveProfile(updatedProfile)

            saveProfilesNow()
        }
    }

    fun goBack() {
        currentScreen = when (currentScreen) {
            Screen.Dashboard -> Screen.Dashboard
            Screen.Onboarding -> Screen.Onboarding

            Screen.Profiles,
            Screen.Settings,
            Screen.Bluetooth,
            Screen.ManualOverride,
            Screen.Prescription,
            Screen.DevicePower,
            Screen.ProfileDetails,
            Screen.AllSet -> Screen.Dashboard

            Screen.CreateProfile -> Screen.Profiles
        }
    }

    BackHandler {
        goBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->

        when (currentScreen) {
            Screen.Onboarding -> {
                OnboardingScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSetUpClick = {
                        currentScreen = Screen.Dashboard
                    }
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
            }

            Screen.Settings -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    SettingsScreen(
                        selectedDeadBatteryMode = setupConfig.deadBatteryMode,
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onContinueClick = { selectedDeadBatteryMode ->
                            setupConfig = setupConfig.copy(
                                deadBatteryMode = selectedDeadBatteryMode
                            )

                            settingsCompleted = true
                            goToDashboardOrAllSet()
                        }
                    )
                }
            }

            Screen.Bluetooth -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    BluetoothScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onConnected = {
                            bluetoothCompleted = true
                        },
                        onContinueClick = {
                            bluetoothCompleted = true
                            goToDashboardOrAllSet()
                        }
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
                            updateSetupConfigFromActiveProfile(profile)
                            saveProfilesNow()
                        },
                        onProfileEdited = { updatedProfile ->
                            editFullProfile(updatedProfile)
                        },
                        onRecalibrateClick = {
                            currentScreen = Screen.DevicePower
                        },
                        onDioptriesClick = {
                            currentScreen = Screen.Prescription
                        },
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
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
                            updateSetupConfigFromActiveProfile(profile)
                            saveProfilesNow()
                        },
                        onAddProfileClick = {
                            currentScreen = Screen.CreateProfile
                        },
                        onEditProfileClick = { profile, newName ->
                            editProfileName(profile, newName)
                        },
                        onDeleteProfileClick = { profile ->
                            deleteProfile(profile.id)
                        },
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onContinueClick = {
                            profileCompleted = profiles.isNotEmpty()
                            profiles.firstOrNull { it.id == activeProfileId }?.let { profile ->
                                updateSetupConfigFromActiveProfile(profile)
                            }
                            saveProfilesNow()
                            goToDashboardOrAllSet()
                        }
                    )
                }
            }

            Screen.CreateProfile -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    CreateProfileScreen(
                        onBackClick = {
                            currentScreen = Screen.Profiles
                        },
                        onAddProfile = { name, age ->
                            val finalName = name.trim().ifBlank { "Unnamed profile" }

                            val newProfile = Profile(
                                id = nextProfileId,
                                name = "$finalName ($age)",
                                createdAt = getCurrentDateText()
                            )

                            profiles.add(newProfile)

                            activeProfileId = newProfile.id
                            selectedProfileId = newProfile.id
                            nextProfileId++

                            profileCompleted = true
                            updateSetupConfigFromActiveProfile(newProfile)
                            saveProfilesNow()

                            currentScreen = Screen.Profiles
                        }
                    )
                }
            }

            Screen.ProfileDetails -> {
                val selectedProfile = profiles.firstOrNull { it.id == selectedProfileId }

                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    if (selectedProfile == null) {
                        currentScreen = Screen.Profiles
                    } else {
                        ProfileDetailsScreen(
                            profile = selectedProfile,
                            onDeleteProfileClick = {
                                deleteProfile(selectedProfile.id)
                                currentScreen = Screen.Profiles
                            },
                            onBackClick = {
                                currentScreen = Screen.Profiles
                            }
                        )
                    }
                }
            }

            Screen.Prescription -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    PrescriptionScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onContinueClick = {
                            prescriptionCompleted = true
                            goToDashboardOrAllSet()
                        }
                    )
                }
            }

            Screen.DevicePower -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    DevicePowerScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onCompleted = {
                            calibrationCompleted = true
                            setupConfig = setupConfig.copy(
                                calibrationCompleted = true
                            )
                            goToDashboardOrAllSet()
                        }
                    )
                }
            }

            Screen.AllSet -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    AllSetScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onContinueClick = {
                            currentScreen = Screen.ManualOverride
                        }
                    )
                }
            }
        }
    }
}

private fun loadProfiles(context: Context): List<Profile> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val savedProfiles = prefs.getString(KEY_PROFILES, null)

    if (savedProfiles.isNullOrBlank()) {
        return emptyList()
    }

    return try {
        val jsonArray = JSONArray(savedProfiles)

        buildList {
            for (index in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(index)

                add(
                    Profile(
                        id = item.getInt("id"),
                        name = item.getString("name"),
                        createdAt = item.getString("createdAt"),
                        nearLeft = item.optString("nearLeft", "+1.50"),
                        nearRight = item.optString("nearRight", "+1.50"),
                        farLeft = item.optString("farLeft", "-2.00"),
                        farRight = item.optString("farRight", "-2.00")
                    )
                )
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveProfiles(
    context: Context,
    profiles: List<Profile>,
    nextProfileId: Int,
    activeProfileId: Int
) {
    val jsonArray = JSONArray()

    profiles.forEach { profile ->
        val item = JSONObject().apply {
            put("id", profile.id)
            put("name", profile.name)
            put("createdAt", profile.createdAt)
            put("nearLeft", profile.nearLeft)
            put("nearRight", profile.nearRight)
            put("farLeft", profile.farLeft)
            put("farRight", profile.farRight)
        }

        jsonArray.put(item)
    }

    context
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_PROFILES, jsonArray.toString())
        .putInt(KEY_NEXT_PROFILE_ID, nextProfileId)
        .putInt(KEY_ACTIVE_PROFILE_ID, activeProfileId)
        .apply()
}

private fun loadNextProfileId(
    context: Context,
    profiles: List<Profile>
): Int {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    return prefs.getInt(
        KEY_NEXT_PROFILE_ID,
        (profiles.maxOfOrNull { it.id } ?: 0) + 1
    )
}

private fun loadActiveProfileId(
    context: Context,
    profiles: List<Profile>
): Int {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    if (profiles.isEmpty()) {
        return 0
    }

    val savedActiveProfileId = prefs.getInt(
        KEY_ACTIVE_PROFILE_ID,
        profiles.first().id
    )

    return if (profiles.any { it.id == savedActiveProfileId }) {
        savedActiveProfileId
    } else {
        profiles.first().id
    }
}