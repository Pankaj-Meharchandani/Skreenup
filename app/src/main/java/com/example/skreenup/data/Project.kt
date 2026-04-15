package com.example.skreenup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val screenshotUri: String?,
    val frameType: String, // e.g., "ANDROID_PHONE", "IPHONE", "MACBOOK", etc.
    val backgroundType: String, // "SOLID", "GRADIENT", "BLUR", "PATTERN"
    val backgroundValue: String, // Hex color, list of hex for gradient, or URI for blur image
    val scale: Float = 0.8f,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val showWatermark: Boolean = false,
    val watermarkText: String = "Created with Skreenup",
    val aspectRatio: String = "SQUARE",
    val createdAt: Long = System.currentTimeMillis()
)
