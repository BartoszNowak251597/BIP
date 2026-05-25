package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    bluetoothCompleted: Boolean,
    profileCompleted: Boolean,
    prescriptionCompleted: Boolean,
    calibrationCompleted: Boolean,
    settingsCompleted: Boolean,
    onManualOverrideClick: () -> Unit,
    onProfilesClick: () -> Unit,
    onPrescriptionClick: () -> Unit,
    onDevicePowerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBluetoothClick: () -> Unit
) {
    val firstFourCompleted =
        bluetoothCompleted &&
                profileCompleted &&
                prescriptionCompleted &&
                calibrationCompleted

    val allSetupCompleted = firstFourCompleted && settingsCompleted

    val activeStep = when {
        !bluetoothCompleted -> 1
        !profileCompleted -> 2
        !prescriptionCompleted -> 3
        !calibrationCompleted -> 4
        !settingsCompleted -> 5
        else -> 0
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◀",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF30313A)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Setting up",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF30313A)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SetupItem(
                number = 1,
                title = "Pair the glasses",
                subtitle = "Bluetooth",
                completed = bluetoothCompleted,
                highlighted = activeStep == 1,
                onClick = onBluetoothClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 2,
                title = "Add wearer profile",
                subtitle = "Name, Age",
                completed = profileCompleted,
                highlighted = activeStep == 2,
                onClick = onProfilesClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 3,
                title = "Enter your dioptries",
                subtitle = "Near, Far",
                completed = prescriptionCompleted,
                highlighted = activeStep == 3,
                onClick = onPrescriptionClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 4,
                title = "Calibrate head position",
                subtitle = "Look ahead, look down",
                completed = calibrationCompleted,
                highlighted = activeStep == 4,
                onClick = onDevicePowerClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 5,
                title = "Pick dead battery mode",
                subtitle = "Optional",
                completed = settingsCompleted,
                highlighted = activeStep == 5,
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SetupBottomButton(
                    text = if (allSetupCompleted) "Continue" else "Skip rest",
                    isPrimary = allSetupCompleted,
                    enabled = firstFourCompleted,
                    onClick = onManualOverrideClick
                )
            }
        }
    }
}

@Composable
private fun SetupBottomButton(
    text: String,
    isPrimary: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color.Black else Color(0xFFFF1919),
            contentColor = if (isPrimary) Color.White else Color.Black,
            disabledContainerColor = if (isPrimary) {
                Color.Black.copy(alpha = 0.35f)
            } else {
                Color(0xFFFF1919).copy(alpha = 0.35f)
            },
            disabledContentColor = if (isPrimary) {
                Color.White
            } else {
                Color.Black.copy(alpha = 0.45f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth(.55f)
            .height(48.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = if (isPrimary) "▶" else "▶ ▶",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SetupItem(
    number: Int,
    title: String,
    subtitle: String,
    completed: Boolean = false,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (highlighted) 2.dp else 1.dp,
            color = if (highlighted) Color.Red else Color.Black
        ),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (completed) Color.Black else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (completed) "✓" else "$number",
                    color = if (completed) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF30313A)
                )

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(72.dp)
                    .height(28.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}