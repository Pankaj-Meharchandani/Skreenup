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
        name = "Neon Night",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF3F51B5.toInt(),
            gradientColors = listOf(0xFF3F51B5.toInt(), 0xFF006A6A.toInt()),
            screenBackgroundColor = 0xFF121212.toInt(),
            heading = "Modern Elegance",
            subheading = "Redefining the mobile experience",
            headingFont = TextFont.POPPINS.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 72f,
            subheadingSize = 40f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.PORTRAIT.name,
            scale = 0.85f,
            rotation = 0f
        )
    ),
    StaticTemplate(
        id = "mobile_2",
        name = "Soft Pastel",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFFFFD1FF.toInt(),
            gradientColors = listOf(0xFFFFD1FF.toInt(), 0xFFFAE1CB.toInt()),
            screenBackgroundColor = 0xFFFFFFFF.toInt(),
            heading = "Light & Airy",
            subheading = "Minimalism in every pixel",
            headingFont = TextFont.MONTSERRAT.name,
            subheadingFont = TextFont.RALEWAY.name,
            headingSize = 64f,
            subheadingSize = 36f,
            textColor = 0xFF4A4A4A.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.PORTRAIT.name,
            scale = 0.8f,
            rotation = 0f
        )
    ),
    StaticTemplate(
        id = "mobile_3",
        name = "Deep Stealth",
        config = EditorConfig(
            selectedDeviceName = "Galaxy S26 Ultra",
            backgroundType = BackgroundType.SOLID.name,
            backgroundColor = 0xFF1A1A1B.toInt(),
            gradientColors = listOf(0xFF1A1A1B.toInt(), 0xFF000000.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Power Unleashed",
            subheading = "Maximum performance",
            headingFont = TextFont.ANTON.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 80f,
            subheadingSize = 44f,
            textColor = 0xFFE0E0E0.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.PORTRAIT.name,
            scale = 0.9f,
            rotation = 0f
        )
    ),
    StaticTemplate(
        id = "laptop_1",
        name = "Pro Studio",
        config = EditorConfig(
            selectedDeviceName = "MacBook Pro",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFFE0E0E0.toInt(),
            gradientColors = listOf(0xFFE0E0E0.toInt(), 0xFFBDBDBD.toInt()),
            screenBackgroundColor = 0xFF2C2C2C.toInt(),
            heading = "Creative Workspace",
            subheading = "For the professionals",
            headingFont = TextFont.PLAYFAIR.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 80f,
            subheadingSize = 40f,
            textColor = 0xFF212121.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.75f,
            rotation = 0f
        )
    ),
    StaticTemplate(
        id = "laptop_2",
        name = "Creative Flow",
        config = EditorConfig(
            selectedDeviceName = "MacBook Pro",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFFFF5722.toInt(),
            gradientColors = listOf(0xFFFF5722.toInt(), 0xFF673AB7.toInt()),
            screenBackgroundColor = 0xFFFFFFFF.toInt(),
            heading = "Push Boundaries",
            subheading = "Design without limits",
            headingFont = TextFont.BEBAS.name,
            subheadingFont = TextFont.QUICKSAND.name,
            headingSize = 100f,
            subheadingSize = 48f,
            textColor = 0xFFFFFFFF.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.7f,
            rotation = -5f
        )
    ),
    StaticTemplate(
        id = "pc_1",
        name = "Ultra Wide",
        config = EditorConfig(
            selectedDeviceName = "Monitor 27\"",
            backgroundType = BackgroundType.SOLID.name,
            backgroundColor = 0xFF0D47A1.toInt(),
            gradientColors = listOf(0xFF0D47A1.toInt(), 0xFF1976D2.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Immersive Gaming",
            subheading = "Experience every detail",
            headingFont = TextFont.OSWALD.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 90f,
            subheadingSize = 42f,
            textColor = 0xFFBBDEFB.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.65f,
            rotation = 0f
        )
    ),
    StaticTemplate(
        id = "mobile_landscape",
        name = "Cinematic Shot",
        config = EditorConfig(
            selectedDeviceName = "iPhone 17 Pro Max",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF000000.toInt(),
            gradientColors = listOf(0xFF000000.toInt(), 0xFF212121.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Landscape Masterpiece",
            subheading = "Capture the world in wide",
            headingFont = TextFont.PACIFICO.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 84f,
            subheadingSize = 38f,
            textColor = 0xFFC0CA33.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.LANDSCAPE.name,
            scale = 0.8f,
            rotation = 90f
        )
    ),
    StaticTemplate(
        id = "tablet_1",
        name = "Digital Canvas",
        config = EditorConfig(
            selectedDeviceName = "iPad Pro 13\"",
            backgroundType = BackgroundType.GRADIENT.name,
            backgroundColor = 0xFF4A148C.toInt(),
            gradientColors = listOf(0xFF4A148C.toInt(), 0xFF880E4F.toInt()),
            screenBackgroundColor = 0xFF000000.toInt(),
            heading = "Tablet Perfection",
            subheading = "Your studio on the go",
            headingFont = TextFont.LIBRE_BASKERVILLE.name,
            subheadingFont = TextFont.QUICKSAND.name,
            headingSize = 78f,
            subheadingSize = 42f,
            textColor = 0xFFF3E5F5.toInt(),
            textAlign = TextAlignLabel.CENTER.name,
            aspectRatio = CompositionAspectRatio.TABLET.name,
            scale = 0.7f,
            rotation = 0f
        )
    )
)
