package com.example.visioncore.bluetooth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeBluetoothRepository : BluetoothRepository {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BleDevice>> = _scannedDevices

    private val _incomingData = MutableSharedFlow<ByteArray>()
    override val incomingData: Flow<ByteArray> = _incomingData

    private val fakeDevices = listOf(
        BleDevice(name = "VisionCore-L1", address = "AA:BB:CC:DD:EE:01", rssi = -42),
        BleDevice(name = "VisionCore-L2", address = "AA:BB:CC:DD:EE:02", rssi = -61),
    )

    override fun startScan() {
        _connectionState.value = ConnectionState.Scanning
        _scannedDevices.value = fakeDevices
    }

    override fun stopScan() {
        if (_connectionState.value == ConnectionState.Scanning) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    override suspend fun connect(device: BleDevice) {
        _connectionState.value = ConnectionState.Connecting
        delay(1000)
        _connectionState.value = ConnectionState.Connected
    }

    override suspend fun disconnect() {
        delay(300)
        _connectionState.value = ConnectionState.Disconnected
        _scannedDevices.value = emptyList()
    }

    override suspend fun sendBytes(bytes: ByteArray) {
        delay(100)
        // Simulate the device echoing the bytes back so we can test incomingData subscribers
        _incomingData.emit(bytes)
    }
}
