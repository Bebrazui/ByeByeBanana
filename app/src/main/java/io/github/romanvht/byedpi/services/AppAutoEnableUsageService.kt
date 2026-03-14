package io.github.romanvht.byedpi.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.romanvht.byedpi.R
import io.github.romanvht.byedpi.utility.AutoEnableUtils
import io.github.romanvht.byedpi.utility.InAppLog
import io.github.romanvht.byedpi.utility.UsageAccessUtils
import io.github.romanvht.byedpi.utility.registerNotificationChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppAutoEnableUsageService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var monitorJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "AutoEnable"
        private const val NOTIFICATION_ID = 4

        fun start(context: Context) {
            InAppLog.i(context, "AutoEnableUsage", "Start service")
            val intent = Intent(context, AppAutoEnableUsageService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            InAppLog.i(context, "AutoEnableUsage", "Stop service")
            val intent = Intent(context, AppAutoEnableUsageService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel(this, CHANNEL_ID, R.string.auto_enable_channel_name)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())

        if (monitorJob == null) {
            monitorJob = scope.launch {
                while (isActive) {
                    if (UsageAccessUtils.hasUsageAccess(this@AppAutoEnableUsageService)) {
                        val packageName = UsageAccessUtils.getLastUsedPackage(this@AppAutoEnableUsageService)
                        AutoEnableUtils.onForegroundApp(this@AppAutoEnableUsageService, packageName)
                    }
                    delay(1200)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        monitorJob?.cancel()
        monitorJob = null
    }

    private fun createNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setSilent(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.auto_enable_notification_content))
            .build()
}
