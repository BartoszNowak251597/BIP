package com.example.visioncore

data class SetupConfig(
    val bluetoothDeviceName: String = "",
    val activeProfileName: String = "",
    val nearLeft: String = "+1.50",
    val nearRight: String = "+1.50",
    val farLeft: String = "-2.00",
    val farRight: String = "-2.00",
    val calibrationCompleted: Boolean = false,
    val deadBatteryMode: String = "Stay in last mode"
)