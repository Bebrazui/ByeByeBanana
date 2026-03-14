package io.github.romanvht.byedpi.utility

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

object AccessibilityUtils {
    fun isServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val enabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED,
            0
        ) == 1

        if (!enabled) return false

        val setting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val expected = ComponentName(context, serviceClass).flattenToString()
        return setting.split(':').any { it.equals(expected, ignoreCase = true) }
    }
}
