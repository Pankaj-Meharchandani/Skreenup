package com.example.skreenup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.components.MockupRenderer.drawMockup

@Composable
fun DeviceFrame(
    screenshot: ImageBitmap?,
    deviceModel: DeviceModel,
    backgroundType: BackgroundType = BackgroundType.SOLID,
    backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    gradientColors: List<androidx.compose.ui.graphics.Color> = listOf(androidx.compose.ui.graphics.Color(0xFF3F51B5), androidx.compose.ui.graphics.Color(0xFF006A6A)),
    scale: Float = 0.8f,
    imageScale: Float = 1.0f,
    frameOffsetX: Float = 0f,
    frameOffsetY: Float = 0f,
    screenshotOffsetX: Float = 0f,
    screenshotOffsetY: Float = 0f,
    aspectRatio: CompositionAspectRatio = CompositionAspectRatio.SQUARE,
    backgroundImage: ImageBitmap? = null,
    showWatermark: Boolean = false,
    watermarkText: String = "",
    rotationDegrees: Float = 0f,
    screenshotRotation: Float = 0f,
    screenBackgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    text: String = "",
    textFont: com.example.skreenup.ui.models.TextFont = com.example.skreenup.ui.models.TextFont.ROBOTO,
    textSize: Float = 48f,
    textColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    textOffsetX: Float = 0f,
    textOffsetY: Float = 0f,
    textAlign: com.example.skreenup.ui.models.TextAlignLabel = com.example.skreenup.ui.models.TextAlignLabel.CENTER,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    isUnderline: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawMockup(
                screenshot = screenshot,
                deviceModel = deviceModel,
                backgroundType = backgroundType,
                backgroundColor = backgroundColor,
                gradientColors = gradientColors,
                backgroundImage = backgroundImage,
                scale = scale,
                imageScale = imageScale,
                frameOffsetX = frameOffsetX,
                frameOffsetY = frameOffsetY,
                screenshotOffsetX = screenshotOffsetX,
                screenshotOffsetY = screenshotOffsetY,
                aspectRatio = aspectRatio,
                showWatermark = showWatermark,
                watermarkText = watermarkText,
                screenBackgroundColor = screenBackgroundColor,
                isExport = false,
                rotationDegrees = rotationDegrees,
                screenshotRotation = screenshotRotation,
                text = text,
                textFont = textFont,
                textFontSize = textSize,
                textColor = textColor,
                textOffsetX = textOffsetX,
                textOffsetY = textOffsetY,
                textAlignment = textAlign,
                isBold = isBold,
                isItalic = isItalic,
                isUnderline = isUnderline
            )
        }
    }
}
