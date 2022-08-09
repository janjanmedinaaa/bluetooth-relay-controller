package com.medina.juanantonio.bluetoothrelaycontroller.ble

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.medina.juanantonio.bluetoothrelaycontroller.features.MainActivity
import com.medina.juanantonio.bluetoothrelaycontroller.R
import com.medina.juanantonio.bluetoothrelaycontroller.data.models.BleDevice
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothLEService : LifecycleService(), BluetoothLeScanCallBack {

    companion object {
        const val SERVICE_ACTION = "SERVICE_ACTION"
        const val START_SERVICE = "START_SERVICE"
        const val STOP_SERVICE = "STOP_SERVICE"
        const val SCANNED_DEVICES = "SCANNED_DEVICES"
        const val BLE_DEVICES = "BLE_DEVICES"

        const val TAG = "bluetoothLEService"
    }

    private val localBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    @Inject
    lateinit var bluetoothLEManager: IBluetoothLEManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(SERVICE_ACTION) ?: "") {
            START_SERVICE -> {
                startDeviceScan()
                startForeground()
                return Service.START_STICKY
            }
            STOP_SERVICE -> {
                stopDeviceScan()
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startDeviceScan() {
        bluetoothLEManager.startScan(this)
    }

    private fun stopDeviceScan() {
        bluetoothLEManager.stopScan()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopDeviceScan()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationId = 101
        val channelId = "BlueButtNotificationChannel"
        val channelName = getString(R.string.ble_relay_controller_channel_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.setShowBadge(false)
        notificationChannel.description =
            getString(R.string.ble_relay_controller_channel_description)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.ble_relay_controller_notification_content_title))
            .setContentText(getString(R.string.ble_relay_controller_notification_content_text))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(notificationId, notification)
    }

    override fun onScanResult(bluetoothDeviceList: HashMap<String, BleDevice>) {
        val intent = Intent(SCANNED_DEVICES)
        intent.putExtra(BLE_DEVICES, bluetoothDeviceList)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun onTriggerAction(bleDevice: BleDevice) {
    }
}
