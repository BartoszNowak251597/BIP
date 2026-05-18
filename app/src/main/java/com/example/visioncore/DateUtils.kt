package com.example.visioncore

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDateText(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date())
}
