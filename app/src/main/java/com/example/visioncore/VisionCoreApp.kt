package com.example.visioncore

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.visioncore.bluetooth.ConnectionState
import com.example.visioncore.bluetooth.RealBluetoothRepository
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS_NAME = "vision_core_profiles_v2"
private const val KEY_PROFILES = "profiles"
private const val KEY_NEXT_PROFILE_ID = "next_profile_id"
private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
private const val KEY_PRESCRIPTION_COMPLETED = "prescription_completed"
private const val KEY_CALIBRATION_COMPLETED = "calibration_completed"
private const val KEY_SETTINGS_COMPLETED = "settings_completed"
private const val KEY_DEAD_BATTERY_MODE = "dead_battery_mode"
private const val KEY_AUTO_MODE_ENABLED = "auto_mode_enabled"
private const val KEY_BLINK_RED = "blink_red_before_low_battery"
private const val KEY_SETUP_DONE = "setup_done"

@Composable
fun VisionCoreApp() {
    val context = LocalContext.current
    val bluetoothRepository = remember { RealBluetoothRepository(context) }
    val bluetoothConnectionState by bluetoothRepository.connectionState.collectAsState()
    val scope = rememberCoroutineScope()

    val appSettings = remember { loadAppSettings(context) }

    var currentScreen by rememberSaveable {
        mutableStateOf(Screen.Onboarding)
    }
    var setupConfig by remember {
        mutableStateOf(SetupConfig(deadBatteryMode = appSettings.deadBatteryMode))
    }

    var autoModeEnabled by rememberSaveable {
        mutableStateOf(appSettings.autoModeEnabled)
    }

    var blinkRedBeforeLowBattery by rememberSaveable {
        mutableStateOf(appSettings.blinkRedBeforeLowBattery)
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

    val activeProfile = profiles.firstOrNull { it.id == activeProfileId }
    var pendingDeadBatterySync by rememberSaveable { mutableStateOf(false) }
    var wasConnected by remember { mutableStateOf(false) }

    var bluetoothCompleted by rememberSaveable { mutableStateOf(false) }
    var profileCompleted by rememberSaveable { mutableStateOf(profiles.isNotEmpty()) }
    var prescriptionCompleted by rememberSaveable { mutableStateOf(appSettings.prescriptionCompleted) }
    var calibrationCompleted by rememberSaveable { mutableStateOf(appSettings.calibrationCompleted) }
    var settingsCompleted by rememberSaveable { mutableStateOf(appSettings.settingsCompleted) }

    var currentGlassesTilt by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        bluetoothRepository.incomingData.collect { bytes ->
            if (bytes.size > 4) {
                val text = try { String(bytes, Charsets.UTF_8) } catch (_: Exception) { return@collect }
                if (text.startsWith("X:")) {
                    val y = text.substringAfter("Y:", "").trim().toFloatOrNull()
                    if (y != null) currentGlassesTilt = y.toInt()
                }
            }
        }
    }

    // On connect: send full config. On disconnect: reset bluetooth step.
    LaunchedEffect(bluetoothConnectionState) {
        when (bluetoothConnectionState) {
            ConnectionState.Connected -> {
                wasConnected = true
                profiles.firstOrNull { it.id == activeProfileId }?.let {
                    bluetoothRepository.sendBytes(it.toPrescriptionBytes())
                }
                bluetoothRepository.sendBytes(autoModeEnabled.toModeBytes())
                bluetoothRepository.sendBytes(setupConfig.deadBatteryMode.toDeadBatteryBytes())
                bluetoothRepository.sendBytes(blinkRedBeforeLowBattery.toBlinkRedBytes())
                pendingDeadBatterySync = false
            }
            ConnectionState.Disconnected -> {
                if (wasConnected) {
                    bluetoothCompleted = false
                    wasConnected = false
                }
            }
            else -> {}
        }
    }

    // Profile dioptries changed while connected: resend prescription
    LaunchedEffect(activeProfile) {
        if (bluetoothConnectionState == ConnectionState.Connected && activeProfile != null) {
            bluetoothRepository.sendBytes(activeProfile.toPrescriptionBytes())
        }
    }

    // Auto/manual mode toggled while connected: resend mode
    LaunchedEffect(autoModeEnabled) {
        if (bluetoothConnectionState == ConnectionState.Connected) {
            bluetoothRepository.sendBytes(autoModeEnabled.toModeBytes())
        }
    }

    // Blink red setting changed while connected: resend
    LaunchedEffect(blinkRedBeforeLowBattery) {
        if (bluetoothConnectionState == ConnectionState.Connected) {
            bluetoothRepository.sendBytes(blinkRedBeforeLowBattery.toBlinkRedBytes())
        }
    }

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
        if (isSetupCompleted()) {
            savePref(context, KEY_SETUP_DONE, true)
        }
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
            Screen.AllSet -> Screen.Dashboard

            Screen.ProfileDetails -> Screen.Profiles

            Screen.CreateProfile -> createProfileReturnScreen
        }
    }

    BackHandler(enabled = currentScreen != Screen.Dashboard && currentScreen != Screen.Onboarding) {
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

                            if (bluetoothConnectionState == ConnectionState.Connected) {
                                scope.launch {
                                    bluetoothRepository.sendBytes(selectedDeadBatteryMode.toDeadBatteryBytes())
                                }
                            } else {
                                pendingDeadBatterySync = true
                            }

                            settingsCompleted = true
                            savePref(context, KEY_SETTINGS_COMPLETED, true)
                            savePref(context, KEY_DEAD_BATTERY_MODE, selectedDeadBatteryMode)
                            goToDashboardOrAllSet()
                        }
                    )
                }
            }

            Screen.Bluetooth -> {
                AppBackground(modifier = Modifier.padding(innerPadding)) {
                    BluetoothScreen(
                        bluetoothRepository = bluetoothRepository,
                        onBackClick = {
                            goBackFromBluetooth()
                        },
                        onConnected = { deviceName ->
                            setupConfig = setupConfig.copy(bluetoothDeviceName = deviceName)
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
                        isConnected = bluetoothConnectionState == ConnectionState.Connected,
                        currentGlassesTilt = currentGlassesTilt,
                        onAutoModeChanged = { enabled ->
                            autoModeEnabled = enabled
                            savePref(context, KEY_AUTO_MODE_ENABLED, enabled)
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
                        onDeadBatteryModeChanged = { mode ->
                            setupConfig = setupConfig.copy(deadBatteryMode = mode)
                            savePref(context, KEY_DEAD_BATTERY_MODE, mode)
                            if (bluetoothConnectionState == ConnectionState.Connected) {
                                scope.launch {
                                    bluetoothRepository.sendBytes(mode.toDeadBatteryBytes())
                                }
                            } else {
                                pendingDeadBatterySync = true
                            }
                        },
                        onManualModeSelected = { mode ->
                            if (bluetoothConnectionState == ConnectionState.Connected) {
                                val position: Byte = when (mode) {
                                    ManualMode.Near -> 0x00
                                    ManualMode.Far  -> 0x01
                                    ManualMode.Off  -> 0x02
                                }
                                scope.launch {
                                    bluetoothRepository.sendBytes(byteArrayOf(0x05, position))
                                }
                            }
                        },
                        blinkRedEnabled = blinkRedBeforeLowBattery,
                        onBlinkRedChanged = { enabled ->
                            blinkRedBeforeLowBattery = enabled
                            savePref(context, KEY_BLINK_RED, enabled)
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
                                savePref(context, KEY_PRESCRIPTION_COMPLETED, true)

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
                        currentGlassesTilt = currentGlassesTilt,
                        onCaptureStep = { stepIndex ->
                            if (bluetoothConnectionState != ConnectionState.Connected) {
                                true
                            } else {
                                bluetoothRepository.sendBytes(byteArrayOf(0x04, stepIndex.toByte()))
                                withTimeoutOrNull(3_000) {
                                    bluetoothRepository.incomingData.first { bytes ->
                                        bytes.size == 2 && bytes[0] == 0x04.toByte()
                                    }
                                }?.let { bytes -> bytes[1] == 0x01.toByte() } ?: false
                            }
                        },
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
                            savePref(context, KEY_CALIBRATION_COMPLETED, true)
                            if (bluetoothConnectionState == ConnectionState.Connected) {
                                scope.launch { bluetoothRepository.sendBytes(byteArrayOf(0x04, 0x02)) }
                            }

                            setupConfig = setupConfig.copy(
                                calibrationCompleted = true
                            )

                            markActiveProfileCalibrated()

                            if (devicePowerReturnScreen == Screen.ManualOverride) {
                                currentScreen = Screen.ManualOverride
                            } else {
                                goToDashboardOrAllSet()
                            }
                        },
                        onSkipClick = {
                            calibrationCompleted = true
                            savePref(context, KEY_CALIBRATION_COMPLETED, true)
                            currentScreen = if (devicePowerReturnScreen == Screen.ManualOverride) {
                                Screen.ManualOverride
                            } else {
                                Screen.Dashboard
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

private data class AppSettings(
    val prescriptionCompleted: Boolean,
    val calibrationCompleted: Boolean,
    val settingsCompleted: Boolean,
    val deadBatteryMode: String,
    val autoModeEnabled: Boolean,
    val blinkRedBeforeLowBattery: Boolean,
    val setupDone: Boolean
)

private fun loadAppSettings(context: Context): AppSettings {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return AppSettings(
        prescriptionCompleted = prefs.getBoolean(KEY_PRESCRIPTION_COMPLETED, false),
        calibrationCompleted = prefs.getBoolean(KEY_CALIBRATION_COMPLETED, false),
        settingsCompleted = prefs.getBoolean(KEY_SETTINGS_COMPLETED, false),
        deadBatteryMode = prefs.getString(KEY_DEAD_BATTERY_MODE, "Stay in last mode") ?: "Stay in last mode",
        autoModeEnabled = prefs.getBoolean(KEY_AUTO_MODE_ENABLED, true),
        blinkRedBeforeLowBattery = prefs.getBoolean(KEY_BLINK_RED, true),
        setupDone = prefs.getBoolean(KEY_SETUP_DONE, false)
    )
}

private fun savePref(context: Context, key: String, value: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit().putBoolean(key, value).apply()
}

private fun savePref(context: Context, key: String, value: String) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit().putString(key, value).apply()
}