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
    POPPINS("Poppins", "sans-serif"),
    INTER("Inter", "sans-serif-medium"),
    MONTSERRAT("Montserrat", "sans-serif-light"),
    BEBAS("Bebas Neue", "sans-serif-black"),
    PACIFICO("Pacifico", "cursive"),
    PLAYFAIR("Playfair Display", "serif-monospace"),
    TIMES("Times New Roman", "serif"),
    OSWALD("Oswald", "sans-serif-condensed"),
    RALEWAY("Raleway", "sans-serif-thin"),
    ANTON("Anton", "sans-serif-black"),
    QUICKSAND("Quicksand", "sans-serif-light"),
    LIBRE_BASKERVILLE("Libre Baskerville", "serif")
}

@Immutable
enum class TextAlignLabel(val label: String) {
    LEFT("Left"),
    CENTER("Center"),
    RIGHT("Right")
}

@Immutable
enum class TextBackgroundStyle {
    NONE,
    FILLED,
    OUTLINED,
    GLASS
}

@kotlinx.serialization.Serializable
data class TextLayer(
    val id: String = java.util.UUID.randomUUID().toString(),
    val heading: String = "",
    val subheading: String = "",
    val headingFont: String = TextFont.POPPINS.name,
    val subheadingFont: String = TextFont.POPPINS.name,
    val headingSize: Float = 60f,
    val subheadingSize: Float = 40f,
    val textGap: Float = 20f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val textColor: Int = 0xFFFFFFFF.toInt(),
    val textAlign: String = TextAlignLabel.CENTER.name,
    val headingBold: Boolean = true,
    val subheadingBold: Boolean = false,
    val textShadow: Boolean = true,
    val zIndex: Int = 1,
    val backgroundStyle: String = TextBackgroundStyle.NONE.name,
    val backgroundColor: Int = 0xFF000000.toInt(),
    val backgroundAlpha: Float = 0.5f,
    val backgroundPadding: Float = 24f,
    val backgroundCornerRadius: Float = 16f
)
