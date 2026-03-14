package io.github.romanvht.byedpi.utility

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InAppLog {
    private const val PREF_ENABLE = "inapp_logs_enable"
    private const val LOG_FILE = "inapp.log"
    private const val MAX_BYTES = 200 * 1024
    private val tsFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    fun isEnabled(context: Context): Boolean =
        context.getPreferences().getBoolean(PREF_ENABLE, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getPreferences().edit { putBoolean(PREF_ENABLE, enabled) }
    }

    fun d(context: Context, tag: String, msg: String) = append(context, "D", tag, msg)
    fun i(context: Context, tag: String, msg: String) = append(context, "I", tag, msg)
    fun w(context: Context, tag: String, msg: String) = append(context, "W", tag, msg)
    fun e(context: Context, tag: String, msg: String) = append(context, "E", tag, msg)

    private fun append(context: Context, level: String, tag: String, msg: String) {
        if (!isEnabled(context)) return
        val line = "${tsFormat.format(Date())} $level/$tag: $msg\n"
        try {
            val file = File(context.filesDir, LOG_FILE)
            file.appendText(line)
            trimIfNeeded(file)
        } catch (e: Exception) {
            Log.w("InAppLog", "Failed to write log", e)
        }
    }

    private fun trimIfNeeded(file: File) {
        if (!file.exists() || file.length() <= MAX_BYTES) return
        val bytes = file.readBytes()
        val start = bytes.size - MAX_BYTES
        file.writeBytes(bytes.copyOfRange(start, bytes.size))
    }

    fun readAll(context: Context): String {
        val file = File(context.filesDir, LOG_FILE)
        if (!file.exists()) return ""
        return file.readText()
    }

    fun clear(context: Context) {
        val file = File(context.filesDir, LOG_FILE)
        if (file.exists()) file.delete()
    }
}
