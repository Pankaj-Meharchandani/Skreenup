package com.example.skreenup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
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
    backgroundImageOffsetX: Float = 0f,
    backgroundImageOffsetY: Float = 0f,
    backgroundImageScale: Float = 1.0f,
    backgroundImageBlur: Float = 0f,
    showWatermark: Boolean = false,
    watermarkText: String = "",
    rotationDegrees: Float = 0f,
    screenshotRotation: Float = 0f,
    screenBackgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    heading: String = "",
    subheading: String = "",
    headingFont: com.example.skreenup.ui.models.TextFont = com.example.skreenup.ui.models.TextFont.POPPINS,
    subheadingFont: com.example.skreenup.ui.models.TextFont = com.example.skreenup.ui.models.TextFont.POPPINS,
    headingSize: Float = 60f,
    subheadingSize: Float = 40f,
    textGap: Float = 20f,
    textColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    textOffsetX: Float = 0f,
    textOffsetY: Float = 0f,
    textAlign: com.example.skreenup.ui.models.TextAlignLabel = com.example.skreenup.ui.models.TextAlignLabel.CENTER,
    headingBold: Boolean = true,
    subheadingBold: Boolean = false,
    showReflection: Boolean = true,
    onScaleChange: (Float) -> Unit = {},
    onFrameOffsetChange: (Float, Float) -> Unit = { _, _ -> },
    onTextOffsetChange: (Float, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var activeTarget by remember { mutableStateOf<Target>(Target.NONE) }

    // Use rememberUpdatedState to avoid restarting pointerInput when values change
    val currentScale by rememberUpdatedState(scale)
    val currentFrameOffsetX by rememberUpdatedState(frameOffsetX)
    val currentFrameOffsetY by rememberUpdatedState(frameOffsetY)
    val currentTextOffsetX by rememberUpdatedState(textOffsetX)
    val currentTextOffsetY by rememberUpdatedState(textOffsetY)
    val currentHeading by rememberUpdatedState(heading)
    val currentSubheading by rememberUpdatedState(subheading)
    val currentDevice by rememberUpdatedState(deviceModel)
    val currentRatio by rememberUpdatedState(aspectRatio)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .pointerInput(canvasSize) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    // Determine target on first movement if not set
                    if (activeTarget == Target.NONE) {
                        activeTarget = hitTest(
                            point = centroid,
                            canvasSize = canvasSize,
                            aspectRatio = currentRatio,
                            deviceModel = currentDevice,
                            scale = currentScale,
                            frameOffsetX = currentFrameOffsetX,
                            frameOffsetY = currentFrameOffsetY,
                            heading = currentHeading,
                            subheading = currentSubheading,
                            textOffsetX = currentTextOffsetX,
                            textOffsetY = currentTextOffsetY
                        )
                    }

                    // Handle Zoom (always affects frame scale in this context)
                    if (zoom != 1f) {
                        onScaleChange((currentScale * zoom).coerceIn(0.1f, 2.0f))
                    }

                    // Handle Pan
                    if (pan != Offset.Zero) {
                        val compWidth = if (canvasSize.width.toFloat() / canvasSize.height.toFloat() > currentRatio.ratio) {
                            canvasSize.height * currentRatio.ratio
                        } else {
                            canvasSize.width.toFloat()
                        }
                        // Normalize pan to "design space" (1000px width)
                        val normalizeFactor = 1000f / compWidth
                        val dx = pan.x * normalizeFactor
                        val dy = pan.y * normalizeFactor

                        when (activeTarget) {
                            Target.FRAME -> onFrameOffsetChange(currentFrameOffsetX + dx, currentFrameOffsetY + dy)
                            Target.TEXT -> onTextOffsetChange(currentTextOffsetX + dx, currentTextOffsetY + dy)
                            else -> {
                                // Default to frame if nothing hit or ambiguous
                                onFrameOffsetChange(currentFrameOffsetX + dx, currentFrameOffsetY + dy)
                            }
                        }
                    }
                }
            }
            // Reset target when all fingers are lifted
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.all { !it.pressed }) {
                            activeTarget = Target.NONE
                        }
                    }
                }
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
                backgroundImageOffsetX = backgroundImageOffsetX,
                backgroundImageOffsetY = backgroundImageOffsetY,
                backgroundImageScale = backgroundImageScale,
                backgroundImageBlur = backgroundImageBlur,
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
                heading = heading,
                subheading = subheading,
                headingFont = headingFont,
                subheadingFont = subheadingFont,
                headingSize = headingSize,
                subheadingSize = subheadingSize,
                textGap = textGap,
                textColor = textColor,
                textOffsetX = textOffsetX,
                textOffsetY = textOffsetY,
                textAlignment = textAlign,
                headingBold = headingBold,
                subheadingBold = subheadingBold,
                showReflection = showReflection
            )
        }
    }
}

