package com.example.skreenup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val configJson: String,
    val previewUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
