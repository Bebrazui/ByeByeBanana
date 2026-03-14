package io.github.romanvht.byedpi.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import io.github.romanvht.byedpi.utility.AutoEnableUtils
import io.github.romanvht.byedpi.utility.InAppLog

class AppAutoEnableAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString()
        if (packageName != null) {
            InAppLog.d(this, "AutoEnableAcc", "Foreground: $packageName")
        }
        AutoEnableUtils.onForegroundApp(this, packageName)
    }

    override fun onInterrupt() = Unit
}
