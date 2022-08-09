package com.medina.juanantonio.bluetoothrelaycontroller.ble.models

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEManager.Companion.JDY23_SERVICE_UUID
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEManager.Companion.JDY23_WRITE_CHARACTERISTIC_UUID
import java.util.*

@SuppressLint("MissingPermission")
class JDY23BleConnection(
    context: Context,
    bluetoothDevice: BluetoothDevice,
    gattCallback: BluetoothGattCallback
) : BleConnection(context, bluetoothDevice, gattCallback) {

    override fun sendWriteCommand(command: ByteArray) {
        gatt?.getService(UUID.fromString(JDY23_SERVICE_UUID))
            ?.getCharacteristic(UUID.fromString(JDY23_WRITE_CHARACTERISTIC_UUID))
            ?.run characteristic@{
                writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                value = command
                gatt?.writeCharacteristic(this@characteristic)
            }
    }
}