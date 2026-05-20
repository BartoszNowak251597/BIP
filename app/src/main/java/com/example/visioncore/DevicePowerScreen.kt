package com.example.visioncore

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

 data class CalibrationStepUi(
    val title: String,
    val subtitle: String,
    val description: String,
    val targetTilt: Int
)

@Composable
fun DevicePowerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCompleted: () -> Unit = {},
    onSkipClick: () -> Unit = {},
    onCaptureStep: suspend (stepIndex: Int) -> Boolean = { true }
) {
    val context = LocalContext.current

    val steps = remember {
        listOf(
            CalibrationStepUi(
                title = "Look straight ahead",
                subtitle = "Step 1 of 3",
                description = "Like you're looking into the distance",
                targetTilt = -30
            ),
            CalibrationStepUi(
                title = "Tilt Down",
                subtitle = "Step 2 of 3",
                description = "Like you're reading a book.\nHold still.",
                targetTilt = -30
            ),
            CalibrationStepUi(
                title = "Look around",
                subtitle = "Step 3 of 3",
                description = "Slowly look from left to right.",
                targetTilt = -30
            )
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    var stepStarted by remember { mutableStateOf(false) }
    var isHolding by remember { mutableStateOf(false) }
    var holdSeconds by remember { mutableStateOf(0.0) }
    var currentTilt by remember { mutableStateOf(-23) }
    var captureError by remember { mutableStateOf(false) }

    val completedSteps = remember { mutableStateListOf<Int>() }
    val currentStep = steps[currentStepIndex]
    val currentStepCompleted = currentStepIndex in completedSteps
    val allCompleted = completedSteps.size == steps.size

    LaunchedEffect(currentStepIndex, stepStarted) {
        if (currentStepIndex in completedSteps) {
            isHolding = false
            holdSeconds = 3.0
            return@LaunchedEffect
        }

        if (!stepStarted) {
            isHolding = false
            holdSeconds = 0.0
            return@LaunchedEffect
        }

        isHolding = true
        holdSeconds = 0.0

        repeat(30) {
            delay(100)
            holdSeconds = (it + 1) / 10.0
        }

        isHolding = false

        if (currentStepIndex !in completedSteps) {
            val ok = onCaptureStep(currentStepIndex)
            if (ok) {
                captureError = false
                completedSteps.add(currentStepIndex)
                playCalibrationFinishedFeedback(context)
            } else {
                captureError = true
                stepStarted = false
                holdSeconds = 0.0
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 28.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            title = "Calibration",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "3 short steps so the glasses know how you wear them.",
            fontSize = 12.sp,
            lineHeight = 15.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = index in completedSteps
                val isActive = index == currentStepIndex && !allCompleted

                CalibrationStepCard(
                    title = step.title,
                    subtitle = step.subtitle,
                    description = step.description,
                    isActive = isActive,
                    isCompleted = isCompleted,
                    showDescription = isActive,
                    onClick = {
                        if (index == currentStepIndex || index in completedSteps) {
                            currentStepIndex = index
                            stepStarted = index in completedSteps
                        }
                    }
                )
            }
        }

        if (!allCompleted) {
            Spacer(modifier = Modifier.height(12.dp))

            HoldStatusCard(
                holdSeconds = holdSeconds,
                currentTilt = currentTilt,
                targetTilt = currentStep.targetTilt,
                isHolding = isHolding,
                stepStarted = stepStarted,
                stepCompleted = currentStepCompleted,
                captureError = captureError
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (allCompleted) {
            ContinueButton(
                text = "Continue",
                onClick = onCompleted
            )
        } else {
            BottomActions(
                actionText = when {
                    isHolding -> "Wait"
                    currentStepCompleted -> "Next"
                    else -> "Start"
                },
                actionEnabled = !isHolding,
                onActionClick = {
                    when {
                        currentStepCompleted -> {
                            if (currentStepIndex < steps.lastIndex) {
                                currentStepIndex += 1
                                stepStarted = false
                            }
                        }

                        !stepStarted -> {
                            captureError = false
                            stepStarted = true
                        }
                    }
                }
            )
        }
    }
}

private fun playCalibrationFinishedFeedback(context: Context) {
    vibrateShort(context)
    playShortBeep()
}

private fun vibrateShort(context: Context) {
    try {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    300,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(300)
        }
    } catch (_: Exception) {
        // Ignoring so no crash if vibration is not possible.
    }
}

private fun playShortBeep() {
    try {
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        toneGenerator.startTone(
            ToneGenerator.TONE_PROP_BEEP,
            300
        )

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                toneGenerator.release()
            } catch (_: Exception) {
                // Ignoring release error.
            }
        }, 350)
    } catch (_: Exception) {
        // Ignoring if no sound is accessible this way.
    }
}

@Composable
private fun Header(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "◀",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(end = 10.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CalibrationStepCard(
    title: String,
    subtitle: String,
    description: String,
    isActive: Boolean,
    isCompleted: Boolean,
    showDescription: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive || isCompleted) Color.Black else Color.White
    val contentColor = if (isActive || isCompleted) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )

                    if (showDescription) {
                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = contentColor.copy(alpha = 0.9f)
                        )
                    }
                }

                if (isCompleted) {
                    CheckCircle()
                }
            }

            if (showDescription) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    color = contentColor.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun HoldStatusCard(
    holdSeconds: Double,
    currentTilt: Int,
    targetTilt: Int,
    isHolding: Boolean,
    stepStarted: Boolean,
    stepCompleted: Boolean,
    captureError: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        captureError -> "Not stable — try again"
                        stepCompleted -> "Step captured"
                        isHolding -> "Hold steady..."
                        !stepStarted -> "Ready to start"
                        else -> "Hold steady..."
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${"%.1f".format(holdSeconds)} s / 3.00 s",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text(
                    text = "tilt: $currentTilt",
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.width(28.dp))

                Text(
                    text = "target: $targetTilt",
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun BottomActions(
    actionText: String,
    actionEnabled: Boolean,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BlackPillButton(
            text = actionText,
            enabled = actionEnabled,
            onClick = onActionClick
        )
    }
}

@Composable
private fun ContinueButton(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        BlackPillButton(
            text = text,
            enabled = true,
            onClick = onClick
        )
    }
}

@Composable
private fun RedPillButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(48.dp)
            .width(128.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF1919),
            contentColor = Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "▶",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BlackPillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(48.dp)
            .width(128.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.Black.copy(alpha = 0.35f),
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "▶",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CheckCircle() {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
