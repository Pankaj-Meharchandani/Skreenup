package com.example.skreenup.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skreenup.data.Project
import com.example.skreenup.data.Preset
import com.example.skreenup.data.SkreenupDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SkreenupDatabase.getDatabase(application)
    private val projectDao = db.projectDao()
    private val presetDao = db.presetDao()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    val presets: StateFlow<List<Preset>> = presetDao.getAllPresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadProject(projectId: Long?) {
        if (projectId == null) {
            _currentProject.value = null
            return
        }
        viewModelScope.launch {
            _currentProject.value = projectDao.getProjectById(projectId)
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            if (project.id == 0L) {
                projectDao.insertProject(project)
            } else {
                projectDao.updateProject(project)
            }
        }
    }

    fun savePreset(preset: Preset) {
        viewModelScope.launch {
            presetDao.insertPreset(preset)
        }
    }
}
