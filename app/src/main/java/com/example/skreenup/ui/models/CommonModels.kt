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
    IMAGE,
    TRANSPARENT
}

@Immutable
enum class CompositionAspectRatio(val ratio: Float, val label: String) {
    SQUARE(1f, "1:1"),
    PORTRAIT(9f / 16f, "9:16"),
    LANDSCAPE(16f / 9f, "16:9"),
    TABLET(4f / 3f, "4:3")
}

@Immutable
enum class TextFont(val label: String, val family: String) {
    ROBOTO("Roboto", "sans-serif"),
    INTER("Inter", "sans-serif-medium"),
    MONTSERRAT("Montserrat", "sans-serif-light"),
    TIMES("Times New Roman", "serif"),
    CALIBRI("Calibri", "sans-serif-condensed"),
    PACIFICO("Script", "cursive"),
    BEBAS("Condensed Bold", "sans-serif-black"),
    PLAYFAIR("Playfair", "serif-monospace")
}

@Immutable
enum class TextAlignLabel(val label: String) {
    LEFT("Left"),
    CENTER("Center"),
    RIGHT("Right")
}
