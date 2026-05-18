package com.example.visioncore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VisionCoreLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier.size(160.dp)
        ) {
            val trianglePath = Path().apply {
                moveTo(size.width * 0.10f, size.height * 0.58f)
                lineTo(size.width * 0.48f, size.height * 0.30f)
                lineTo(size.width * 0.56f, size.height * 0.80f)
                close()
            }

            drawPath(
                path = trianglePath,
                color = Color.Black
            )

            drawCircle(
                color = Color.Red,
                radius = size.width * 0.23f,
                center = Offset(
                    x = size.width * 0.73f,
                    y = size.height * 0.56f
                )
            )

            val cutPath = Path().apply {
                moveTo(size.width * 0.73f, size.height * 0.56f)
                lineTo(size.width * 0.98f, size.height * 0.45f)
                lineTo(size.width * 0.98f, size.height * 0.74f)
                close()
            }

            drawPath(
                path = cutPath,
                color = Color.White
            )

            drawCircle(
                color = Color.Black,
                radius = size.width * 0.08f,
                center = Offset(
                    x = size.width * 0.73f,
                    y = size.height * 0.56f
                )
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Vision")
                }

                withStyle(
                    style = SpanStyle(
                        color = Color.Red,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Core")
                }
            },
            fontSize = 30.sp
        )
    }
}
