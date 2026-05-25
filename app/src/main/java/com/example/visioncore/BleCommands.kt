package com.example.visioncore

fun Boolean.toModeBytes(): ByteArray = byteArrayOf(
    0x02,
    if (this) 0x00 else 0x01  // auto = 0x00, manual = 0x01
)

fun String.toDeadBatteryBytes(): ByteArray {
    val mode: Byte = when (this) {
        "Lock near"   -> 0x01
        "Lock far"    -> 0x02
        "Neutral 0 D" -> 0x03
        else          -> 0x00  // "Stay in last mode"
    }
    return byteArrayOf(0x03, mode)
}

fun Boolean.toBlinkRedBytes(): ByteArray = byteArrayOf(
    0x06,
    if (this) 0x01 else 0x00
)
