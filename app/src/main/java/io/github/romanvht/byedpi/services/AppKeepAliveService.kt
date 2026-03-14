package io.github.romanvht.byedpi.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.romanvht.byedpi.R
import io.github.romanvht.byedpi.utility.InAppLog
import io.github.romanvht.byedpi.utility.registerNotificationChannel

class AppKeepAliveService : Service() {
    companion object {
        private const val CHANNEL_ID = "KeepAlive"
        private const val NOTIFICATION_ID = 6

        fun start(context: Context) {
            InAppLog.i(context, "KeepAlive", "Start service")
            val intent = Intent(context, AppKeepAliveService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            InAppLog.i(context, "KeepAlive", "Stop service")
            val intent = Intent(context, AppKeepAliveService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel(this, CHANNEL_ID, R.string.keep_alive_channel_name)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    private fun createNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setSilent(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.keep_alive_notification_content))
            .build()
}
