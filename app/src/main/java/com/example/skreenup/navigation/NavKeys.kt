package com.example.skreenup.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SkreenupNavKey : NavKey

@Serializable
object Home : SkreenupNavKey

@Serializable
data class Editor(val projectId: Long? = null, val presetId: Long? = null) : SkreenupNavKey