private enum class Target { NONE, FRAME, TEXT }

private fun hitTest(
    point: Offset,
    canvasSize: IntSize,
    aspectRatio: CompositionAspectRatio,
    deviceModel: DeviceModel,
    scale: Float,
    frameOffsetX: Float,
    frameOffsetY: Float,
    heading: String,
    subheading: String,
    textOffsetX: Float,
    textOffsetY: Float
): Target {
    val canvasWidth = canvasSize.width.toFloat()
    val canvasHeight = canvasSize.height.toFloat()
    if (canvasWidth <= 0 || canvasHeight <= 0) return Target.NONE

    // 1. Calculate comp area (Same logic as MockupRenderer)
    val compWidth: Float
    val compHeight: Float
    if (canvasWidth / canvasHeight > aspectRatio.ratio) {
        compHeight = canvasHeight
        compWidth = compHeight * aspectRatio.ratio
    } else {
        compWidth = canvasWidth
        compHeight = compWidth / aspectRatio.ratio
    }
    val compLeft = (canvasWidth - compWidth) / 2
    val compTop = (canvasHeight - compHeight) / 2
    val exportScaleFactor = compWidth / 1000f

    // 2. Check Text Hit (Rough bounding box)
    if (heading.isNotEmpty() || subheading.isNotEmpty()) {
        val textCenterX = compLeft + compWidth / 2 + (textOffsetX * exportScaleFactor)
        val textCenterY = compTop + compHeight / 2 + (textOffsetY * exportScaleFactor)
        
        // Heuristic: touchable area for text
        val textHitRect = Rect(
            left = textCenterX - 300f * exportScaleFactor,
            right = textCenterX + 300f * exportScaleFactor,
            top = textCenterY - 100f * exportScaleFactor,
            bottom = textCenterY + 100f * exportScaleFactor
        )

        if (textHitRect.contains(point)) return Target.TEXT
    }

    // 3. Check Frame Hit
    val frameAspectRatio = deviceModel.aspectRatio
    val frameWidth: Float
    val frameHeight: Float
    if (compWidth / compHeight > frameAspectRatio) {
        frameHeight = compHeight * scale
        frameWidth = frameHeight * frameAspectRatio
    } else {
        frameWidth = compWidth * scale
        frameHeight = frameWidth / frameAspectRatio
    }

    val currentFrameOffsetX = frameOffsetX * exportScaleFactor
    val currentFrameOffsetY = frameOffsetY * exportScaleFactor
    val frameLeft = compLeft + (compWidth - frameWidth) / 2 + currentFrameOffsetX
    val frameTop = compTop + (compHeight - frameHeight) / 2 + currentFrameOffsetY
    
    val frameRect = Rect(Offset(frameLeft, frameTop), androidx.compose.ui.geometry.Size(frameWidth, frameHeight))
    if (frameRect.contains(point)) return Target.FRAME

    return Target.NONE
}
