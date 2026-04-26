package com.example.skreenup.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class ExportAction {
    ASK, SAVE, SHARE, CLIPBOARD
}

class SettingsManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("skreenup_settings", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(loadTheme())
    val theme: StateFlow<AppTheme> = _theme

    private val _useGradientBackground = MutableStateFlow(loadUseGradientBackground())
    val useGradientBackground: StateFlow<Boolean> = _useGradientBackground

    private val _continueLastProject = MutableStateFlow(loadContinueLastProject())
    val continueLastProject: StateFlow<Boolean> = _continueLastProject

    private val _useHaptics = MutableStateFlow(loadUseHaptics())
    val useHaptics: StateFlow<Boolean> = _useHaptics

    private val _defaultExportAction = MutableStateFlow(loadDefaultExportAction())
    val defaultExportAction: StateFlow<ExportAction> = _defaultExportAction

    private val _showWatermark = MutableStateFlow(loadShowWatermark())
    val showWatermark: StateFlow<Boolean> = _showWatermark

    private val _customWatermark = MutableStateFlow(loadCustomWatermark())
    val customWatermark: StateFlow<String> = _customWatermark

    fun setTheme(theme: AppTheme) {
        prefs.edit().putString("app_theme", theme.name).apply()
        _theme.value = theme
    }

    private fun loadTheme(): AppTheme {
        val name = prefs.getString("app_theme", AppTheme.SYSTEM.name)
        return try {
            AppTheme.valueOf(name ?: AppTheme.SYSTEM.name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    fun setUseGradientBackground(use: Boolean) {
        prefs.edit().putBoolean("use_gradient_bg", use).apply()
        _useGradientBackground.value = use
    }

    private fun loadUseGradientBackground(): Boolean {
        return prefs.getBoolean("use_gradient_bg", true)
    }

    fun setContinueLastProject(continueLast: Boolean) {
        prefs.edit().putBoolean("continue_last_project", continueLast).apply()
        _continueLastProject.value = continueLast
    }

    private fun loadContinueLastProject(): Boolean {
        return prefs.getBoolean("continue_last_project", false)
    }

    fun setUseHaptics(use: Boolean) {
        prefs.edit().putBoolean("use_haptics", use).apply()
        _useHaptics.value = use
    }

    private fun loadUseHaptics(): Boolean {
        return prefs.getBoolean("use_haptics", true)
    }

    fun setDefaultExportAction(action: ExportAction) {
        prefs.edit().putString("default_export_action", action.name).apply()
        _defaultExportAction.value = action
    }

    private fun loadDefaultExportAction(): ExportAction {
        val name = prefs.getString("default_export_action", ExportAction.ASK.name)
        return try {
            ExportAction.valueOf(name ?: ExportAction.ASK.name)
        } catch (e: Exception) {
            ExportAction.ASK
        }
    }

    fun saveLastEditorConfig(json: String) {
        prefs.edit().putString("last_editor_config", json).apply()
    }

    fun getLastEditorConfig(): String? {
        return prefs.getString("last_editor_config", null)
    }

    fun setLastVisitedScreen(screen: String) {
        prefs.edit().putString("last_visited_screen", screen).apply()
    }

    fun getLastVisitedScreen(): String? {
        return prefs.getString("last_visited_screen", null)
    }

    fun setShowWatermark(show: Boolean) {
        prefs.edit().putBoolean("show_watermark", show).apply()
        _showWatermark.value = show
    }

    private fun loadShowWatermark(): Boolean {
        return prefs.getBoolean("show_watermark", true)
    }

    fun setCustomWatermark(text: String) {
        prefs.edit().putString("custom_watermark", text).apply()
        _customWatermark.value = text
    }

    private fun loadCustomWatermark(): String {
        return prefs.getString("custom_watermark", "Made with Skreenup") ?: "Made with Skreenup"
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean("is_first_launch", true)
    }

    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean("is_first_launch", false).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context).also { INSTANCE = it }
            }
        }
    }
}
