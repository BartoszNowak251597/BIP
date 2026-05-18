package com.example.visioncore

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PlaceholderButton(text: String) {
    Button(onClick = {}) {
        Text(text = text)
    }
}
