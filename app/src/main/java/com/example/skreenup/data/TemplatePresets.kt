package com.example.skreenup.data

import com.example.skreenup.ui.models.EditorConfig
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.models.TextFont

data class StaticTemplate(
    val id: String,
    val name: String,
    val config: EditorConfig
)

val PRESET_TEMPLATES = listOf(
    StaticTemplate(
        id = "skreenup_main",
        name = "Skreenup Pro",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF121212.toInt(),
            gradientColors = listOf(0xFF0F2027.toInt(), 0xFF203A43.toInt(), 0xFF2C5364.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Skreenup\nMockups",
            subheading = "Transform screenshots into professional art",
            headingFont = TextFont.POPPINS.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 85f,
            subheadingSize = 32f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.85f,
            rotation = 8f,
            frameOffsetX = 260f,
            frameOffsetY = 20f,
            textOffsetX = 0f,
            textOffsetY = 0f,
            textZIndex = -1
        )
    ),
    StaticTemplate(
        id = "wormhole_style",
        name = "Space Portal",
        config = EditorConfig(
            selectedDeviceName = "Galaxy S26 Ultra",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF0F0C29.toInt(),
            gradientColors = listOf(0xFF0F0C29.toInt(), 0xFF302B63.toInt(), 0xFF24243E.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Universal\nSharing",
            subheading = "Fast, secure, and cross-platform export",
            headingFont = TextFont.ANTON.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 90f,
            subheadingSize = 32f,
            textColor = 0xFFE1BEE7.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.9f,
            rotation = -5f,
            frameOffsetX = 280f,
            frameOffsetY = 0f,
            textOffsetX = 0f,
            textOffsetY = 50f
        )
    ),
    StaticTemplate(
        id = "mint_style",
        name = "Minty Focus",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFFFF9A9E.toInt(),
            gradientColors = listOf(0xFFFF9A9E.toInt(), 0xFFFAD0C4.toInt()),
            screenBackgroundColor = 0xFFFFFFFF.toInt(),
            heading = "Play Store\nReady",
            subheading = "Export high-res images for your app listing",
            headingFont = TextFont.MONTSERRAT.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 80f,
            subheadingSize = 34f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.85f,
            rotation = 0f,
            frameOffsetX = 260f,
            frameOffsetY = 0f,
            textOffsetX = 0f,
            textOffsetY = 0f
        )
    ),
    StaticTemplate(
        id = "official_release",
        name = "Studio Mist",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.IMAGE.name,
            backgroundColor = 0xFF000000.toInt(),
            gradientColors = listOf(0xFF000000.toInt(), 0xFF000000.toInt()),
            backgroundImageUri = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=2071&auto=format&fit=crop",
            backgroundImageBlur = 10f,
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Clean\nFraming",
            subheading = "Minimal designs for maximum impact",
            headingFont = TextFont.PLAYFAIR.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 95f,
            subheadingSize = 34f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.8f,
            rotation = 0f,
            frameOffsetX = 280f,
            frameOffsetY = 0f,
            textOffsetX = 0f,
            textOffsetY = 0f
        )
    ),
    StaticTemplate(
        id = "updatium_blue",
        name = "Tech Blue",
        config = EditorConfig(
            selectedDeviceName = "MacBook Pro",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF000428.toInt(),
            gradientColors = listOf(0xFF000428.toInt(), 0xFF004E92.toInt()),
            screenBackgroundColor = 0xFF121212.toInt(),
            heading = "Desktop\nMockup",
            subheading = "Present your web apps beautifully",
            headingFont = TextFont.BEBAS.name,
            subheadingFont = TextFont.QUICKSAND.name,
            headingSize = 110f,
            subheadingSize = 38f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.75f,
            rotation = 0f,
            frameOffsetX = 240f,
            frameOffsetY = 0f,
            textOffsetX = 0f,
            textOffsetY = 0f
        )
    ),
    StaticTemplate(
        id = "sunset_tab",
        name = "Sunset Tablet",
        config = EditorConfig(
            selectedDeviceName = "iPad Pro 13\"",
            backgroundType = BackgroundType.IMAGE.name,
            backgroundColor = 0xFF000000.toInt(),
            gradientColors = listOf(0xFF000000.toInt(), 0xFF000000.toInt()),
            backgroundImageUri = "https://images.unsplash.com/photo-1519750783826-e2420f4d687f?q=80&w=1974&auto=format&fit=crop",
            backgroundImageBlur = 5f,
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Infinite\nStyles",
            subheading = "Unlimited possibilities for your brand",
            headingFont = TextFont.POPPINS.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 85f,
            subheadingSize = 32f,
            textColor = 0xFFFCE4EC.toInt(),
            textAlign = TextAlignLabel.RIGHT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.75f,
            rotation = 0f,
            frameOffsetX = -280f,
            frameOffsetY = 0f,
            textOffsetX = 0f,
            textOffsetY = 0f
        )
    ),
    StaticTemplate(
        id = "classic_retro",
        name = "Retro Vibe",
        config = EditorConfig(
            selectedDeviceName = "iPhone X",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF232526.toInt(),
            gradientColors = listOf(0xFF232526.toInt(), 0xFF414345.toInt()),
            screenBackgroundColor = 0xFF121212.toInt(),
            heading = "Classic\nFrames",
            subheading = "A touch of nostalgia in modern UI",
            headingFont = TextFont.INTER.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 90f,
            subheadingSize = 36f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 1.1f,
            rotation = 0f,
            frameOffsetX = 0f,
            frameOffsetY = 200f,
            textOffsetX = 0f,
            textOffsetY = -240f
        )
    ),
    StaticTemplate(
        id = "lime_audio",
        name = "Lime Pulse",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.SOLID.name,
            backgroundColor = 0xFFD4E157.toInt(),
            gradientColors = listOf(0xFFD4E157.toInt(), 0xFFD4E157.toInt()),
            screenBackgroundColor = 0xFF121212.toInt(),
            heading = "Social\nReady",
            subheading = "Share your designs on any platform",
            headingFont = TextFont.MONTSERRAT.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 85f,
            subheadingSize = 32f,
            textColor = 0xFF1A1A1D.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.9f,
            rotation = 0f,
            frameOffsetX = 260f,
            frameOffsetY = 200f,
            textOffsetX = 0f,
            textOffsetY = -240f,
            textZIndex = -1
        )
    )
)
