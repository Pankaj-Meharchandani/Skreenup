package com.example.skreenup.data

import com.example.skreenup.ui.models.OverlayLayer
import com.example.skreenup.ui.models.OverlayType
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.models.TextFont
import com.example.skreenup.ui.models.TextBackgroundStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class TextTemplate(
    val id: String,
    val name: String,
    val description: String,
    val layer: OverlayLayer
)

val PRESET_TEXT_TEMPLATES = listOf(
    TextTemplate(
        id = "modern_hero",
        name = "Modern Hero",
        description = "Clean and versatile",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "Modern Design",
            subheading = "Perfect for any app showcase",
            headingFont = TextFont.POPPINS.name,
            subheadingFont = TextFont.POPPINS.name,
            headingSize = 80f,
            subheadingSize = 35f,
            textGap = 20f,
            headingBold = true,
            subheadingBold = false
        )
    ),
    TextTemplate(
        id = "impact_bold",
        name = "Impact Bold",
        description = "Grab attention fast",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "BOLD IMPACT",
            subheading = "Make a strong statement",
            headingFont = TextFont.ANTON.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 90f,
            subheadingSize = 32f,
            textGap = 15f,
            headingBold = true,
            subheadingBold = true
        )
    ),
    TextTemplate(
        id = "elegant_serif",
        name = "Elegant Serif",
        description = "Premium look",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "Elegant Style",
            subheading = "Luxurious feel for high-end products",
            headingFont = TextFont.PLAYFAIR.name,
            subheadingFont = TextFont.LIBRE_BASKERVILLE.name,
            headingSize = 75f,
            subheadingSize = 28f,
            textGap = 35f,
            headingBold = true,
            subheadingBold = false
        )
    ),
    TextTemplate(
        id = "minimal_clean",
        name = "Minimal Clean",
        description = "Focus on clarity",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "Simple",
            subheading = "Minimalism at its best",
            headingFont = TextFont.MONTSERRAT.name,
            subheadingFont = TextFont.MONTSERRAT.name,
            headingSize = 70f,
            subheadingSize = 24f,
            textGap = 12f,
            headingBold = false,
            subheadingBold = false
        )
    ),
    TextTemplate(
        id = "bebas_stack",
        name = "Bebas Stack",
        description = "Professional title",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "DISPLAY\nTITLE",
            subheading = "Condensed typography style",
            headingFont = TextFont.BEBAS.name,
            subheadingFont = TextFont.OSWALD.name,
            headingSize = 100f,
            subheadingSize = 34f,
            textGap = 10f,
            headingBold = true,
            subheadingBold = false
        )
    ),
    TextTemplate(
        id = "glass_badge",
        name = "Glass Badge",
        description = "Modern translucent feel",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "NEW FEATURE",
            subheading = "Check out the latest update",
            headingFont = TextFont.INTER.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 40f,
            subheadingSize = 24f,
            textGap = 10f,
            headingBold = true,
            backgroundStyle = TextBackgroundStyle.GLASS.name,
            backgroundPadding = 40f,
            backgroundCornerRadius = 32f
        )
    ),
    TextTemplate(
        id = "filled_label",
        name = "Filled Label",
        description = "High contrast tag",
        layer = OverlayLayer(
            type = OverlayType.TEXT,
            heading = "GET STARTED",
            subheading = "Quick setup guide",
            headingFont = TextFont.POPPINS.name,
            subheadingFont = TextFont.POPPINS.name,
            headingSize = 45f,
            subheadingSize = 22f,
            textGap = 8f,
            headingBold = true,
            color = 0xFFFFFFFF.toInt(),
            backgroundStyle = TextBackgroundStyle.FILLED.name,
            backgroundColor = 0xFF3F51B5.toInt(),
            backgroundAlpha = 1.0f,
            backgroundPadding = 30f,
            backgroundCornerRadius = 12f
        )
    )
)
