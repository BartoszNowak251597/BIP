package com.example.visioncore

data class Profile(
    val id: Int,
    val name: String,
    val createdAt: String,
    val nearLeft: String = "+1.50",
    val nearRight: String = "+1.75",
    val farLeft: String = "-2.00",
    val farRight: String = "-2.25",
    val calibrationCompleted: Boolean = false
)

fun Profile.toPrescriptionBytes(): ByteArray {
    fun encode(s: String) = (s.toFloat() * 4).toInt().toByte()
    return byteArrayOf(
        0x01,
        encode(nearLeft),
        encode(nearRight),
        encode(farLeft),
        encode(farRight)
    )
}