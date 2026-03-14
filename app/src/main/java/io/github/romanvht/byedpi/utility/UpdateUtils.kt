package io.github.romanvht.byedpi.utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.github.romanvht.byedpi.BuildConfig
import io.github.romanvht.byedpi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateUtils {
    private const val PREF_LAST_PROMPT_VERSION = "update_last_prompt_version"
    private const val PREF_LAST_CHECK_TIME = "update_last_check_time"
    private const val CHECK_COOLDOWN_MS = 6 * 60 * 60 * 1000L
    private const val REPO_OWNER = "Bebrazui"
    private const val REPO_NAME = "ByeByeBanana"

    @Volatile
    private var checkedThisSession = false

    fun checkForUpdates(host: LifecycleOwner, context: Context) {
        if (checkedThisSession) return
        checkedThisSession = true

        val prefs = context.getPreferences()
        val now = System.currentTimeMillis()
        val lastCheck = prefs.getLong(PREF_LAST_CHECK_TIME, 0L)
        if (now - lastCheck < CHECK_COOLDOWN_MS) return
        prefs.edit { putLong(PREF_LAST_CHECK_TIME, now) }

        host.lifecycleScope.launch(Dispatchers.IO) {
            val result = fetchLatestRelease() ?: return@launch

            if (result.isPreRelease) return@launch

            val localVersion = BuildConfig.VERSION_NAME.removeSuffix("-debug")
            if (!isNewer(result.version, localVersion)) return@launch

            val lastPrompted = prefs.getStringNotNull(PREF_LAST_PROMPT_VERSION, "")
            if (lastPrompted == result.version) return@launch

            prefs.edit { putString(PREF_LAST_PROMPT_VERSION, result.version) }
            withContext(Dispatchers.Main) {
                showUpdateDialog(context, result)
            }
        }
    }

    private fun showUpdateDialog(context: Context, release: ReleaseInfo) {
        AlertDialog.Builder(context)
            .setTitle(R.string.update_available_title)
            .setMessage(context.getString(R.string.update_available_message, release.version))
            .setPositiveButton(R.string.update_action) { _, _ ->
                val url = release.downloadUrl ?: release.pageUrl ?: return@setPositiveButton
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun fetchLatestRelease(): ReleaseInfo? {
        val url = URL("https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest")
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 7000
        connection.readTimeout = 7000
        connection.setRequestProperty("Accept", "application/vnd.github+json")

        return try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val tag = json.optString("tag_name", "")
            val name = json.optString("name", "")
            val version = if (tag.isNotBlank()) tag else name
            val pageUrl = json.optString("html_url", "")
            val prerelease = json.optBoolean("prerelease", false)
            val assets = json.optJSONArray("assets")
            val downloadUrl = assets?.let { arr ->
                (0 until arr.length())
                    .map { arr.getJSONObject(it) }
                    .firstOrNull { it.optString("name", "").endsWith(".apk", true) }
                    ?.optString("browser_download_url", "")
            }
            if (version.isBlank()) null else ReleaseInfo(version, downloadUrl, pageUrl, prerelease)
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun isNewer(remote: String, local: String): Boolean {
        val r = extractVersionNums(remote)
        val l = extractVersionNums(local)
        val max = maxOf(r.size, l.size)
        for (i in 0 until max) {
            val rv = r.getOrNull(i) ?: 0
            val lv = l.getOrNull(i) ?: 0
            if (rv != lv) return rv > lv
        }
        return false
    }

    private fun extractVersionNums(value: String): List<Int> {
        val list = Regex("\\d+").findAll(value).mapNotNull { it.value.toIntOrNull() }.toList()
        return if (list.isEmpty()) listOf(0) else list
    }

    private data class ReleaseInfo(
        val version: String,
        val downloadUrl: String?,
        val pageUrl: String?,
        val isPreRelease: Boolean
    )
}
