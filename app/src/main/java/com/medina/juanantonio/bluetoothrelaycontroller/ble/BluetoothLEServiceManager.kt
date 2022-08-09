package com.medina.juanantonio.bluetoothrelaycontroller.ble

import android.content.Context
import android.content.Intent
import android.os.Build

class BluetoothLEServiceManager(
    private val context: Context,
    private val bluetoothManager: IBluetoothLEManager
) {

    fun startBluetoothLEService() {
        if (bluetoothManager.scanning) return
        val bluetoothLEServiceIntent = Intent(context, BluetoothLEService::class.java)
        bluetoothLEServiceIntent.putExtra(
            BluetoothLEService.SERVICE_ACTION,
            BluetoothLEService.START_SERVICE
        )
        context.startForegroundService(bluetoothLEServiceIntent)
    }

    fun stopBluetoothLEService() {
        if (!bluetoothManager.scanning) return
        val bluetoothLEServiceIntent = Intent(context, BluetoothLEService::class.java)
        bluetoothLEServiceIntent.putExtra(
            BluetoothLEService.SERVICE_ACTION,
            BluetoothLEService.STOP_SERVICE
        )
        context.startForegroundService(bluetoothLEServiceIntent)
    }
}
