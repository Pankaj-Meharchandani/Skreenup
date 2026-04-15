package com.example.skreenup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

enum class FrameType {
    ANDROID_PHONE,
    IPHONE,
    TABLET,
    DESKTOP
}

enum class BackgroundType {
    SOLID,
    GRADIENT,
    BLUR
}

enum class CompositionAspectRatio(val ratio: Float, val label: String) {
    SQUARE(1f, "1:1"),
    PORTRAIT(9f / 16f, "9:16"),
    LANDSCAPE(16f / 9f, "16:9"),
    TABLET(4f / 3f, "4:3")
}

@Composable
fun DeviceFrame(
    screenshot: ImageBitmap?,
    frameType: FrameType,
    backgroundType: BackgroundType = BackgroundType.SOLID,
    backgroundColor: Color = Color.White,
    gradientColors: List<Color> = listOf(Color(0xFF3F51B5), Color(0xFF006A6A)),
    scale: Float = 0.8f,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    aspectRatio: CompositionAspectRatio = CompositionAspectRatio.SQUARE,
    showWatermark: Boolean = false,
    watermarkText: String = "",
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate composition area based on aspect ratio
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
            val compRect = Rect(Offset(compLeft, compTop), Size(compWidth, compHeight))

            // 1. Draw Background
            clipPath(Path().apply { addRect(compRect) }) {
                when (backgroundType) {
                    BackgroundType.SOLID -> {
                        drawRect(color = backgroundColor, topLeft = Offset(compLeft, compTop), size = Size(compWidth, compHeight))
                    }
                    BackgroundType.GRADIENT -> {
                        drawRect(
                            brush = Brush.linearGradient(colors = gradientColors),
                            topLeft = Offset(compLeft, compTop),
                            size = Size(compWidth, compHeight)
                        )
                    }
                    BackgroundType.BLUR -> {
                        if (screenshot != null) {
                            drawImage(
                                image = screenshot,
                                dstOffset = IntOffset(compLeft.toInt(), compTop.toInt()),
                                dstSize = IntSize(compWidth.toInt(), compHeight.toInt()),
                                alpha = 0.5f,
                                blendMode = BlendMode.SrcOver
                            )
                            // Add a semi-transparent overlay to enhance the "blur" feel
                            drawRect(
                                color = Color.White.copy(alpha = 0.3f),
                                topLeft = Offset(compLeft, compTop),
                                size = Size(compWidth, compHeight)
                            )
                        } else {
                            drawRect(color = Color.LightGray, topLeft = Offset(compLeft, compTop), size = Size(compWidth, compHeight))
                        }
                    }
                }
            }

            // 2. Calculate frame dimensions
            val frameAspectRatio = when (frameType) {
                FrameType.ANDROID_PHONE, FrameType.IPHONE -> 9f / 19.5f
                FrameType.TABLET -> 4f / 3f
                FrameType.DESKTOP -> 16f / 10f
            }

            var frameWidth: Float
            var frameHeight: Float

            if (compWidth / compHeight > frameAspectRatio) {
                frameHeight = compHeight * scale
                frameWidth = frameHeight * frameAspectRatio
            } else {
                frameWidth = compWidth * scale
                frameHeight = frameWidth / frameAspectRatio
            }

            val frameLeft = compLeft + (compWidth - frameWidth) / 2 + offsetX
            val frameTop = compTop + (compHeight - frameHeight) / 2 + offsetY
            val frameRect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, frameHeight))

            val cornerRadiusValue = with(density) { 32.dp.toPx() } // Using fixed for phone, simplify for now

            val framePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = frameRect,
                        cornerRadius = CornerRadius(cornerRadiusValue)
                    )
                )
            }

            // 3. Draw Shadow
            drawPath(
                path = framePath,
                color = Color.Black.copy(alpha = 0.3f),
                style = Stroke(width = with(density) { 12.dp.toPx() })
            )

            // 4. Clip and Draw Screenshot
            clipPath(framePath) {
                if (screenshot != null) {
                    drawImage(
                        image = screenshot,
                        dstOffset = IntOffset(frameLeft.toInt(), frameTop.toInt()),
                        dstSize = IntSize(frameWidth.toInt(), frameHeight.toInt()),
                        blendMode = BlendMode.SrcOver
                    )
                    
                    // Reflection Effect (Subtle Gradient Overlay)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent, Color.White.copy(alpha = 0.05f)),
                            startY = frameTop,
                            endY = frameTop + frameHeight
                        ),
                        topLeft = Offset(frameLeft, frameTop),
                        size = Size(frameWidth, frameHeight),
                        blendMode = BlendMode.Screen
                    )
                } else {
                    drawRect(
                        color = Color.DarkGray,
                        topLeft = Offset(frameLeft, frameTop),
                        size = Size(frameWidth, frameHeight)
                    )
                }
            }

            // 5. Draw Frame Border
            drawPath(
                path = framePath,
                color = Color.Black,
                style = Stroke(width = with(density) { 4.dp.toPx() })
            )

            // 6. Draw "Speaker" or "Notch" for phones
            if (frameType == FrameType.ANDROID_PHONE || frameType == FrameType.IPHONE) {
                val speakerWidth = frameWidth * 0.2f
                val speakerHeight = with(density) { 4.dp.toPx() }
                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(frameLeft + (frameWidth - speakerWidth) / 2, frameTop + with(density) { 12.dp.toPx() }),
                    size = Size(speakerWidth, speakerHeight),
                    cornerRadius = CornerRadius(with(density) { 2.dp.toPx() })
                )
            }

            // 7. Draw Watermark
            if (showWatermark && watermarkText.isNotEmpty()) {
                val paint = android.graphics.Paint().apply {
                    color = Color.White.copy(alpha = 0.7f).toArgb()
                    textSize = with(density) { 16.dp.toPx() }
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    watermarkText,
                    compLeft + compWidth - with(density) { 16.dp.toPx() },
                    compTop + compHeight - with(density) { 16.dp.toPx() },
                    paint
                )
            }
        }
    }
}
