package com.medina.juanantonio.bluetoothrelaycontroller.data.receivers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.CallSuper
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEServiceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

@AndroidEntryPoint
class RestartReceiver : HiltBroadcastReceiver() {

    companion object {
        private const val REQUEST_TIMER = 1
        private const val TAG = "RestartReceiver"

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getIntent(context: Context, requestCode: Int): PendingIntent? {
            val intent = Intent(context, RestartReceiver::class.java)
            // https://developer.android.com/reference/android/app/PendingIntent.html#getBroadcast(android.content.Context,%20int,%20android.content.Intent,%20int)
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        fun startAlarm(context: Context) {
            val pendingIntent = getIntent(context, REQUEST_TIMER)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            /*
            // trigger at 8:30am
            val alarmTime = LocalTime.of(8, 30)
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            if (now.toLocalTime().isAfter(alarmTime)) {
                now = now.plusDays(1)
            }
            now = now.withHour(alarmTime.hour).withMinute(alarmTime.minute) // .withSecond(alarmTime.second).withNano(alarmTime.nano)
            val utc= now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()

            val triggerAtMillis = utc.atZone(ZoneOffset.UTC)!!.toInstant()!!.toEpochMilli()
            // first trigger at next 8:30am, then repeat each day
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
             */

            alarm.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_HALF_HOUR,
                pendingIntent
            )
        }

        fun cancelAlarm(context: Context) {
            val pendingIntent = getIntent(context, REQUEST_TIMER)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    @Inject
    lateinit var bluetoothLEServiceManager: BluetoothLEServiceManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "RestartReceiver triggered.")

        bluetoothLEServiceManager.startBluetoothLEService()
    }
}