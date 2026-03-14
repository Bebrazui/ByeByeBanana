package io.github.romanvht.byedpi.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.preference.*
import io.github.romanvht.byedpi.R
import io.github.romanvht.byedpi.BuildConfig
import io.github.romanvht.byedpi.activities.TestActivity
import io.github.romanvht.byedpi.data.Mode
import io.github.romanvht.byedpi.services.AppAutoEnableAccessibilityService
import io.github.romanvht.byedpi.utility.*

class MainSettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private val TAG: String = MainSettingsFragment::class.java.simpleName
    }

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            updatePreferences()
            AutoEnableUtils.updateMonitoring(requireContext())
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)

        setEditTextPreferenceListener("byedpi_proxy_ip") { checkIp(it) }
        setEditTestPreferenceListenerPort("byedpi_proxy_port")
        setEditTextPreferenceListener("dns_ip") { it.isBlank() || checkNotLocalIp(it) }

        findPreferenceNotNull<ListPreference>("language")
            .setOnPreferenceChangeListener { _, newValue ->
                SettingsUtils.setLang(newValue as String)
                true
            }

        findPreferenceNotNull<ListPreference>("app_theme")
            .setOnPreferenceChangeListener { _, newValue ->
                SettingsUtils.setTheme(newValue as String)
                true
            }

        findPreferenceNotNull<Preference>("proxy_test")
            .setOnPreferenceClickListener {
                val intent = Intent(context, TestActivity::class.java)
                startActivity(intent)
                true
            }

        findPreferenceNotNull<Preference>("battery_optimization")
            .setOnPreferenceClickListener {
                BatteryUtils.requestBatteryOptimization(requireContext())
                true
            }

        findPreferenceNotNull<Preference>("storage_access")
            .setOnPreferenceClickListener {
                StorageUtils.requestStoragePermission(this)
                true
            }

        findPreferenceNotNull<Preference>("inapp_logs_open")
            .setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), io.github.romanvht.byedpi.activities.LogsActivity::class.java))
                true
            }

        findPreferenceNotNull<Preference>("version").summary = BuildConfig.VERSION_NAME
        findPreferenceNotNull<Preference>("byedpi_version").summary = "0.17.3"

        val autoEnable = findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_enable")
        val autoEnableMethod =
            findPreferenceNotNull<ListPreference>("applist_whitelist_auto_enable_method")
        val autoStrict =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_strict")
        val autoStopOnExit =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_stop_on_exit")
        val keepAlive =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_keep_alive")

        autoEnable.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                requestAutoEnablePermission(autoEnableMethod.value)
            }
            AutoEnableUtils.updateMonitoring(requireContext())
            true
        }

        autoEnableMethod.setOnPreferenceChangeListener { _, newValue ->
            if (autoEnable.isChecked) {
                requestAutoEnablePermission(newValue as String)
            }
            AutoEnableUtils.updateMonitoring(requireContext())
            true
        }

        autoStrict.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            if (enabled) {
                autoEnableMethod.value = AutoEnableUtils.METHOD_ACCESSIBILITY
                autoStopOnExit.isChecked = false
                requestAutoEnablePermission(AutoEnableUtils.METHOD_ACCESSIBILITY)
            }
            AutoEnableUtils.updateMonitoring(requireContext())
            true
        }

        keepAlive.setOnPreferenceChangeListener { _, _ ->
            AutoEnableUtils.updateMonitoring(requireContext())
            true
        }

        updatePreferences()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences?.registerOnSharedPreferenceChangeListener(preferenceListener)
        updatePreferences()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    private fun updatePreferences() {
        val cmdEnable = findPreferenceNotNull<SwitchPreference>("byedpi_enable_cmd_settings").isChecked
        val mode = findPreferenceNotNull<ListPreference>("byedpi_mode").value.let { Mode.fromString(it) }
        val dns = findPreferenceNotNull<EditTextPreference>("dns_ip")
        val ipv6 = findPreferenceNotNull<SwitchPreference>("ipv6_enable")
        val proxy = findPreferenceNotNull<PreferenceCategory>("byedpi_proxy_category")

        val applistType = findPreferenceNotNull<ListPreference>("applist_type")
        val selectedApps = findPreferenceNotNull<Preference>("selected_apps")
        val autoEnable = findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_enable")
        val autoEnableMethod =
            findPreferenceNotNull<ListPreference>("applist_whitelist_auto_enable_method")
        val autoDisableService =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_disable_service")
        val autoStopOnExit =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_stop_on_exit")
        val autoStrict =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_auto_strict")
        val keepAlive =
            findPreferenceNotNull<SwitchPreference>("applist_whitelist_keep_alive")
        val batteryOptimization = findPreferenceNotNull<Preference>("battery_optimization")
        val storageAccess = findPreferenceNotNull<Preference>("storage_access")

        val uiSettings = findPreferenceNotNull<Preference>("byedpi_ui_settings")
        val cmdSettings = findPreferenceNotNull<Preference>("byedpi_cmd_settings")
        val proxyTest = findPreferenceNotNull<Preference>("proxy_test")

        if (cmdEnable) {
            val (cmdIp, cmdPort) = sharedPreferences?.checkIpAndPortInCmd() ?: Pair(null, null)
            proxy.isVisible = cmdIp == null && cmdPort == null
        } else {
            proxy.isVisible = true
        }

        uiSettings.isEnabled = !cmdEnable
        cmdSettings.isEnabled = cmdEnable
        proxyTest.isEnabled = cmdEnable

        when (mode) {
            Mode.VPN -> {
                dns.isVisible = true
                ipv6.isVisible = true

                when (applistType.value) {
                    "disable" -> {
                        applistType.isVisible = true
                        selectedApps.isVisible = false
                        autoEnable.isVisible = false
                        autoEnableMethod.isVisible = false
                        autoDisableService.isVisible = false
                        autoStopOnExit.isVisible = false
                        autoStrict.isVisible = false
                    }
                    "blacklist", "whitelist" -> {
                        applistType.isVisible = true
                        selectedApps.isVisible = true
                        autoEnable.isVisible = applistType.value == "whitelist"
                        autoEnableMethod.isVisible =
                            applistType.value == "whitelist" && autoEnable.isChecked
                        autoDisableService.isVisible =
                            applistType.value == "whitelist" && autoEnable.isChecked
                        autoStopOnExit.isVisible =
                            applistType.value == "whitelist" && autoEnable.isChecked
                        autoStrict.isVisible =
                            applistType.value == "whitelist" && autoEnable.isChecked
                        keepAlive.isVisible =
                            applistType.value == "whitelist" && autoEnable.isChecked
                    }
                    else -> {
                        applistType.isVisible = true
                        selectedApps.isVisible = false
                        autoEnable.isVisible = false
                        autoEnableMethod.isVisible = false
                        autoDisableService.isVisible = false
                        autoStopOnExit.isVisible = false
                        autoStrict.isVisible = false
                        keepAlive.isVisible = false
                        Log.w(TAG, "Unexpected applistType value: ${applistType.value}")
                    }
                }
            }

            Mode.Proxy -> {
                dns.isVisible = false
                ipv6.isVisible = false
                applistType.isVisible = false
                selectedApps.isVisible = false
                autoEnable.isVisible = false
                autoEnableMethod.isVisible = false
                autoDisableService.isVisible = false
                autoStopOnExit.isVisible = false
                autoStrict.isVisible = false
                keepAlive.isVisible = false
            }
        }

        if (BatteryUtils.isOptimizationDisabled(requireContext())) {
            batteryOptimization.summary = getString(R.string.battery_optimization_disabled_summary)
        } else {
            batteryOptimization.summary = getString(R.string.battery_optimization_summary)
        }

        if (StorageUtils.hasStoragePermission(requireContext())) {
            storageAccess.summary = getString(R.string.storage_access_allowed_summary)
        } else {
            storageAccess.summary = getString(R.string.storage_access_summary)
        }
    }

    private fun requestAutoEnablePermission(method: String) {
        when (method) {
            AutoEnableUtils.METHOD_USAGE_STATS -> {
                if (!UsageAccessUtils.hasUsageAccess(requireContext())) {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            }
            AutoEnableUtils.METHOD_ACCESSIBILITY -> {
                if (!AccessibilityUtils.isServiceEnabled(
                        requireContext(),
                        AppAutoEnableAccessibilityService::class.java
                    )
                ) {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            }
        }
    }
}
