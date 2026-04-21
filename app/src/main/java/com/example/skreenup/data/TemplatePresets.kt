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
        id = "mobile_1",
        name = "Vibrant Side",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF6200EE.toInt(),
            gradientColors = listOf(0xFF6200EE.toInt(), 0xFF03DAC6.toInt()),
            screenBackgroundColor = 0xFF121212.toInt(),
            heading = "Modern\nDesign",
            subheading = "Build beautiful mockups in seconds",
            headingFont = TextFont.POPPINS.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 80f,
            subheadingSize = 32f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.PORTRAIT.name,
            scale = 0.9f,
            rotation = -8f,
            frameOffsetX = 180f,
            frameOffsetY = 50f,
            textOffsetX = -250f,
            textOffsetY = -150f,
            textGap = 10f,
            headingBold = true,
            subheadingBold = false,
            textZIndex = -1
        )
    ),
    StaticTemplate(
        id = "mobile_2",
        name = "Elegant Soft",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFFFAD0C4.toInt(),
            gradientColors = listOf(0xFFFAD0C4.toInt(), 0xFFFFD1FF.toInt()),
            screenBackgroundColor = 0xFFFFFFFF.toInt(),
            heading = "Pure\nSimplicity",
            subheading = "Focus on what matters most",
            headingFont = TextFont.MONTSERRAT.name,
            subheadingFont = TextFont.RALEWAY.name,
            headingSize = 72f,
            subheadingSize = 34f,
            textColor = 0xFF333333.toInt(),
            textAlign = TextAlignLabel.RIGHT.name,
            aspectRatio = CompositionAspectRatio.PORTRAIT.name,
            scale = 0.85f,
            rotation = 5f,
            frameOffsetX = -200f,
            frameOffsetY = 80f,
            textOffsetX = 220f,
            textOffsetY = -200f,
            textGap = 15f
        )
    ),
    StaticTemplate(
        id = "mobile_3",
        name = "Dark Premium",
        config = EditorConfig(
            selectedDeviceName = "Galaxy S26 Ultra",
            backgroundType = BackgroundType.SOLID.name,
            backgroundColor = 0xFF121212.toInt(),
            gradientColors = listOf(0xFF121212.toInt(), 0xFF2C2C2C.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Elite Performance",
            subheading = "Uncompromising power & style",
            headingFont = TextFont.ANTON.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 85f,
            subheadingSize = 38f,
            textColor = 0xFFBB86FC.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.PORTRAIT.name,
            scale = 1.1f,
            rotation = 0f,
            frameOffsetX = 0f,
            frameOffsetY = 250f,
            textOffsetX = 0f,
            textOffsetY = -350f,
            textShadow = true
        )
    ),
    StaticTemplate(
        id = "laptop_1",
        name = "Studio Angle",
        config = EditorConfig(
            selectedDeviceName = "MacBook Pro",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF232526.toInt(),
            gradientColors = listOf(0xFF232526.toInt(), 0xFF414345.toInt()),
            screenBackgroundColor = 0xFF121212.toInt(),
            heading = "Creative Pro",
            subheading = "Designed for high-end results",
            headingFont = TextFont.PLAYFAIR.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 90f,
            subheadingSize = 36f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.LEFT.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.8f,
            rotation = -12f,
            frameOffsetX = 150f,
            frameOffsetY = 0f,
            textOffsetX = -250f,
            textOffsetY = 0f,
            textZIndex = -1
        )
    ),
    StaticTemplate(
        id = "laptop_2",
        name = "Tech Horizon",
        config = EditorConfig(
            selectedDeviceName = "MacBook Pro",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF00B4DB.toInt(),
            gradientColors = listOf(0xFF00B4DB.toInt(), 0xFF0083B0.toInt()),
            screenBackgroundColor = 0xFFFFFFFF.toInt(),
            heading = "Future Forward",
            subheading = "Innovative solutions for tomorrow",
            headingFont = TextFont.BEBAS.name,
            subheadingFont = TextFont.QUICKSAND.name,
            headingSize = 110f,
            subheadingSize = 42f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.75f,
            rotation = 0f,
            frameOffsetX = 0f,
            frameOffsetY = 100f,
            textOffsetX = 0f,
            textOffsetY = -300f
        )
    ),
    StaticTemplate(
        id = "pc_1",
        name = "Gaming Pulse",
        config = EditorConfig(
            selectedDeviceName = "Monitor 27\"",
            backgroundType = BackgroundType.SOLID.name,
            backgroundColor = 0xFF1A1A1D.toInt(),
            gradientColors = listOf(0xFF1A1A1D.toInt(), 0xFFC3073F.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Total Immersion",
            subheading = "Level up your presentation",
            headingFont = TextFont.OSWALD.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 95f,
            subheadingSize = 40f,
            textColor = 0xFFC3073F.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.7f,
            rotation = 0f,
            frameOffsetX = 0f,
            frameOffsetY = 120f,
            textOffsetX = 0f,
            textOffsetY = -320f
        )
    ),
    StaticTemplate(
        id = "mobile_landscape",
        name = "Cinema Wide",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF000000.toInt(),
            gradientColors = listOf(0xFF0F2027.toInt(), 0xFF203A43.toInt(), 0xFF2C5364.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Cinematic Experience",
            subheading = "A new perspective in landscape",
            headingFont = TextFont.PACIFICO.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 80f,
            subheadingSize = 34f,
            textColor = 0xFFD4AF37.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.85f,
            rotation = 90f,
            frameOffsetX = 0f,
            frameOffsetY = 100f,
            textOffsetX = 0f,
            textOffsetY = -300f
        )
    ),
    StaticTemplate(
        id = "tablet_1",
        name = "Studio Canvas",
        config = EditorConfig(
            selectedDeviceName = "iPad Pro 13\"",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF11998E.toInt(),
            gradientColors = listOf(0xFF11998E.toInt(), 0xFF38EF7D.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Creative Studio",
            subheading = "Your complete mobile workstation",
            headingFont = TextFont.LIBRE_BASKERVILLE.name,
            subheadingFont = TextFont.QUICKSAND.name,
            headingSize = 85f,
            subheadingSize = 40f,
            textColor = 0xFF004D40.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.TABLET.name,
            scale = 0.75f,
            rotation = -5f,
            frameOffsetX = 50f,
            frameOffsetY = 80f,
            textOffsetX = -50f,
            textOffsetY = -300f
        )
    )
)
