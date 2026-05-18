package com.example.visioncore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visioncore.ui.theme.VisionCoreTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

data class Profile(
    val id: Int,
    val name: String,
    val createdAt: String,
    val nearLeft: String = "+1.50",
    val nearRight: String = "+1.75",
    val farLeft: String = "-2.00",
    val farRight: String = "-2.25"
)

enum class Screen {
    Onboarding,
    Dashboard,
    Settings,
    Bluetooth,
    ManualOverride,
    Profiles,
    ProfileDetails,
    Prescription,
    DevicePower
}

fun getCurrentDateText(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date())
}

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.17f))
    ) {
        content()
    }
}

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
            profiles.removeAll { profile ->
                profile.id == profileId
            }

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
                    onSetUpClick = {
                        currentScreen = Screen.Dashboard
                    }
                )
            }

            Screen.Dashboard -> {
                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    DashboardScreen(
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
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    SettingsScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }

            Screen.Bluetooth -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    BluetoothScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }

            Screen.ManualOverride -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    ManualOverrideScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }

            Screen.Profiles -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    ProfilesScreen(
                        profiles = profiles,
                        activeProfileId = activeProfileId,
                        onProfileClick = { profile ->
                            activeProfileId = profile.id
                            selectedProfileId = profile.id
                            currentScreen = Screen.ProfileDetails
                        },
                        onAddProfileClick = { profileName ->
                            val finalProfileName = profileName.ifBlank {
                                "Unnamed profile"
                            }

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
                        onDeleteActiveProfileClick = {
                            deleteProfile(activeProfileId)
                        },
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }

            Screen.ProfileDetails -> {
                BackHandler {
                    currentScreen = Screen.Profiles
                }

                val selectedProfile = profiles.firstOrNull { profile ->
                    profile.id == selectedProfileId
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    if (selectedProfile != null) {
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
                    } else {
                        currentScreen = Screen.Profiles
                    }
                }
            }

            Screen.Prescription -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    PrescriptionScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }

            Screen.DevicePower -> {
                BackHandler {
                    currentScreen = Screen.Dashboard
                }

                AppBackground(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    DevicePowerScreen(
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onSetUpClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        VisionCoreAbstractBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 100.dp, bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            VisionCoreLogo()

            Spacer(modifier = Modifier.weight(1.6f))

            Button(
                onClick = onSetUpClick,
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "Set up",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "   →",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VisionCoreLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(160.dp)
        ) {
            val trianglePath = Path().apply {
                moveTo(size.width * 0.10f, size.height * 0.58f)
                lineTo(size.width * 0.48f, size.height * 0.30f)
                lineTo(size.width * 0.56f, size.height * 0.80f)
                close()
            }

            drawPath(
                path = trianglePath,
                color = Color.Black
            )

            drawCircle(
                color = Color.Red,
                radius = size.width * 0.23f,
                center = Offset(
                    x = size.width * 0.73f,
                    y = size.height * 0.56f
                )
            )

            val cutPath = Path().apply {
                moveTo(size.width * 0.73f, size.height * 0.56f)
                lineTo(size.width * 0.98f, size.height * 0.45f)
                lineTo(size.width * 0.98f, size.height * 0.74f)
                close()
            }

            drawPath(
                path = cutPath,
                color = Color.White
            )

            drawCircle(
                color = Color.Black,
                radius = size.width * 0.08f,
                center = Offset(
                    x = size.width * 0.73f,
                    y = size.height * 0.56f
                )
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Vision")
                }

                withStyle(
                    style = SpanStyle(
                        color = Color.Red,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Core")
                }
            },
            fontSize = 30.sp
        )
    }
}

@Composable
fun VisionCoreAbstractBackground() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val palePink = Color(0xFFFFCDD2).copy(alpha = 0.65f)
        val paleGray = Color(0xFFD9D9D9).copy(alpha = 0.75f)
        val veryLightGray = Color(0xFFEDEDED).copy(alpha = 0.85f)

        rotate(
            degrees = -35f,
            pivot = Offset(size.width / 2f, size.height / 2f)
        ) {
            drawRoundRect(
                color = palePink,
                topLeft = Offset(-size.width * 0.18f, size.height * 0.02f),
                size = Size(size.width * 0.23f, size.height * 1.35f),
                cornerRadius = CornerRadius(80f, 80f)
            )

            drawRoundRect(
                color = paleGray,
                topLeft = Offset(size.width * 0.78f, -size.height * 0.15f),
                size = Size(size.width * 0.23f, size.height * 1.35f),
                cornerRadius = CornerRadius(80f, 80f)
            )

            drawRoundRect(
                color = paleGray,
                topLeft = Offset(-size.width * 0.25f, size.height * 0.70f),
                size = Size(size.width * 0.24f, size.height * 0.65f),
                cornerRadius = CornerRadius(80f, 80f)
            )

            drawRoundRect(
                color = veryLightGray,
                topLeft = Offset(size.width * 0.26f, size.height * 0.06f),
                size = Size(size.width * 0.035f, size.height * 0.62f),
                cornerRadius = CornerRadius(20f, 20f)
            )

            drawRoundRect(
                color = veryLightGray,
                topLeft = Offset(size.width * 0.66f, size.height * 0.22f),
                size = Size(size.width * 0.035f, size.height * 0.70f),
                cornerRadius = CornerRadius(20f, 20f)
            )

            drawRoundRect(
                color = veryLightGray,
                topLeft = Offset(-size.width * 0.05f, size.height * 0.46f),
                size = Size(size.width * 0.035f, size.height * 0.45f),
                cornerRadius = CornerRadius(20f, 20f)
            )
        }

        drawCircle(
            color = palePink,
            radius = size.width * 0.13f,
            center = Offset(
                x = size.width * 0.68f,
                y = size.height * 0.04f
            )
        )

        drawCircle(
            color = palePink,
            radius = size.width * 0.11f,
            center = Offset(
                x = size.width * 0.27f,
                y = size.height * 0.73f
            )
        )

        drawRoundRect(
            color = paleGray,
            topLeft = Offset(
                x = size.width * 0.75f,
                y = size.height * 0.29f
            ),
            size = Size(
                width = size.width * 0.23f,
                height = size.height * 0.18f
            ),
            cornerRadius = CornerRadius(80f, 80f)
        )

        drawRoundRect(
            color = paleGray,
            topLeft = Offset(
                x = size.width * 0.78f,
                y = size.height * 0.86f
            ),
            size = Size(
                width = size.width * 0.25f,
                height = size.height * 0.18f
            ),
            cornerRadius = CornerRadius(80f, 80f)
        )
    }
}

