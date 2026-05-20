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

    var autoModeEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    var createProfileReturnScreen by rememberSaveable {
        mutableStateOf(Screen.Profiles)
    }

    var prescriptionReturnScreen by rememberSaveable {
        mutableStateOf(Screen.Dashboard)
    }

    var devicePowerReturnScreen by rememberSaveable {
        mutableStateOf(Screen.Dashboard)
    }

    var bluetoothReturnScreen by rememberSaveable {
        mutableStateOf(Screen.Dashboard)
    }

    var manualOverrideStartOnProfilesTab by rememberSaveable {
        mutableStateOf(false)
    }

    var manualOverrideStartOnDeviceTab by rememberSaveable {
        mutableStateOf(false)
    }

    var editingProfileId by rememberSaveable {
        mutableIntStateOf(0)
    }

    var pendingProfileName by rememberSaveable {
        mutableStateOf("")
    }

    var pendingProfileAge by rememberSaveable {
        mutableStateOf("")
    }

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

    fun getActiveProfile(): Profile? {
        return profiles.firstOrNull { it.id == activeProfileId }
    }

    fun updateSetupConfigFromActiveProfile(profile: Profile) {
        setupConfig = setupConfig.copy(
            activeProfileName = profile.name,
            nearLeft = profile.nearLeft,
            nearRight = profile.nearRight,
            farLeft = profile.farLeft,
            farRight = profile.farRight,
            calibrationCompleted = profile.calibrationCompleted
        )
    }

    fun updateActiveProfileDioptries(
        nearLeft: String,
        nearRight: String,
        farLeft: String,
        farRight: String
    ) {
        val index = profiles.indexOfFirst { it.id == activeProfileId }

        if (index != -1) {
            val updatedProfile = profiles[index].copy(
                nearLeft = nearLeft,
                nearRight = nearRight,
                farLeft = farLeft,
                farRight = farRight
            )

            profiles[index] = updatedProfile
            selectedProfileId = updatedProfile.id
            activeProfileId = updatedProfile.id

            updateSetupConfigFromActiveProfile(updatedProfile)
            saveProfilesNow()
        } else {
            setupConfig = setupConfig.copy(
                nearLeft = nearLeft,
                nearRight = nearRight,
                farLeft = farLeft,
                farRight = farRight
            )
        }
    }

    fun markActiveProfileCalibrated() {
        val index = profiles.indexOfFirst { it.id == activeProfileId }

        if (index != -1) {
            val updatedProfile = profiles[index].copy(
                calibrationCompleted = true
            )

            profiles[index] = updatedProfile
            selectedProfileId = updatedProfile.id
            activeProfileId = updatedProfile.id

            updateSetupConfigFromActiveProfile(updatedProfile)
            saveProfilesNow()
        }
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

    fun returnFromBluetooth() {
        currentScreen = when (bluetoothReturnScreen) {
            Screen.ManualOverride -> {
                manualOverrideStartOnProfilesTab = false
                manualOverrideStartOnDeviceTab = true
                Screen.ManualOverride
            }

            else -> {
                goToDashboardOrAllSet()
                return
            }
        }
    }

    fun goBackFromBluetooth() {
        currentScreen = when (bluetoothReturnScreen) {
            Screen.ManualOverride -> {
                manualOverrideStartOnProfilesTab = false
                manualOverrideStartOnDeviceTab = true
                Screen.ManualOverride
            }

            else -> Screen.Dashboard
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

    fun startEditingProfile(profile: Profile, returnScreen: Screen) {
        editingProfileId = profile.id
        pendingProfileName = extractProfileNameWithoutAgeRange(profile.name)
        pendingProfileAge = extractAgeRangeFromProfileName(profile.name)

        createProfileReturnScreen = returnScreen
        prescriptionReturnScreen = returnScreen

        if (returnScreen == Screen.ManualOverride) {
            manualOverrideStartOnProfilesTab = true
            manualOverrideStartOnDeviceTab = false
        }

        currentScreen = Screen.CreateProfile
    }

    fun finishEditingProfile(
        nearLeft: String,
        nearRight: String,
        farLeft: String,
        farRight: String
    ) {
        val profileId = editingProfileId
        val index = profiles.indexOfFirst { it.id == profileId }

        if (index != -1) {
            val oldProfile = profiles[index]

            val updatedProfile = oldProfile.copy(
                name = buildProfileNameWithAgeRange(
                    name = pendingProfileName,
                    ageRange = pendingProfileAge,
                    fallbackName = oldProfile.name
                ),
                nearLeft = nearLeft,
                nearRight = nearRight,
                farLeft = farLeft,
                farRight = farRight
            )

            profiles[index] = updatedProfile
            activeProfileId = updatedProfile.id
            selectedProfileId = updatedProfile.id
            profileCompleted = true
            updateSetupConfigFromActiveProfile(updatedProfile)
            saveProfilesNow()
        }

        editingProfileId = 0
        pendingProfileName = ""
        pendingProfileAge = ""

        currentScreen = prescriptionReturnScreen
    }

    fun goBack() {
        currentScreen = when (currentScreen) {
            Screen.Dashboard -> Screen.Dashboard
            Screen.Onboarding -> Screen.Onboarding

            Screen.Bluetooth -> {
                goBackFromBluetooth()
                return
            }

            Screen.Profiles,
            Screen.Settings,
            Screen.ManualOverride,
            Screen.Prescription,
            Screen.DevicePower,
            Screen.ProfileDetails,
            Screen.AllSet -> Screen.Dashboard

            Screen.CreateProfile -> createProfileReturnScreen
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
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = false
                            currentScreen = Screen.ManualOverride
                        },
                        onProfilesClick = {
                            currentScreen = Screen.Profiles
                        },
                        onPrescriptionClick = {
                            editingProfileId = 0
                            prescriptionReturnScreen = Screen.Dashboard
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = false
                            currentScreen = Screen.Prescription
                        },
                        onDevicePowerClick = {
                            devicePowerReturnScreen = Screen.Dashboard
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = false
                            currentScreen = Screen.DevicePower
                        },
                        onSettingsClick = {
                            currentScreen = Screen.Settings
                        },
                        onBluetoothClick = {
                            bluetoothReturnScreen = Screen.Dashboard
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = false
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
                            goBackFromBluetooth()
                        },
                        onConnected = {
                            setupConfig = setupConfig.copy(
                                bluetoothDeviceName = "VisionCore-A2FI"
                            )

                            bluetoothCompleted = true
                        },
                        onContinueClick = {
                            setupConfig = setupConfig.copy(
                                bluetoothDeviceName = "VisionCore-A2FI"
                            )

                            bluetoothCompleted = true
                            returnFromBluetooth()
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
                        startOnProfilesTab = manualOverrideStartOnProfilesTab,
                        startOnDeviceTab = manualOverrideStartOnDeviceTab,
                        autoModeEnabled = autoModeEnabled,
                        onAutoModeChanged = { enabled ->
                            autoModeEnabled = enabled
                        },
                        onAddProfileClick = {
                            editingProfileId = 0
                            createProfileReturnScreen = Screen.ManualOverride
                            manualOverrideStartOnProfilesTab = true
                            manualOverrideStartOnDeviceTab = false
                            currentScreen = Screen.CreateProfile
                        },
                        onDeviceSettingsClick = {
                            bluetoothReturnScreen = Screen.ManualOverride
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = true
                            currentScreen = Screen.Bluetooth
                        },
                        onProfileSelected = { profile ->
                            activeProfileId = profile.id
                            selectedProfileId = profile.id
                            profileCompleted = true
                            updateSetupConfigFromActiveProfile(profile)
                            saveProfilesNow()
                        },
                        onProfileEdited = { profile ->
                            startEditingProfile(
                                profile = profile,
                                returnScreen = Screen.ManualOverride
                            )
                        },
                        onProfileDelete = { profile ->
                            deleteProfile(profile.id)
                            manualOverrideStartOnProfilesTab = true
                            manualOverrideStartOnDeviceTab = false
                        },
                        onRecalibrateClick = {
                            devicePowerReturnScreen = Screen.ManualOverride
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = true
                            currentScreen = Screen.DevicePower
                        },
                        onDioptriesClick = {
                            val activeProfile = getActiveProfile()

                            if (activeProfile != null) {
                                editingProfileId = activeProfile.id
                                pendingProfileName =
                                    extractProfileNameWithoutAgeRange(activeProfile.name)
                                pendingProfileAge =
                                    extractAgeRangeFromProfileName(activeProfile.name)
                            } else {
                                editingProfileId = 0
                            }

                            prescriptionReturnScreen = Screen.ManualOverride
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = true
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
                            editingProfileId = 0
                            createProfileReturnScreen = Screen.Profiles
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = false
                            currentScreen = Screen.CreateProfile
                        },
                        onEditProfileClick = { profile, _ ->
                            startEditingProfile(
                                profile = profile,
                                returnScreen = Screen.Profiles
                            )
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
                val editedProfile = profiles.firstOrNull { it.id == editingProfileId }

                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    CreateProfileScreen(
                        initialName = if (editingProfileId != 0) {
                            pendingProfileName.ifBlank {
                                editedProfile?.name
                                    ?.let { extractProfileNameWithoutAgeRange(it) }
                                    .orEmpty()
                            }
                        } else {
                            ""
                        },
                        initialAge = if (editingProfileId != 0) {
                            pendingProfileAge.ifBlank {
                                editedProfile?.name
                                    ?.let { extractAgeRangeFromProfileName(it) }
                                    .orEmpty()
                            }
                        } else {
                            ""
                        },
                        title = if (editingProfileId != 0) {
                            "Edit profile"
                        } else {
                            "Profile"
                        },
                        description = if (editingProfileId != 0) {
                            "Update profile details."
                        } else {
                            "Who is this profile for?"
                        },
                        buttonText = if (editingProfileId != 0) {
                            "Continue"
                        } else {
                            "add profile"
                        },
                        onBackClick = {
                            currentScreen = createProfileReturnScreen
                        },
                        onAddProfile = { name, age ->
                            val finalName = name.trim().ifBlank { "Unnamed profile" }

                            if (editingProfileId != 0) {
                                pendingProfileName = finalName
                                pendingProfileAge = age
                                currentScreen = Screen.Prescription
                            } else {
                                val newProfile = Profile(
                                    id = nextProfileId,
                                    name = buildProfileNameWithAgeRange(
                                        name = finalName,
                                        ageRange = age,
                                        fallbackName = finalName
                                    ),
                                    createdAt = getCurrentDateText()
                                )

                                profiles.add(newProfile)

                                activeProfileId = newProfile.id
                                selectedProfileId = newProfile.id
                                nextProfileId++

                                profileCompleted = true
                                updateSetupConfigFromActiveProfile(newProfile)
                                saveProfilesNow()

                                currentScreen = createProfileReturnScreen
                            }
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
                val editedProfile = profiles.firstOrNull { it.id == editingProfileId }

                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    PrescriptionScreen(
                        initialNearLeft = editedProfile?.nearLeft ?: setupConfig.nearLeft,
                        initialNearRight = editedProfile?.nearRight ?: setupConfig.nearRight,
                        initialFarLeft = editedProfile?.farLeft ?: setupConfig.farLeft,
                        initialFarRight = editedProfile?.farRight ?: setupConfig.farRight,
                        onBackClick = {
                            if (editingProfileId != 0) {
                                currentScreen = Screen.CreateProfile
                            } else {
                                currentScreen =
                                    if (prescriptionReturnScreen == Screen.ManualOverride) {
                                        Screen.ManualOverride
                                    } else {
                                        Screen.Dashboard
                                    }
                            }
                        },
                        onContinueClick = { nearLeft, nearRight, farLeft, farRight ->
                            if (editingProfileId != 0) {
                                finishEditingProfile(
                                    nearLeft = nearLeft,
                                    nearRight = nearRight,
                                    farLeft = farLeft,
                                    farRight = farRight
                                )
                            } else {
                                updateActiveProfileDioptries(
                                    nearLeft = nearLeft,
                                    nearRight = nearRight,
                                    farLeft = farLeft,
                                    farRight = farRight
                                )

                                prescriptionCompleted = true

                                if (prescriptionReturnScreen == Screen.ManualOverride) {
                                    currentScreen = Screen.ManualOverride
                                } else {
                                    goToDashboardOrAllSet()
                                }
                            }
                        }
                    )
                }
            }

            Screen.DevicePower -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    DevicePowerScreen(
                        onBackClick = {
                            currentScreen =
                                if (devicePowerReturnScreen == Screen.ManualOverride) {
                                    Screen.ManualOverride
                                } else {
                                    Screen.Dashboard
                                }
                        },
                        onCompleted = {
                            calibrationCompleted = true

                            setupConfig = setupConfig.copy(
                                calibrationCompleted = true
                            )

                            markActiveProfileCalibrated()

                            if (devicePowerReturnScreen == Screen.ManualOverride) {
                                currentScreen = Screen.ManualOverride
                            } else {
                                goToDashboardOrAllSet()
                            }
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
                            manualOverrideStartOnProfilesTab = false
                            manualOverrideStartOnDeviceTab = false
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
                        nearRight = item.optString("nearRight", "+1.75"),
                        farLeft = item.optString("farLeft", "-2.00"),
                        farRight = item.optString("farRight", "-2.25"),
                        calibrationCompleted = item.optBoolean("calibrationCompleted", false)
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
            put("calibrationCompleted", profile.calibrationCompleted)
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

private fun extractProfileNameWithoutAgeRange(profileName: String): String {
    return profileName
        .replace(Regex("\\s*\\([^)]*\\)\\s*$"), "")
        .trim()
}

private fun extractAgeRangeFromProfileName(profileName: String): String {
    val match = Regex("\\(([^)]*)\\)\\s*$").find(profileName)

    return match
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
        .orEmpty()
}

private fun buildProfileNameWithAgeRange(
    name: String,
    ageRange: String,
    fallbackName: String
): String {
    val cleanedName = name
        .trim()
        .ifBlank {
            extractProfileNameWithoutAgeRange(fallbackName).ifBlank {
                fallbackName
            }
        }

    val cleanedAgeRange = ageRange.trim()

    return if (cleanedAgeRange.isBlank()) {
        cleanedName
    } else {
        "$cleanedName ($cleanedAgeRange)"
    }
}