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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.skreenup.ui.models.CutoutType
import com.example.skreenup.ui.models.DeviceModel

enum class FrameType {
    ANDROID_PHONE,
    IPHONE,
    TABLET,
    DESKTOP
}

enum class BackgroundType {
    SOLID,
    GRADIENT,
    BLUR,
    IMAGE
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
    deviceModel: DeviceModel,
    backgroundType: BackgroundType = BackgroundType.SOLID,
    backgroundColor: Color = Color.White,
    gradientColors: List<Color> = listOf(Color(0xFF3F51B5), Color(0xFF006A6A)),
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
                            drawRect(
                                color = Color.White.copy(alpha = 0.3f),
                                topLeft = Offset(compLeft, compTop),
                                size = Size(compWidth, compHeight)
                            )
                        } else {
                            drawRect(color = Color.LightGray, topLeft = Offset(compLeft, compTop), size = Size(compWidth, compHeight))
                        }
                    }
                    BackgroundType.IMAGE -> {
                        if (backgroundImage != null) {
                            drawImage(
                                image = backgroundImage,
                                dstOffset = IntOffset(compLeft.toInt(), compTop.toInt()),
                                dstSize = IntSize(compWidth.toInt(), compHeight.toInt()),
                                blendMode = BlendMode.SrcOver
                            )
                        } else {
                            drawRect(color = Color.LightGray, topLeft = Offset(compLeft, compTop), size = Size(compWidth, compHeight))
                        }
                    }
                }
            }

            // 2. Calculate frame dimensions
            val frameAspectRatio = deviceModel.aspectRatio
            var frameWidth: Float
            var frameHeight: Float

            if (compWidth / compHeight > frameAspectRatio) {
                frameHeight = compHeight * scale
                frameWidth = frameHeight * frameAspectRatio
            } else {
                frameWidth = compWidth * scale
                frameHeight = frameWidth / frameAspectRatio
            }

            // Apply Frame Offsets
            val frameLeft = compLeft + (compWidth - frameWidth) / 2 + frameOffsetX
            val frameTop = compTop + (compHeight - frameHeight) / 2 + frameOffsetY
            val frameRect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, frameHeight))

            val pixelScale = frameWidth / deviceModel.widthMm
            
            // Refined Corner Radius Calculation (pt to mm to px)
            // 1pt = 0.3527mm
            val cornerRadiusPx = (deviceModel.cornerRadiusDp * 0.3527f) * pixelScale

            // 2.1 Draw Laptop Chassis
            if (deviceModel.hasChassis && deviceModel.type == FrameType.DESKTOP) {
                val chassisHeightPx = 8 * pixelScale
                val chassisWidthPx = frameWidth * 1.15f
                val chassisRect = Rect(
                    offset = Offset(frameLeft - (chassisWidthPx - frameWidth) / 2, frameTop + frameHeight),
                    size = Size(chassisWidthPx, chassisHeightPx)
                )
                
                // Draw main chassis bar
                drawRoundRect(
                    color = Color(0xFF2C2C2C),
                    topLeft = chassisRect.topLeft,
                    size = chassisRect.size,
                    cornerRadius = CornerRadius(4 * pixelScale)
                )
                
                // Draw opening notch
                val notchWidthPx = chassisWidthPx * 0.15f
                val notchHeightPx = chassisHeightPx * 0.4f
                drawRoundRect(
                    color = Color(0xFF1A1A1A),
                    topLeft = Offset(chassisRect.left + (chassisWidthPx - notchWidthPx) / 2, chassisRect.top),
                    size = Size(notchWidthPx, notchHeightPx),
                    cornerRadius = CornerRadius(0f, 0f)
                )
            }

            val framePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = frameRect,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                )
            }

            // Fix Background Tinting: Draw solid device body first
            drawPath(
                path = framePath,
                color = Color(0xFF1A1A1A), // Deep professional dark gray
                style = Fill
            )

            // 3. Draw Shadow
            drawPath(
                path = framePath,
                color = Color.Black.copy(alpha = 0.3f),
                style = Stroke(width = 12 * (frameWidth / 300f))
            )

            // 4. Clip and Draw Screenshot
            clipPath(framePath) {
                if (screenshot != null) {
                    val imgAspectRatio = screenshot.width.toFloat() / screenshot.height.toFloat()
                    var imgWidth: Float
                    var imgHeight: Float
                    
                    if (frameWidth / frameHeight > imgAspectRatio) {
                        imgHeight = frameHeight * imageScale
                        imgWidth = imgHeight * imgAspectRatio
                    } else {
                        imgWidth = frameWidth * imageScale
                        imgHeight = imgWidth / imgAspectRatio
                    }
                    
                    // Apply Screenshot Offsets
                    val imgLeft = frameLeft + (frameWidth - imgWidth) / 2 + screenshotOffsetX
                    val imgTop = frameTop + (frameHeight - imgHeight) / 2 + screenshotOffsetY
                    
                    drawImage(
                        image = screenshot,
                        dstOffset = IntOffset(imgLeft.toInt(), imgTop.toInt()),
                        dstSize = IntSize(imgWidth.toInt(), imgHeight.toInt()),
                        blendMode = BlendMode.SrcOver
                    )
                    
                    // Reflection Effect
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
                    // Draw a neutral display area if no screenshot
                    drawRect(
                        color = Color(0xFF2C2C2C),
                        topLeft = Offset(frameLeft, frameTop),
                        size = Size(frameWidth, frameHeight)
                    )
                }
            }

            // 5. Draw Frame Border
            drawPath(
                path = framePath,
                color = Color.Black,
                style = Stroke(width = 4 * (frameWidth / 300f))
            )

            // 6. Draw Camera Cutouts (Recalibrated for v2.1.1)
            when (deviceModel.cutoutType) {
                CutoutType.DYNAMIC_ISLAND -> {
                    // Slightly narrower: 12.5mm x 5mm
                    val islandWidthPx = 12.5f * pixelScale
                    val islandHeightPx = 5 * pixelScale
                    val islandRect = Rect(
                        offset = Offset(frameLeft + (frameWidth - islandWidthPx) / 2, frameTop + 4 * pixelScale), // Higher up (4mm)
                        size = Size(islandWidthPx, islandHeightPx)
                    )
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = islandRect.topLeft,
                        size = islandRect.size,
                        cornerRadius = CornerRadius(islandHeightPx / 2)
                    )
                }
                CutoutType.NOTCH -> {
                    // Recalibrated iPhone X Notch: 34mm x 5.5mm
                    val notchWidthPx = 34 * pixelScale
                    val notchHeightPx = 5.5f * pixelScale
                    val notchRect = Rect(
                        offset = Offset(frameLeft + (frameWidth - notchWidthPx) / 2, frameTop),
                        size = Size(notchWidthPx, notchHeightPx)
                    )
                    // Use a path for a smoother notch shape
                    val notchPath = Path().apply {
                        moveTo(notchRect.left, notchRect.top)
                        lineTo(notchRect.right, notchRect.top)
                        lineTo(notchRect.right, notchRect.bottom - 2 * pixelScale)
                        quadraticTo(notchRect.right, notchRect.bottom, notchRect.right - 2 * pixelScale, notchRect.bottom)
                        lineTo(notchRect.left + 2 * pixelScale, notchRect.bottom)
                        quadraticTo(notchRect.left, notchRect.bottom, notchRect.left, notchRect.bottom - 2 * pixelScale)
                        close()
                    }
                    drawPath(path = notchPath, color = Color.Black)
                }
                CutoutType.DOT -> {
                    val dotDiameterPx = 4 * pixelScale
                    drawCircle(
                        color = Color.Black,
                        radius = dotDiameterPx / 2,
                        center = Offset(frameLeft + frameWidth / 2, frameTop + 4 * pixelScale) // Higher up (4mm)
                    )
                }
                CutoutType.LAPTOP_NOTCH -> {
                    val notchWidthPx = frameWidth * 0.12f
                    val notchHeightPx = 4 * pixelScale
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = Offset(frameLeft + (frameWidth - notchWidthPx) / 2, frameTop),
                        size = Size(notchWidthPx, notchHeightPx),
                        cornerRadius = CornerRadius(0f, 0f)
                    )
                }
                CutoutType.NONE -> {}
            }

            // 7. Draw Watermark
            if (showWatermark && watermarkText.isNotEmpty()) {
                val paint = android.graphics.Paint().apply {
                    color = Color.White.copy(alpha = 0.7f).toArgb()
                    textSize = 16 * density.density * scale
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    watermarkText,
                    compLeft + compWidth - 16 * density.density,
                    compTop + compHeight - 16 * density.density,
                    paint
                )
            }
        }
    }
}
