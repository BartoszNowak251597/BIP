package com.example.visioncore.bluetooth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class BleDevice(
    val name: String,
    val address: String
)

enum class ConnectionState {
    Disconnected,
    Scanning,
    Connecting,
    Connected
}

interface BluetoothRepository {

    // Current connection status — UI observes this to react to state changes
    val connectionState: StateFlow<ConnectionState>

    // List of discovered devices during a scan
    val scannedDevices: StateFlow<List<BleDevice>>

    // Raw bytes pushed from the device (e.g. lens state, battery level)
    // Upper layers subscribe to this and interpret the bytes themselves
    val incomingData: Flow<ByteArray>

    fun startScan()
    fun stopScan()

    suspend fun connect(device: BleDevice)
    suspend fun disconnect()

    // Send a raw byte array to the device — callers decide what the bytes mean
    suspend fun sendBytes(bytes: ByteArray)
}
