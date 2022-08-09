package com.medina.juanantonio.bluetoothrelaycontroller.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.medina.juanantonio.bluetoothrelaycontroller.ble.models.BleConnection
import com.medina.juanantonio.bluetoothrelaycontroller.ble.models.JDY23BleConnection
import com.medina.juanantonio.bluetoothrelaycontroller.data.models.BleDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.concurrent.schedule

@SuppressLint("MissingPermission")
class BluetoothLEManager(
    private val context: Context
) : IBluetoothLEManager, BluetoothGattCallback() {

    companion object {
        const val JDY23_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"
        const val JDY23_WRITE_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"

        const val TAG = "BluetoothLEManager"
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    override lateinit var leScanCallBack: BluetoothLeScanCallBack
    private var verifyBleDeviceTask: TimerTask? = null
    private var deviceBeingEdited = false

    private val scanSettings =
        ScanSettings
            .Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

    private val jdy23ScanFilter =
        ScanFilter
            .Builder()
            .setServiceUuid(ParcelUuid.fromString(JDY23_SERVICE_UUID))
            .build()

    internal val bleConnectionHashMap = HashMap<String, BleConnection>()
    private var bluetoothLEManagerScope = CoroutineScope(Default)

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val macAddress = result?.device?.address ?: return
            Log.d(TAG, "Scanned $callbackType $macAddress")

            if (bleDeviceHashMap.containsKey(macAddress)) {
                bleDeviceHashMap[macAddress]?.lastSeen = System.currentTimeMillis()
                createBleConnection(macAddress)
                return
            }
            if (result.device == null) return

            bluetoothLEManagerScope.launch {
                try {
                    val bleDevice = BleDevice(
                        name = result.device.name ?: macAddress,
                        alias = ""
                    ).apply {
                        this.macAddress = macAddress
                        lastSeen = System.currentTimeMillis()
                    }

                    bleDeviceHashMap[macAddress] = bleDevice
                    createBleConnection(macAddress)

                    refreshDeviceList()
                } catch (e: Exception) {
                    Log.d(TAG, "$e")
                }
            }
        }
    }

    override var scanning = false
    override val bleDeviceHashMap = HashMap<String, BleDevice>()

    override fun isBluetoothAvailable(): Boolean = bluetoothAdapter != null

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    override fun startScan(_leScanCallBack: BluetoothLeScanCallBack) {
        scanning = true

        if (!::leScanCallBack.isInitialized) leScanCallBack = _leScanCallBack
        refreshDeviceList()
        bluetoothLeScanner?.startScan(
            listOf(jdy23ScanFilter),
            scanSettings,
            scanCallback
        )
        resetVerifyBleDevicesTask()

        Log.d(TAG, "BLE Scanner started Scanning - $bluetoothLeScanner")
    }

    override fun stopScan() {
        scanning = false
        verifyBleDeviceTask = null
        if (BluetoothAdapter.getDefaultAdapter().state == STATE_ON) {
            bluetoothLeScanner?.stopScan(scanCallback)
            Log.d(TAG, "BLE Scanner stopped Scanning - $bluetoothLeScanner")
        }
    }

    override fun createBleConnection(address: String) {
        Log.d(TAG, "$address createBleConnection")
        if (bleConnectionHashMap.containsKey(address)) return

        val device = bluetoothAdapter.getRemoteDevice(address)
        val bleConnection =
            JDY23BleConnection(
                context,
                device,
                BluetoothDeviceGattCallback()
            )
        bleConnectionHashMap[address] = bleConnection

        val bleDevice = bleDeviceHashMap[address] ?: return
        Log.d(TAG, "Automatically connect $address = ${bleDevice.isPreviouslyConnected}")

        if (bleDevice.isPreviouslyConnected)
            bluetoothLEManagerScope.launch {
                connectDevice(address)
            }
    }

    override suspend fun connectDevice(address: String) {
        Log.d(TAG, "$address connectDevice")
        deviceBeingEdited = true

        bleConnectionHashMap[address] ?: createBleConnection(address)
        val device = bleConnectionHashMap[address]
        device?.connect()

        bleDeviceHashMap[address]?.isDeviceLoading = true
        bleDeviceHashMap[address]?.lastSeen = System.currentTimeMillis()
        refreshDeviceList()

        bleDeviceHashMap[address]?.isPreviouslyConnected = true
        deviceBeingEdited = false
    }

    override fun bondDevice(address: String) {
        val device = bleConnectionHashMap[address] ?: return
        device.pair()
    }

    override fun unBondDevice(address: String) {
        val device = bleConnectionHashMap[address] ?: return
        device.unpair()
    }

    override suspend fun disconnectDevice(address: String) {
        Log.d(TAG, "$address disconnectDevice")
        deviceBeingEdited = true

        bleDeviceHashMap[address]?.isPreviouslyConnected = false

        val device = bleConnectionHashMap[address] ?: return
        device.disconnect()

        bleDeviceHashMap[address]?.isDeviceLoading = true
        refreshDeviceList()
        deviceBeingEdited = false
    }

    override fun writeToDevice(address: String, command: ByteArray) {
        val device = bleConnectionHashMap[address] ?: return
        device.sendWriteCommand(command)
    }

    override fun refreshDeviceList(bleDevice: BleDevice?) {
        Log.d(TAG, "refreshDeviceList")
        bleDevice?.run {
            bleDeviceHashMap[macAddress]?.apply {
                alias = this@run.alias
            }
        }

        leScanCallBack.onScanResult(bleDeviceHashMap)
    }

    private fun resetVerifyBleDevicesTask() {
        bluetoothLEManagerScope.launch(Dispatchers.Main) {
            verifyBleDeviceTask?.cancel()
            verifyBleDeviceTask = null
            verifyBleDeviceTask = Timer().schedule(1000, 1000) {
                if (deviceBeingEdited) return@schedule
                Log.d(TAG, "Run verifyBleDeviceTask task")
                // Items should not be directly removed from forEach
                // loop, causes ConcurrentModificationException
                val addressesToRemove = arrayListOf<String>()

                bleDeviceHashMap.forEach { (address, bleDevice) ->
                    if (bleDevice.notAvailable && !deviceBeingEdited) {
                        Log.d(TAG, "Device not found - $address")
                        addressesToRemove.add(address)
                    }
                }

                if (addressesToRemove.isNotEmpty() && !deviceBeingEdited) {
                    addressesToRemove.forEach { bleDeviceHashMap.remove(it) }
                    refreshDeviceList()
                }
            }
        }
    }

    inner class BluetoothDeviceGattCallback : BluetoothGattCallback() {
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.d(TAG, "Character Write: $status")
        }

        override fun onConnectionStateChange(
            gatt: BluetoothGatt?,
            status: Int,
            newState: Int
        ) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt?.device?.address?.let {
                        bleDeviceHashMap[it]?.apply {
                            isConnected = true
                            isDeviceLoading = false
                        }

                        Log.d(TAG, "$it STATE_CONNECTED")
                    }
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt?.close()
                    gatt?.device?.address?.let {
                        bleDeviceHashMap[it]?.apply {
                            isConnected = false
                            isDeviceLoading = false
                            lastSeen = System.currentTimeMillis()
                        }
                        bleConnectionHashMap.remove(it)

                        Log.d(TAG, "$it STATE_DISCONNECTED")
                    }
                }
            }
            refreshDeviceList()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt?.services?.forEach { service ->
                service.characteristics?.forEach { characteristic ->
                    Log.d(TAG, "${gatt.device?.name} ${service.uuid} ${characteristic.uuid}")
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
        }
    }
}

interface IBluetoothLEManager {
    var scanning: Boolean
    val bleDeviceHashMap: HashMap<String, BleDevice>
    var leScanCallBack: BluetoothLeScanCallBack

    fun isBluetoothAvailable(): Boolean
    fun isBluetoothEnabled(): Boolean

    fun startScan(_leScanCallBack: BluetoothLeScanCallBack)
    fun stopScan()
    fun createBleConnection(address: String)
    suspend fun connectDevice(address: String)
    suspend fun disconnectDevice(address: String)
    fun bondDevice(address: String)
    fun unBondDevice(address: String)
    fun writeToDevice(address: String, command: ByteArray)

    fun refreshDeviceList(bleDevice: BleDevice? = null)
}

interface BluetoothLeScanCallBack {
    fun onScanResult(bluetoothDeviceList: HashMap<String, BleDevice>)
    fun onTriggerAction(bleDevice: BleDevice)
}
