package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    onManualOverrideClick: () -> Unit,
    onProfilesClick: () -> Unit,
    onPrescriptionClick: () -> Unit,
    onDevicePowerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBluetoothClick: () -> Unit
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "◀",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Setting up",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            SetupItem(
                number = 1,
                title = "Pair the glasses",
                subtitle = "Bluetooth",
                completed = true,
                onClick = onBluetoothClick
            )

            Spacer(Modifier.height(10.dp))

            SetupItem(
                number = 2,
                title = "Add wearer profile",
                subtitle = "Name, Age",
                highlighted = true,
                showOpenButton = true,
                onClick = onProfilesClick
            )

            Spacer(Modifier.height(10.dp))

            SetupItem(
                number = 3,
                title = "Enter your dioptries",
                subtitle = "Near, Far",
                onClick = onPrescriptionClick
            )

            Spacer(Modifier.height(10.dp))

            SetupItem(
                number = 4,
                title = "Calibrate head position",
                subtitle = "Look ahead, look down",
                highlighted = true,
                onClick = onDevicePowerClick
            )

            Spacer(Modifier.height(10.dp))

            SetupItem(
                number = 5,
                title = "Pick dead battery mode",
                subtitle = "Optional",
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

                    Spacer(
                        Modifier.width(10.dp)
                    )

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
            color = if (highlighted)
                Color.Red
            else
                Color.Black
        ),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (completed)
                            Color.Black
                        else
                            Color.White
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (completed) "✓" else "$number",
                    color = if (completed)
                        Color.White
                    else
                        Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

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