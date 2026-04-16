package com.example.skreenup.ui.models

import androidx.compose.runtime.Immutable

@Immutable
enum class FrameType {
    ANDROID_PHONE,
    IPHONE,
    TABLET,
    LAPTOP,
    DESKTOP
}

@Immutable
enum class DeviceCategory(val label: String) {
    PHONE("Phone"),
    TABLET("Tablet"),
    LAPTOP("Laptop"),
    WEB("Web"),
    PC("PC")
}

@Immutable
enum class BackgroundType {
    SOLID,
    GRADIENT,
    BLUR,
    IMAGE
}

@Immutable
enum class CompositionAspectRatio(val ratio: Float, val label: String) {
    SQUARE(1f, "1:1"),
    PORTRAIT(9f / 16f, "9:16"),
    LANDSCAPE(16f / 9f, "16:9"),
    TABLET(4f / 3f, "4:3")
}
