package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrescriptionScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {

    var sameEyes by remember { mutableStateOf(false) }

    var nearLeft by remember { mutableStateOf(1.50f) }
    var nearRight by remember { mutableStateOf(1.75f) }

    var farLeft by remember { mutableStateOf(-2.00f) }
    var farRight by remember { mutableStateOf(-2.35f) }

    LaunchedEffect(sameEyes, nearLeft, farLeft) {
        if (sameEyes) {
            nearRight = nearLeft
            farRight = farLeft
        }
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

            // HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Text(
                        text = "◀",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Dioptries",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "From your last eye exam. Range -6 to +3.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            EyeSection(
                title = "Near/Reading",
                leftValue = nearLeft,
                rightValue = nearRight,
                onLeftChange = {
                    nearLeft = it
                },
                onRightChange = {
                    if (!sameEyes) {
                        nearRight = it
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            EyeSection(
                title = "Far/Distance",
                leftValue = farLeft,
                rightValue = farRight,
                onLeftChange = {
                    farLeft = it
                },
                onRightChange = {
                    if (!sameEyes) {
                        farRight = it
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Switch(
                    checked = sameEyes,
                    onCheckedChange = {
                        sameEyes = it
                    }
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Same for both eyes",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // CONTINUE BUTTON
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    onClick = {
                        onContinueClick()
                    },
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

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "▶",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EyeSection(
    title: String,
    leftValue: Float,
    rightValue: Float,
    onLeftChange: (Float) -> Unit,
    onRightChange: (Float) -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(
            2.dp,
            Color.Black
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                EyeValuePicker(
                    label = "L OS",
                    value = leftValue,
                    onValueChange = onLeftChange
                )

                EyeValuePicker(
                    label = "R OD",
                    value = rightValue,
                    onValueChange = onRightChange
                )
            }
        }
    }
}

@Composable
fun EyeValuePicker(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Surface(
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                2.dp,
                Color.Black
            ),
            color = Color.White
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "-",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onValueChange(
                                (value - 0.01f)
                                    .coerceAtLeast(-6f)
                            )
                        }
                        .padding(8.dp)
                )

                Text(
                    text = String.format("%+.2f", value),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )

                Text(
                    text = "+",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onValueChange(
                                (value + 0.01f)
                                    .coerceAtMost(3f)
                            )
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}