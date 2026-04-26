package com.example.skreenup.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.skreenup.data.AppTheme
import com.example.skreenup.data.SettingsManager
import kotlinx.coroutines.flow.StateFlow

import androidx.lifecycle.viewModelScope
import com.example.skreenup.update.GitHubRelease
import com.example.skreenup.update.UpdateChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager.getInstance(application)
    private val updateChecker = UpdateChecker()

    val theme: StateFlow<AppTheme> = settingsManager.theme
    val useGradientBackground: StateFlow<Boolean> = settingsManager.useGradientBackground
    val continueLastProject: StateFlow<Boolean> = settingsManager.continueLastProject
    val useHaptics: StateFlow<Boolean> = settingsManager.useHaptics
    val defaultExportAction: StateFlow<com.example.skreenup.data.ExportAction> = settingsManager.defaultExportAction
    val showWatermark: StateFlow<Boolean> = settingsManager.showWatermark
    val customWatermark: StateFlow<String> = settingsManager.customWatermark

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        settingsManager.setTheme(theme)
    }

    fun setUseGradientBackground(use: Boolean) {
        settingsManager.setUseGradientBackground(use)
    }

    fun setContinueLastProject(continueLast: Boolean) {
        settingsManager.setContinueLastProject(continueLast)
    }

    fun setUseHaptics(use: Boolean) {
        settingsManager.setUseHaptics(use)
    }

    fun setDefaultExportAction(action: com.example.skreenup.data.ExportAction) {
        settingsManager.setDefaultExportAction(action)
    }

    fun setShowWatermark(show: Boolean) {
        settingsManager.setShowWatermark(show)
    }

    fun setCustomWatermark(text: String) {
        settingsManager.setCustomWatermark(text)
    }

    fun clearHistory() {
        viewModelScope.launch {
            val db = com.example.skreenup.data.SkreenupDatabase.getDatabase(getApplication())
            db.projectDao().deleteAllProjects()
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            try {
                val release = updateChecker.checkForUpdate()
                if (release != null) {
                    val currentVersion = getApplication<Application>().packageManager
                        .getPackageInfo(getApplication<Application>().packageName, 0).versionName

                    if (release.tag_name != currentVersion) {
                        _updateState.value = UpdateState.UpdateAvailable(release)
                    } else {
                        _updateState.value = UpdateState.UpToDate
                    }
                } else {
                    _updateState.value = UpdateState.Error
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class UpdateAvailable(val release: GitHubRelease) : UpdateState()
    object UpToDate : UpdateState()
    object Error : UpdateState()
}
