package io.github.romanvht.byedpi.utility

import android.content.Context
import androidx.core.content.edit
import android.net.VpnService
import io.github.romanvht.byedpi.data.AppStatus
import io.github.romanvht.byedpi.data.Mode
import io.github.romanvht.byedpi.services.AppAutoEnableUsageService
import io.github.romanvht.byedpi.services.ServiceManager
import io.github.romanvht.byedpi.services.AppKeepAliveService
import io.github.romanvht.byedpi.services.appStatus
import io.github.romanvht.byedpi.utility.InAppLog

object AutoEnableUtils {
    const val PREF_AUTO_ENABLE = "applist_whitelist_auto_enable"
    const val PREF_AUTO_METHOD = "applist_whitelist_auto_enable_method"
    const val PREF_AUTO_ACTIVE = "applist_whitelist_auto_active"
    const val PREF_AUTO_STOP_ON_EXIT = "applist_whitelist_auto_stop_on_exit"

    const val METHOD_USAGE_STATS = "usage_stats"
    const val METHOD_ACCESSIBILITY = "accessibility"

    fun shouldMonitor(prefs: android.content.SharedPreferences): Boolean {
        return prefs.mode() == Mode.VPN &&
            prefs.getStringNotNull("applist_type", "disable") == "whitelist" &&
            prefs.getBoolean(PREF_AUTO_ENABLE, false)
    }

    fun updateMonitoring(context: Context) {
        val prefs = context.getPreferences()
        val shouldMonitor = shouldMonitor(prefs)
        val strict = true
        val keepAlive = true
        val method = METHOD_ACCESSIBILITY

        if (keepAlive && shouldMonitor) {
            AppKeepAliveService.start(context)
        } else {
            AppKeepAliveService.stop(context)
        }

        if (method == METHOD_USAGE_STATS) {
            if (shouldMonitor && UsageAccessUtils.hasUsageAccess(context)) {
                AppAutoEnableUsageService.start(context)
                InAppLog.d(context, "AutoEnable", "Monitoring via usage stats")
            } else {
                AppAutoEnableUsageService.stop(context)
                InAppLog.d(context, "AutoEnable", "Monitoring stopped (usage stats)")
            }
        } else {
            AppAutoEnableUsageService.stop(context)
            InAppLog.d(context, "AutoEnable", "Monitoring via accessibility")
        }

        if (!shouldMonitor) {
            clearAutoActiveIfNeeded(context)
        }
    }

    private const val STOP_DELAY_MS = 3000L
    private const val START_COOLDOWN_MS = 50L
    private const val START_STABLE_MS = 50L
    private var lastPackage: String? = null
    private var lastPackageChangeMs: Long = 0
    private var lastStartMs: Long = 0
    private var lastNonWhitelistMs: Long = 0

    fun onForegroundApp(context: Context, packageName: String?) {
        if (packageName.isNullOrBlank()) return
        if (packageName == context.packageName) return
        if (isIgnorablePackage(context, packageName)) return

        val prefs = context.getPreferences()
        val strict = true
        val stopOnExit = if (strict) false else prefs.getBoolean(PREF_AUTO_STOP_ON_EXIT, false)
        if (!shouldMonitor(prefs)) {
            clearAutoActiveIfNeeded(context)
            return
        }

        val selected = prefs.getSelectedApps()
        if (selected.isEmpty()) {
            clearAutoActiveIfNeeded(context)
            return
        }

        val isWhitelisted = selected.contains(packageName)
        val autoActive = prefs.getBoolean(PREF_AUTO_ACTIVE, false)
        val now = System.currentTimeMillis()

        if (packageName != lastPackage) {
            lastPackage = packageName
            lastPackageChangeMs = now
        }

        if (isWhitelisted) {
            if (appStatus.first != AppStatus.Running) {
                val stable = now - lastPackageChangeMs > START_STABLE_MS
                if (stable && now - lastStartMs > START_COOLDOWN_MS) {
                    if (VpnService.prepare(context) != null) {
                        InAppLog.w(context, "AutoEnable", "VPN permission not granted, skip start")
                        return
                    }
                    InAppLog.i(context, "AutoEnable", "Start VPN for $packageName")
                    ServiceManager.start(context, Mode.VPN)
                    lastStartMs = now
                }
            }
            if (!autoActive) {
                prefs.edit { putBoolean(PREF_AUTO_ACTIVE, true) }
            }
            lastNonWhitelistMs = 0
        } else {
            if (autoActive || stopOnExit) {
                if (lastNonWhitelistMs == 0L) {
                    lastNonWhitelistMs = now
                }
                val stableNonWhitelist = now - lastNonWhitelistMs > STOP_DELAY_MS
                val stablePackage = now - lastPackageChangeMs > STOP_DELAY_MS
                if (stableNonWhitelist && stablePackage && appStatus.first == AppStatus.Running) {
                    InAppLog.i(context, "AutoEnable", "Stop VPN (left whitelist)")
                    ServiceManager.stop(context)
                    prefs.edit { putBoolean(PREF_AUTO_ACTIVE, false) }
                }
            }
        }
    }

    private fun clearAutoActiveIfNeeded(context: Context) {
        val prefs = context.getPreferences()
        if (prefs.getBoolean(PREF_AUTO_ACTIVE, false)) {
            if (appStatus.first == AppStatus.Running) {
                ServiceManager.stop(context)
            }
            prefs.edit { putBoolean(PREF_AUTO_ACTIVE, false) }
        }
    }

    private fun isIgnorablePackage(context: Context, packageName: String): Boolean {
        if (packageName == "android" || packageName == "com.android.systemui") return true
        if (packageName.contains("launcher", ignoreCase = true)) return true
        return try {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            val isSystem = (info.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            isSystem
        } catch (_: Exception) {
            true
        }
    }
}
