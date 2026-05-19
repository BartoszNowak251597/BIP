package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class ManualTab {
    Now,
    Profiles,
    Device
}

private enum class ManualMode {
    Near,
    Far,
    Off
}

private fun bgColor(darkMode: Boolean): Color {
    return if (darkMode) Color.Black else Color.White
}

private fun fgColor(darkMode: Boolean): Color {
    return if (darkMode) Color.White else Color.Black
}

private fun selectedBgColor(darkMode: Boolean): Color {
    return if (darkMode) Color.White else Color.Black
}

private fun selectedFgColor(darkMode: Boolean): Color {
    return if (darkMode) Color.Black else Color.White
}

@Composable
fun ManualOverrideScreen(
    modifier: Modifier = Modifier,
    setupConfig: SetupConfig = SetupConfig(),
    profiles: List<Profile> = defaultManualProfiles(),
    activeProfileId: Int = profiles.firstOrNull()?.id ?: 0,
    onProfileSelected: (Profile) -> Unit = {},
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(ManualTab.Now) }
    var selectedMode by remember { mutableStateOf(ManualMode.Near) }
    var selectedProfileId by remember { mutableStateOf(activeProfileId) }
    var deadBatteryMode by remember { mutableStateOf(setupConfig.deadBatteryMode.ifBlank { "Stay in last mode" }) }
    var blinkRedBeforeLowBattery by remember { mutableStateOf(true) }
    var darkColorMode by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = bgColor(darkColorMode),
        contentColor = fgColor(darkColorMode)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor(darkColorMode))
        ) {
            ManualTabs(
                selectedTab = selectedTab,
                darkMode = darkColorMode,
                onTabSelected = { selectedTab = it }
            )

            when (selectedTab) {
                ManualTab.Now -> {
                    NowTab(
                        setupConfig = setupConfig,
                        selectedMode = selectedMode,
                        darkMode = darkColorMode,
                        onModeSelected = { selectedMode = it }
                    )
                }

                ManualTab.Profiles -> {
                    ProfilesTab(
                        profiles = profiles,
                        activeProfileId = selectedProfileId,
                        darkMode = darkColorMode,
                        onProfileClick = { profile ->
                            selectedProfileId = profile.id
                            onProfileSelected(profile)
                        }
                    )
                }

                ManualTab.Device -> {
                    DeviceTab(
                        setupConfig = setupConfig,
                        deadBatteryMode = deadBatteryMode,
                        onDeadBatteryModeSelected = { deadBatteryMode = it },
                        blinkRedBeforeLowBattery = blinkRedBeforeLowBattery,
                        onBlinkRedBeforeLowBatteryChange = { blinkRedBeforeLowBattery = it },
                        darkColorMode = darkColorMode,
                        onDarkColorModeChange = { darkColorMode = it },
                        onRecalibrateClick = onBackClick,
                        onDioptriesClick = onBackClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualTabs(
    selectedTab: ManualTab,
    darkMode: Boolean,
    onTabSelected: (ManualTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(bgColor(darkMode))
            .border(BorderStroke(1.dp, fgColor(darkMode))),
        verticalAlignment = Alignment.Bottom
    ) {
        ManualTabButton(
            text = "now",
            selected = selectedTab == ManualTab.Now,
            darkMode = darkMode,
            onClick = { onTabSelected(ManualTab.Now) },
            modifier = Modifier.weight(1f)
        )

        ManualTabButton(
            text = "profiles",
            selected = selectedTab == ManualTab.Profiles,
            darkMode = darkMode,
            onClick = { onTabSelected(ManualTab.Profiles) },
            modifier = Modifier.weight(1f)
        )

        ManualTabButton(
            text = "device",
            selected = selectedTab == ManualTab.Device,
            darkMode = darkMode,
            onClick = { onTabSelected(ManualTab.Device) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ManualTabButton(
    text: String,
    selected: Boolean,
    darkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .background(if (selected) selectedBgColor(darkMode) else bgColor(darkMode))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) selectedFgColor(darkMode) else fgColor(darkMode)
        )
    }
}

@Composable
private fun NowTab(
    setupConfig: SetupConfig,
    selectedMode: ManualMode,
    darkMode: Boolean,
    onModeSelected: (ManualMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor(darkMode))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CURRENT MODE",
                        fontSize = 12.sp,
                        color = fgColor(darkMode)
                    )

                    Text(
                        text = selectedMode.label(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = fgColor(darkMode)
                    )
                }

                AutoToggleBadge(darkMode = darkMode)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedMode.valuesText(setupConfig),
                    fontSize = 14.sp,
                    color = fgColor(darkMode),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "1 sec ago",
                    fontSize = 12.sp,
                    color = fgColor(darkMode)
                )
            }
        }

        DividerLine(darkMode = darkMode)

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoBox(
                    title = "Distance:",
                    value = "42 cm",
                    selected = true,
                    darkMode = darkMode,
                    modifier = Modifier.weight(1f)
                )

                InfoBox(
                    title = "Tilt:",
                    value = "-24",
                    selected = false,
                    darkMode = darkMode,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoBox(
                    title = "Battery:",
                    value = "78 %",
                    selected = false,
                    darkMode = darkMode,
                    modifier = Modifier.weight(1f)
                )

                InfoBox(
                    title = "Today:",
                    value = "2 h 45 m",
                    selected = false,
                    highlighted = true,
                    darkMode = darkMode,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        DividerLine(darkMode = darkMode)

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "MANUAL OVERRIDE",
                fontSize = 12.sp,
                color = fgColor(darkMode)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModeButton(
                    text = "NEAR",
                    selected = selectedMode == ManualMode.Near,
                    darkMode = darkMode,
                    onClick = { onModeSelected(ManualMode.Near) },
                    modifier = Modifier.weight(1f)
                )

                ModeButton(
                    text = "FAR",
                    selected = selectedMode == ManualMode.Far,
                    darkMode = darkMode,
                    onClick = { onModeSelected(ManualMode.Far) },
                    modifier = Modifier.weight(1f)
                )

                ModeButton(
                    text = "OFF",
                    selected = selectedMode == ManualMode.Off,
                    darkMode = darkMode,
                    onClick = { onModeSelected(ManualMode.Off) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ProfilesTab(
    profiles: List<Profile>,
    activeProfileId: Int,
    darkMode: Boolean,
    onProfileClick: (Profile) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor(darkMode))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp)
        ) {
            Text(
                text = "${profiles.size} profiles found.",
                fontSize = 13.sp,
                color = fgColor(darkMode)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CreateProfileButton(darkMode = darkMode)

            Spacer(modifier = Modifier.height(20.dp))
        }

        DividerLine(darkMode = darkMode)

        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            profiles.forEach { profile ->
                ProfileRowCard(
                    profile = profile,
                    active = profile.id == activeProfileId,
                    darkMode = darkMode,
                    onClick = { onProfileClick(profile) }
                )
            }
        }
    }
}

@Composable
private fun DeviceTab(
    setupConfig: SetupConfig,
    deadBatteryMode: String,
    onDeadBatteryModeSelected: (String) -> Unit,
    blinkRedBeforeLowBattery: Boolean,
    onBlinkRedBeforeLowBatteryChange: (Boolean) -> Unit,
    darkColorMode: Boolean,
    onDarkColorModeChange: (Boolean) -> Unit,
    onRecalibrateClick: () -> Unit,
    onDioptriesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor(darkColorMode))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        DeviceCard(
            deviceName = setupConfig.bluetoothDeviceName.ifBlank { "VisionCore-A2FI" },
            darkMode = darkColorMode
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DEAD BATTERY MODES",
            fontSize = 12.sp,
            color = fgColor(darkColorMode)
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeviceOptionButton(
            text = "Stay in last mode",
            selected = deadBatteryMode == "Stay in last mode",
            darkMode = darkColorMode,
            onClick = { onDeadBatteryModeSelected("Stay in last mode") }
        )

        DeviceOptionButton(
            text = "Lock near",
            selected = deadBatteryMode == "Lock near",
            darkMode = darkColorMode,
            onClick = { onDeadBatteryModeSelected("Lock near") }
        )

        DeviceOptionButton(
            text = "Lock far",
            selected = deadBatteryMode == "Lock far",
            highlighted = true,
            darkMode = darkColorMode,
            onClick = { onDeadBatteryModeSelected("Lock far") }
        )

        DeviceOptionButton(
            text = "Neutral 0 D",
            selected = deadBatteryMode == "Neutral 0 D",
            darkMode = darkColorMode,
            onClick = { onDeadBatteryModeSelected("Neutral 0 D") }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "OTHER",
            fontSize = 12.sp,
            color = fgColor(darkColorMode)
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeviceNavigationButton(
            text = "Recalibrate head",
            darkMode = darkColorMode,
            onClick = onRecalibrateClick
        )

        DeviceNavigationButton(
            text = "Dioptries",
            darkMode = darkColorMode,
            onClick = onDioptriesClick
        )

        DeviceSwitchRow(
            text = "Blink red before 15 %",
            checked = blinkRedBeforeLowBattery,
            highlighted = true,
            darkMode = darkColorMode,
            onCheckedChange = onBlinkRedBeforeLowBatteryChange
        )

        DeviceSwitchRow(
            text = "Dark color mode",
            checked = darkColorMode,
            darkMode = darkColorMode,
            onCheckedChange = onDarkColorModeChange
        )
    }
}

@Composable
private fun AutoToggleBadge(
    darkMode: Boolean
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(selectedBgColor(darkMode))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "auto:",
            color = selectedFgColor(darkMode),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "on",
            color = selectedFgColor(darkMode),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    selected: Boolean,
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) selectedBgColor(darkMode) else bgColor(darkMode))
            .border(
                width = 2.dp,
                color = if (highlighted) Color(0xFFFF1919) else fgColor(darkMode),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = if (selected) selectedFgColor(darkMode) else fgColor(darkMode),
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = if (selected) selectedFgColor(darkMode) else fgColor(darkMode),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ModeButton(
    text: String,
    selected: Boolean,
    darkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, fgColor(darkMode)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) selectedBgColor(darkMode) else bgColor(darkMode),
            contentColor = if (selected) selectedFgColor(darkMode) else fgColor(darkMode)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CreateProfileButton(
    darkMode: Boolean
) {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, fgColor(darkMode)),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor(darkMode),
            contentColor = fgColor(darkMode)
        )
    ) {
        Text(
            text = "☻",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = "Create new profile",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileRowCard(
    profile: Profile,
    active: Boolean,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (profile.name.contains("Mum", ignoreCase = true)) {
        Color(0xFFFF1919)
    } else {
        fgColor(darkMode)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 2.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (active) selectedBgColor(darkMode) else bgColor(darkMode)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(if (active) selectedBgColor(darkMode) else bgColor(darkMode))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "♙",
                fontSize = 24.sp,
                color = if (active) selectedFgColor(darkMode) else fgColor(darkMode)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (active) selectedFgColor(darkMode) else fgColor(darkMode)
                )

                Text(
                    text = "NEAR  L/R ${profile.nearLeft}/${profile.nearRight}",
                    fontSize = 9.sp,
                    color = if (active) selectedFgColor(darkMode) else fgColor(darkMode)
                )

                Text(
                    text = "FAR     L/R ${profile.farLeft}/${profile.farRight}",
                    fontSize = 9.sp,
                    color = if (active) selectedFgColor(darkMode) else fgColor(darkMode)
                )
            }

            if (active) {
                Text(
                    text = "ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = selectedFgColor(darkMode)
                )
            } else {
                Text(
                    text = "▶",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = fgColor(darkMode)
                )
            }
        }
    }
}

