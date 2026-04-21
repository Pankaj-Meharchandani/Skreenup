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

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        settingsManager.setTheme(theme)
    }

    fun setUseGradientBackground(use: Boolean) {
        settingsManager.setUseGradientBackground(use)
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
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

