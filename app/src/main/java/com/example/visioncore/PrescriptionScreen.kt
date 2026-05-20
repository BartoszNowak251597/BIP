package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun PrescriptionScreen(
    modifier: Modifier = Modifier,
    initialNearLeft: String = "+1.50",
    initialNearRight: String = "+1.75",
    initialFarLeft: String = "-2.00",
    initialFarRight: String = "-2.25",
    onBackClick: () -> Unit,
    onContinueClick: (String, String, String, String) -> Unit
) {
    var sameEyes by remember { mutableStateOf(false) }

    var nearLeft by remember {
        mutableFloatStateOf(parseDiopter(initialNearLeft, 1.50f))
    }

    var nearRight by remember {
        mutableFloatStateOf(parseDiopter(initialNearRight, 1.75f))
    }

    var farLeft by remember {
        mutableFloatStateOf(parseDiopter(initialFarLeft, -2.00f))
    }

    var farRight by remember {
        mutableFloatStateOf(parseDiopter(initialFarRight, -2.25f))
    }

    LaunchedEffect(initialNearLeft, initialNearRight, initialFarLeft, initialFarRight) {
        nearLeft = parseDiopter(initialNearLeft, 1.50f)
        nearRight = parseDiopter(initialNearRight, 1.75f)
        farLeft = parseDiopter(initialFarLeft, -2.00f)
        farRight = parseDiopter(initialFarRight, -2.25f)
    }

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
                .navigationBarsPadding()
        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "From your last eye exam.\nRange -6 to +3.",
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
                    modifier = Modifier.height(32.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onContinueClick(
                            formatDiopter(nearLeft),
                            formatDiopter(nearRight),
                            formatDiopter(farLeft),
                            formatDiopter(farRight)
                        )
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
                                (value - 0.25f)
                                    .coerceAtLeast(-6f)
                            )
                        }
                        .padding(8.dp)
                )

                Text(
                    text = formatDiopter(value),
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
                                (value + 0.25f)
                                    .coerceAtMost(3f)
                            )
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun parseDiopter(
    value: String,
    fallback: Float
): Float {
    return value
        .replace(",", ".")
        .toFloatOrNull()
        ?: fallback
}

private fun formatDiopter(value: Float): String {
    return String.format(
        Locale.US,
        "%+.2f",
        value
    )
}