package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun ModeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        border = BorderStroke(2.dp, Color.Black),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Color.Black else Color.Transparent,
            contentColor = if (selected) Color.White else Color.Black
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    var selectedOption by remember { mutableStateOf("Stay in last mode") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Text(
                        text = "◀ ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Battery mode",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    fontSize = 35.sp,
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Dead Battery Mode keeps essential vision setting active to maintain basic usability, when the battery is low or fully drained.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(80.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    ModeButton(
                        text = "Stay in last mode",
                        selected = selectedOption == "Stay in last mode",
                        onClick = { selectedOption = "Stay in last mode" }
                    )

                    ModeButton(
                        text = "Lock near",
                        selected = selectedOption == "Lock near",
                        onClick = { selectedOption = "Lock near" }
                    )

                    ModeButton(
                        text = "Lock far",
                        selected = selectedOption == "Lock far",
                        onClick = { selectedOption = "Lock far" }
                    )

                    ModeButton(
                        text = "Neutral 0 D",
                        selected = selectedOption == "Neutral 0 D",
                        onClick = { selectedOption = "Neutral 0 D" }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(52.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "▶",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}