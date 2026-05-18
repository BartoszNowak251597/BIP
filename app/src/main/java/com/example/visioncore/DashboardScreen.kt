package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Setting up",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SetupItem(
                number = 1,
                title = "Pair the glasses",
                subtitle = "Bluetooth",
                completed = bluetoothCompleted,
                highlighted = activeStep == 1,
                showOpenButton = activeStep == 1,
                onClick = onBluetoothClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 2,
                title = "Add wearer profile",
                subtitle = "Name, Age",
                completed = profileCompleted,
                highlighted = activeStep == 2,
                showOpenButton = activeStep == 2,
                onClick = onProfilesClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 3,
                title = "Enter your dioptries",
                subtitle = "Near, Far",
                completed = prescriptionCompleted,
                highlighted = activeStep == 3,
                showOpenButton = activeStep == 3,
                onClick = onPrescriptionClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 4,
                title = "Calibrate head position",
                subtitle = "Look ahead, look down",
                completed = calibrationCompleted,
                highlighted = activeStep == 4,
                showOpenButton = activeStep == 4,
                onClick = onDevicePowerClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SetupItem(
                number = 5,
                title = "Pick dead battery mode",
                subtitle = "Optional",
                completed = settingsCompleted,
                highlighted = activeStep == 5,
                showOpenButton = activeStep == 5,
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onManualOverrideClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier
                        .fillMaxWidth(.55f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Skip rest",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "▶ ▶",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SetupItem(
    number: Int,
    title: String,
    subtitle: String,
    completed: Boolean = false,
    highlighted: Boolean = false,
    showOpenButton: Boolean = false,
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
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            if (showOpenButton) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "Open",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "▶",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
