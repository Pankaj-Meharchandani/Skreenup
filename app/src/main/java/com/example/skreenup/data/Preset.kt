package com.example.skreenup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val configJson: String, // Store all editor state as JSON
    val previewUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
