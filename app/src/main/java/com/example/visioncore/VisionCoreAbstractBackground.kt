package com.example.visioncore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate

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
