package com.example.skreenup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val frameType: String,
    val backgroundType: String,
    val backgroundValue: String,
    val scale: Float = 0.8f,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val showWatermark: Boolean = false,
    val watermarkText: String = "Created with Skreenup",
    val aspectRatio: String = "SQUARE"
)
