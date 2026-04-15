package com.example.skreenup.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SkreenupNavKey : NavKey

@Serializable
object Editor : SkreenupNavKey

@Serializable
object About : SkreenupNavKey

@Serializable
sealed interface SkreenupTabKey : NavKey

@Serializable
object FrameTab : SkreenupTabKey

@Serializable
object BackgroundTab : SkreenupTabKey

@Serializable
object AdjustTab : SkreenupTabKey
