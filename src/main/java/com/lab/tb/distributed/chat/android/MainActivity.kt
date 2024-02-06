package com.lab.tb.distributed.chat.android

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.app.NotificationCompat
import com.lab.tb.distributed.BloomService
import com.lab.tb.distributed.ChatController
import com.lab.tb.distributed.chat.android.presentation.app.DistributedChatApp
import com.lab.tb.distributed.chat.android.presentation.component.theme.DistributedChatAppTheme
import com.lab.tb.distributed.chat.android.receiver.BluetoothConnectionReceiver
import com.lab.tb.distributed.database.DatabaseHelper
import com.lab.tb.distributed.database.DbConst.uniqueKey
import com.lab.tb.distributed.model.BleSettings
import com.lab.tb.distributed.model.Network
import com.lab.tb.distributed.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : AppCompatActivity() {

    private val permissionsUtil = PermissionsUtil(this)

    private val bluetoothConnectionReceiver = BluetoothConnectionReceiver(this,
        onConnectionStateChanged = { isEnabled ->
        if (isEnabled) {
            Toast.makeText(this, R.string.bluetooth_enabled_message, Toast.LENGTH_SHORT).show()
            val settings = DatabaseHelper.instance.get<BleSettings>(uniqueKey) ?: BleSettings()
            CoroutineScope(Dispatchers.Main).launch {
                if (settings.advertisingEnabled) {
                    BloomService.getInstance().startAdvertising()
                }

                if (settings.scanningEnabled) {
                    BloomService.getInstance().startScanning()
                }
            }
        } else {
            Toast.makeText(this, R.string.bluetooth_disabled_message, Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Main).launch {
                BloomService.getInstance().stopAdvertising()
                BloomService.getInstance().stopScanning()
            }
        }
    })

    private var isCanShowNotification = false

    companion object {
        const val COUNTER_CHANNEL_ID = "BloomChannel"
    }

    override fun onStart() {
        super.onStart()
        isCanShowNotification = false
    }

    override fun onResume() {
        super.onResume()
        isCanShowNotification = false
    }

    override fun onDestroy() {
        super.onDestroy()
        BloomService.getInstance().stopServer()
        bluetoothConnectionReceiver.unregister()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        isCanShowNotification = false
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val profile = Profile()
        val network = Network()
        DatabaseHelper.instance.init(this.applicationContext as Application)

        val settings = DatabaseHelper.instance.get<BleSettings>(uniqueKey) ?: BleSettings()
        val bloomService = BloomService.getInstance().apply {
            this.init(applicationContext as Application)
        }
        ChatController.getInstance().initialize(applicationContext as Application)

        ChatController.getInstance().doRegisterPushLocalNoti { displayName, content ->
            if (!isCanShowNotification) return@doRegisterPushLocalNoti
            DatabaseHelper.instance.get<BleSettings>(uniqueKey)?.let {
                if (!it.enableNotification) return@doRegisterPushLocalNoti
            }
            pushNotification(applicationContext as Application, displayName, content)
        }

        permissionsUtil.requestBluetoothPermission {
            if (it) {
                if (settings.advertisingEnabled) {
                    bloomService.startAdvertising()
                }
                if (settings.scanningEnabled) {
                    bloomService.startScanning()
                }
            }
            Toast.makeText(this, "Permissions are granted = $it", Toast.LENGTH_SHORT).show()
        }
        bluetoothConnectionReceiver.register()

        setContent {
            DistributedChatAppTheme(darkTheme = true) {
                DistributedChatApp(windowSizeClass = calculateWindowSizeClass(this))
            }
        }
        processDeeplink(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            COUNTER_CHANNEL_ID,
            "Bloom",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Used for the increment counter notifications"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun pushNotification(context: Context, displayName: String, content: String) {
        val intent = Intent(context, this::class.java)
        val notificationId =
            System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setContentTitle(displayName)
            .setContentText(content)
            .setSmallIcon(R.drawable.bloom_app_icon)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, notification)
    }

    override fun onPause() {
        super.onPause()
        isCanShowNotification = true
    }

    override fun onNewIntent(intent: Intent?) {
        processDeeplink(intent)
        super.onNewIntent(intent)
    }

    private fun processDeeplink(intent: Intent?) {
        intent?.let {
            val data = it.data
            data?.let { uri ->
                if (uri.path?.contains("message") == true) {
                    val messageId = uri.lastPathSegment

                } else if (uri.path?.contains("attachment") == true) {
                    val attachment = uri.lastPathSegment

                } else if (uri.path?.contains("chaChoonnel") == true) {
                    val channelId = uri.lastPathSegment
                }
            }
        }
    }
}
