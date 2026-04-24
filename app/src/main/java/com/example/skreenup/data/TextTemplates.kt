package com.example.skreenup.data

import com.example.skreenup.ui.models.TextLayer
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.models.TextFont

data class TextTemplate(
    val id: String,
    val name: String,
    val layer: TextLayer
)

val PRESET_TEXT_TEMPLATES = listOf(
    TextTemplate(
        id = "modern_hero",
        name = "Modern Hero",
        layer = TextLayer(
            heading = "Heading Here",
            subheading = "Subheading description goes here",
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
        layer = TextLayer(
            heading = "BOLD IMPACT",
            subheading = "Sleek subtitle",
            headingFont = TextFont.ANTON.name,
            subheadingFont = TextFont.INTER.name,
            headingSize = 90f,
            subheadingSize = 30f,
            textGap = 15f,
            headingBold = true,
            subheadingBold = true
        )
    ),
    TextTemplate(
        id = "elegant_serif",
        name = "Elegant Serif",
        layer = TextLayer(
            heading = "Elegant Design",
            subheading = "Premium presentation",
            headingFont = TextFont.PLAYFAIR.name,
            subheadingFont = TextFont.LIBRE_BASKERVILLE.name,
            headingSize = 75f,
            subheadingSize = 28f,
            textGap = 25f,
            headingBold = true,
            subheadingBold = false
        )
    ),
    TextTemplate(
        id = "minimal_clean",
        name = "Minimal Clean",
        layer = TextLayer(
            heading = "Clean",
            subheading = "Simple & Minimal",
            headingFont = TextFont.MONTSERRAT.name,
            subheadingFont = TextFont.MONTSERRAT.name,
            headingSize = 70f,
            subheadingSize = 24f,
            textGap = 10f,
            headingBold = false,
            subheadingBold = false
        )
    ),
    TextTemplate(
        id = "bebas_stack",
        name = "Bebas Stack",
        layer = TextLayer(
            heading = "STACKED\nHEADING",
            subheading = "Subheading text",
            headingFont = TextFont.BEBAS.name,
            subheadingFont = TextFont.OSWALD.name,
            headingSize = 100f,
            subheadingSize = 32f,
            textGap = 20f,
            headingBold = true,
            subheadingBold = false
        )
    )
)
