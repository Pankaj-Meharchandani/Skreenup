package com.example.skreenup.data

import com.example.skreenup.ui.models.TextLayer
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.models.TextFont

data class TextTemplate(
    val id: String,
    val name: String,
    val description: String,
    val layer: TextLayer
)

val PRESET_TEXT_TEMPLATES = listOf(
    TextTemplate(
        id = "modern_hero",
        name = "Modern Hero",
        description = "Clean and versatile",
        layer = TextLayer(
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
        layer = TextLayer(
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
        layer = TextLayer(
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
        layer = TextLayer(
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
        layer = TextLayer(
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
    )
)
