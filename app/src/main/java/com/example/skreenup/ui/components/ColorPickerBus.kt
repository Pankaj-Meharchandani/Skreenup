package com.example.skreenup.ui.components

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ColorPickerBus {
    private val _events = MutableSharedFlow<Pair<String, Int>>()
    val events = _events.asSharedFlow()
    
    suspend fun emit(tag: String, color: Int) {
        _events.emit(tag to color)
    }
}