@Composable
fun PlaceholderButton(text: String) {
    Button(onClick = {}) {
        Text(text = text)
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

        PlaceholderButton(text = "Scan for devices")
        PlaceholderButton(text = "Connect")
        PlaceholderButton(text = "Disconnect")
        PlaceholderButton(text = "Send test command")

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

        PlaceholderButton(text = "Hold near")
        PlaceholderButton(text = "Hold far")
        PlaceholderButton(text = "Resume auto")

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
    profiles: List<Profile>,
    activeProfileId: Int,
    onProfileClick: (Profile) -> Unit,
    onAddProfileClick: (String) -> Unit,
    onDeleteActiveProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var newProfileName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profiles")

        OutlinedTextField(
            value = newProfileName,
            onValueChange = { value ->
                newProfileName = value
            },
            label = {
                Text(text = "Profile name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onAddProfileClick(newProfileName)
                newProfileName = ""
            }
        ) {
            Text(text = "Add profile")
        }

        Button(onClick = onDeleteActiveProfileClick) {
            Text(text = "Delete active profile")
        }

        Button(onClick = onBackClick) {
            Text(text = "Back")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = profiles,
                key = { profile ->
                    profile.id
                }
            ) { profile ->
                Button(
                    onClick = {
                        onProfileClick(profile)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (profile.id == activeProfileId) {
                        Text(text = "${profile.name} · ACTIVE")
                    } else {
                        Text(text = profile.name)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailsScreen(
    modifier: Modifier = Modifier,
    profile: Profile,
    onDeleteProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = profile.name)

        Button(onClick = onDeleteProfileClick) {
            Text(text = "Delete profile")
        }

        Text(text = "Created at: ${profile.createdAt}")

        Text(text = "Diopters")
        Text(text = "Near left: ${profile.nearLeft}")
        Text(text = "Near right: ${profile.nearRight}")
        Text(text = "Far left: ${profile.farLeft}")
        Text(text = "Far right: ${profile.farRight}")

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

        PlaceholderButton(text = "Same value for both eyes")
        PlaceholderButton(text = "Scan prescription card")

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

        PlaceholderButton(text = "Recalibrate head")

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
fun OnboardingScreenPreview() {
    VisionCoreTheme {
        OnboardingScreen(
            onSetUpClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    VisionCoreTheme {
        AppBackground {
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
                onBackClick = {}
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
            BluetoothScreen(onBackClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    VisionCoreTheme {
        AppBackground {
            SettingsScreen(onBackClick = {})
        }
    }
}