@Composable
private fun DeviceCard(
    deviceName: String,
    darkMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, fgColor(darkMode)),
        colors = CardDefaults.cardColors(containerColor = bgColor(darkMode)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor(darkMode))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(42.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◀●",
                    color = Color(0xFFFF1919),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deviceName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = fgColor(darkMode)
                )

                Text(
                    text = "Connected",
                    fontSize = 10.sp,
                    color = fgColor(darkMode)
                )

                Text(
                    text = "Firmware 4.0.1",
                    fontSize = 10.sp,
                    color = fgColor(darkMode)
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, fgColor(darkMode), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙",
                    fontSize = 16.sp,
                    color = fgColor(darkMode)
                )
            }
        }
    }
}

@Composable
private fun DeviceOptionButton(
    text: String,
    selected: Boolean,
    highlighted: Boolean = false,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(bottom = 6.dp),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (highlighted) Color(0xFFFF1919) else fgColor(darkMode)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) selectedBgColor(darkMode) else bgColor(darkMode),
            contentColor = if (selected) selectedFgColor(darkMode) else fgColor(darkMode)
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeviceNavigationButton(
    text: String,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(bottom = 6.dp),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(2.dp, fgColor(darkMode)),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor(darkMode),
            contentColor = fgColor(darkMode)
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "▶",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeviceSwitchRow(
    text: String,
    checked: Boolean,
    highlighted: Boolean = false,
    darkMode: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor(darkMode))
            .border(
                width = 2.dp,
                color = if (highlighted) Color(0xFFFF1919) else fgColor(darkMode),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(start = 18.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = fgColor(darkMode),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(width = 44.dp, height = 24.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = selectedFgColor(darkMode),
                checkedTrackColor = selectedBgColor(darkMode),
                uncheckedThumbColor = selectedFgColor(darkMode),
                uncheckedTrackColor = selectedBgColor(darkMode),
                uncheckedBorderColor = fgColor(darkMode)
            )
        )
    }
}

