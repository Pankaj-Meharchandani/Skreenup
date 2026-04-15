package com.example.skreenup.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SkreenupTabKey : NavKey

@Serializable
object FrameTab : SkreenupTabKey

@Serializable
object BackgroundTab : SkreenupTabKey

@Serializable
object AdjustTab : SkreenupTabKey

@Serializable
object AboutTab : SkreenupTabKey
