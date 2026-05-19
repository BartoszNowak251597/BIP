package com.example.visioncore.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class RealBluetoothRepository(private val context: Context) : BluetoothRepository {

    // BluetoothAdapter is the entry point to all Bluetooth operations on the device
    private val bluetoothAdapter = context.getSystemService(BluetoothManager::class.java)?.adapter

    // The scanner is a separate object used specifically for BLE scanning
    private val scanner get() = bluetoothAdapter?.bluetoothLeScanner

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BleDevice>> = _scannedDevices

    // SharedFlow is used here instead of StateFlow because incoming data is a stream of events,
    // not a single value with a current state — we don't want new subscribers to replay old bytes
    private val _incomingData = MutableSharedFlow<ByteArray>()
    override val incomingData: Flow<ByteArray> = _incomingData

    // BluetoothGatt represents an active connection to a device.
    // It is the object through which we send and receive all data after connecting.
    private var bluetoothGatt: BluetoothGatt? = null

    // --- Scan ---

    // ScanCallback receives results as the BLE scanner finds nearby advertising devices
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord

            // 1. Szukamy naszego urządzenia po SERVICE_UUID (to jest w 100% pewne)
            val uuids = scanRecord?.serviceUuids
            val hasOurService = uuids?.any { it.uuid == SERVICE_UUID } == true

            // 2. Próbujemy pobrać nazwę, ale NIE UŻYWAMY "return" jeśli jej nie ma!
            val rawName = result.device.name ?: scanRecord?.deviceName
            val isOurName = rawName?.startsWith("VisionCore") == true

            // 3. Akceptujemy urządzenie, jeśli zgadza się UUID LUB nazwa
            if (!hasOurService && !isOurName) return

            // 4. Jeśli pakiet nie miał nazwy, ale miał nasz UUID, wiemy, że to VisionCore
            val finalName = rawName ?: "Losowa Nazwa"
            val device = BleDevice(name = finalName, address = result.device.address)

            val current = _scannedDevices.value
            if (current.none { it.address == device.address }) {
                _scannedDevices.value = current + device
            }
        }

        override fun onScanFailed(errorCode: Int) {
            android.util.Log.e("BLE", "Scan failed with error code: $errorCode")
            _connectionState.value = ConnectionState.Disconnected
        }
    }
    // --- GATT callbacks ---

    // BluetoothGattCallback is how Android delivers all BLE events to us after connecting.
    // Every interaction with the device (connecting, discovering services, receiving data)
    // results in one of these callbacks being called.
    private val gattCallback = object : BluetoothGattCallback() {

        // Called when the connection state changes (connected / disconnected)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = ConnectionState.Connected
                    // After connecting we must discover services before we can do anything.
                    // This asks the device: "what services and characteristics do you have?"
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    // Always close the GATT object when disconnected to free system resources
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        // Called when service discovery completes — now we know what the device can do
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            // Find our State characteristic and subscribe to notifications.
            // Without this, the device will never push data to us.
            val characteristic = gatt.getService(SERVICE_UUID)
                ?.getCharacteristic(STATE_UUID) ?: return

            // Step 1: tell the local Bluetooth stack to forward notifications to our callback
            gatt.setCharacteristicNotification(characteristic, true)

            // Step 2: write to the CCCD descriptor on the device itself.
            // CCCD (Client Characteristic Configuration Descriptor) is a standard BLE mechanism —
            // writing ENABLE_NOTIFICATION_VALUE to it tells the device to start sending updates.
            val descriptor = characteristic.getDescriptor(CCCD_UUID) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                gatt.writeDescriptor(descriptor)
            }
        }

        // Called on API 33+ when the device pushes a notification to us
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            _incomingData.tryEmit(value)
        }

        // Called on API < 33 when the device pushes a notification to us
        @Deprecated("Used on API < 33")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            @Suppress("DEPRECATION")
            _incomingData.tryEmit(characteristic.value)
        }
    }

    // --- Public API ---

    private fun requireBluetoothEnabled() {
        if (bluetoothAdapter?.isEnabled != true) throw BluetoothDisabledException()
    }

    @SuppressLint("MissingPermission")
    override fun startScan() {
        requireBluetoothEnabled()
        _scannedDevices.value = emptyList()
        _connectionState.value = ConnectionState.Scanning
        scanner?.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        scanner?.stopScan(scanCallback)
        if (_connectionState.value == ConnectionState.Scanning) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(device: BleDevice) {
        requireBluetoothEnabled()
        stopScan()
        _connectionState.value = ConnectionState.Connecting
        val btDevice = bluetoothAdapter?.getRemoteDevice(device.address) ?: return
        // TRANSPORT_LE forces BLE even if the device also supports Classic Bluetooth
        bluetoothGatt = btDevice.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    override suspend fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    @SuppressLint("MissingPermission")
    override suspend fun sendBytes(bytes: ByteArray) {
        requireBluetoothEnabled()
        val gatt = bluetoothGatt ?: return
        val characteristic = gatt.getService(SERVICE_UUID)
            ?.getCharacteristic(COMMAND_UUID) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(characteristic, bytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        } else {
            @Suppress("DEPRECATION")
            characteristic.value = bytes
            @Suppress("DEPRECATION")
            gatt.writeCharacteristic(characteristic)
        }
    }

    // Call this when the app is done with Bluetooth (e.g. onDestroy) to clean up resources
    fun close() {
        stopScan()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    companion object {
        // TODO: replace with your actual device UUIDs once the firmware defines them
        val SERVICE_UUID: UUID = UUID.fromString("ba89a69d-5c63-4e5b-87e6-3b4d42273fe5")
        val COMMAND_UUID: UUID  = UUID.fromString("aad23908-258f-4d17-83b5-e7a6448a022e")
        val STATE_UUID: UUID    = UUID.fromString("fff1224f-4294-410f-b8c8-99e0c67ced6e")

        // Standard BLE UUID that every notify-capable characteristic has — do not change this
        val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