@Composable
private fun DividerLine(
    darkMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(fgColor(darkMode))
    )
}

private fun ManualMode.label(): String {
    return when (this) {
        ManualMode.Near -> "NEAR"
        ManualMode.Far -> "FAR"
        ManualMode.Off -> "OFF"
    }
}

private fun ManualMode.valuesText(setupConfig: SetupConfig): String {
    return when (this) {
        ManualMode.Near -> "L ${setupConfig.nearLeft} / R ${setupConfig.nearRight}"
        ManualMode.Far -> "L ${setupConfig.farLeft} / R ${setupConfig.farRight}"
        ManualMode.Off -> "L 0.00 / R 0.00"
    }
}

private fun defaultManualProfiles(): List<Profile> {
    return listOf(
        Profile(
            id = 1,
            name = "Jacob",
            createdAt = "Today",
            nearLeft = "+1.50",
            nearRight = "+1.50",
            farLeft = "-2.00",
            farRight = "-2.00"
        ),
        Profile(
            id = 2,
            name = "Sasha",
            createdAt = "Today",
            nearLeft = "+1.50",
            nearRight = "+1.50",
            farLeft = "-2.00",
            farRight = "-2.00"
        ),
        Profile(
            id = 3,
            name = "Mum",
            createdAt = "Today",
            nearLeft = "+1.50",
            nearRight = "+1.50",
            farLeft = "-2.00",
            farRight = "-2.00"
        )
    )
}