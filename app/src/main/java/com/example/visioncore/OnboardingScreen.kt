package com.example.visioncore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
