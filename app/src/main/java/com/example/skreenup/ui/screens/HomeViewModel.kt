package com.example.skreenup.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skreenup.data.Project
import com.example.skreenup.data.Preset
import com.example.skreenup.data.SkreenupDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SkreenupDatabase.getDatabase(application)
    private val projectDao = db.projectDao()
    private val presetDao = db.presetDao()

    val projects: StateFlow<List<Project>> = projectDao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presets: StateFlow<List<Preset>> = presetDao.getAllPresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            projectDao.deleteProject(project)
        }
    }

    fun deletePreset(preset: Preset) {
        viewModelScope.launch {
            presetDao.deletePreset(preset)
        }
    }
}
