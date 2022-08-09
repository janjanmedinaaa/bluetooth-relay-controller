package com.medina.juanantonio.bluetoothrelaycontroller.features

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEServiceManager
import com.medina.juanantonio.bluetoothrelaycontroller.data.receivers.RestartReceiver
import com.medina.juanantonio.bluetoothrelaycontroller.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var bluetoothLEServiceManager: BluetoothLEServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RestartReceiver.startAlarm(applicationContext)
    }

    override fun onResume() {
        super.onResume()

        val permissionList = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
                add(Manifest.permission.BLUETOOTH_SCAN)
            }
        }.toTypedArray()

        runWithPermissions(*permissionList) {
            bluetoothLEServiceManager.startBluetoothLEService()
        }
    }

    override fun finish() {
        bluetoothLEServiceManager.stopBluetoothLEService()
        super.finish()
    }
}