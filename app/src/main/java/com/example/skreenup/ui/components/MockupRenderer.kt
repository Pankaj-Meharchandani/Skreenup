package com.example.skreenup.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.CutoutType
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.models.FrameType

object MockupRenderer {
    fun DrawScope.drawMockup(
        screenshot: ImageBitmap?,
        deviceModel: DeviceModel,
        backgroundType: BackgroundType,
        backgroundColor: Color,
        gradientColors: List<Color>,
        backgroundImage: ImageBitmap?,
        scale: Float,
        imageScale: Float,
        frameOffsetX: Float,
        frameOffsetY: Float,
        screenshotOffsetX: Float,
        screenshotOffsetY: Float,
        aspectRatio: CompositionAspectRatio,
        showWatermark: Boolean,
        watermarkText: String,
        isExport: Boolean = false
    ) {
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

        // 1. Draw Background (STRICTLY FIRST)
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

        // Adjust offsets for export if needed (assuming offsets are in DP in preview)
        // If they are in pixels in preview, they need to be scaled for export.
        val exportScaleFactor = if (isExport) compWidth / 1000f else 1f
        val currentFrameOffsetX = frameOffsetX * exportScaleFactor
        val currentFrameOffsetY = frameOffsetY * exportScaleFactor
        val currentScreenshotOffsetX = screenshotOffsetX * exportScaleFactor
        val currentScreenshotOffsetY = screenshotOffsetY * exportScaleFactor

        val frameLeft = compLeft + (compWidth - frameWidth) / 2 + currentFrameOffsetX
        val frameTop = compTop + (compHeight - frameHeight) / 2 + currentFrameOffsetY
        val frameRect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, frameHeight))

        val pixelScale = frameWidth / deviceModel.widthMm
        val cornerRadiusPx = (deviceModel.cornerRadiusDp * 0.3527f) * pixelScale

        val framePath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = frameRect,
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            )
        }

        // 2.1 Draw Laptop Chassis
        if (deviceModel.hasChassis && deviceModel.type == FrameType.DESKTOP) {
            val chassisHeightPx = 8 * pixelScale
            val chassisWidthPx = frameWidth * 1.15f
            val chassisRect = Rect(
                offset = Offset(frameLeft - (chassisWidthPx - frameWidth) / 2, frameTop + frameHeight),
                size = Size(chassisWidthPx, chassisHeightPx)
            )
            
            drawRoundRect(
                color = Color(0xFF2C2C2C),
                topLeft = chassisRect.topLeft,
                size = chassisRect.size,
                cornerRadius = CornerRadius(4 * pixelScale)
            )
            
            val notchWidthPx = chassisWidthPx * 0.15f
            val notchHeightPx = chassisHeightPx * 0.4f
            drawRoundRect(
                color = Color(0xFF1A1A1A),
                topLeft = Offset(chassisRect.left + (chassisWidthPx - notchWidthPx) / 2, chassisRect.top),
                size = Size(notchWidthPx, notchHeightPx),
                cornerRadius = CornerRadius(0f, 0f)
            )
        }

        // 3. Draw solid device body (STRICTLY SECOND) - Fixes background bleeding
        drawPath(
            path = framePath,
            color = Color(0xFF1A1A1A),
            style = Fill
        )

        // 4. Draw Shadow (Double layered for depth)
        val shadowWidth = 16 * (frameWidth / 300f)
        drawPath(
            path = framePath,
            color = Color.Black.copy(alpha = 0.15f),
            style = Stroke(width = shadowWidth)
        )
        drawPath(
            path = framePath,
            color = Color.Black.copy(alpha = 0.1f),
            style = Stroke(width = shadowWidth * 2f)
        )

        // 5. Clip and Draw Screenshot (STRICTLY THIRD)
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
                
                val imgLeft = frameLeft + (frameWidth - imgWidth) / 2 + currentScreenshotOffsetX
                val imgTop = frameTop + (frameHeight - imgHeight) / 2 + currentScreenshotOffsetY
                
                drawImage(
                    image = screenshot,
                    dstOffset = IntOffset(imgLeft.toInt(), imgTop.toInt()),
                    dstSize = IntSize(imgWidth.toInt(), imgHeight.toInt()),
                    blendMode = BlendMode.SrcOver
                )
                
                // Reflection Effect (Glossy Diagonal Slash)
                if (deviceModel.hasReflection) {
                    drawRect(
                        brush = Brush.linearGradient(
                            0.4f to Color.Transparent,
                            0.5f to Color.White.copy(alpha = 0.2f),
                            0.6f to Color.Transparent,
                            start = Offset(frameLeft, frameTop),
                            end = Offset(frameLeft + frameWidth, frameTop + frameHeight)
                        ),
                        topLeft = Offset(frameLeft, frameTop),
                        size = Size(frameWidth, frameHeight),
                        blendMode = BlendMode.Screen
                    )
                }
            } else {
                drawRect(
                    color = Color(0xFF2C2C2C),
                    topLeft = Offset(frameLeft, frameTop),
                    size = Size(frameWidth, frameHeight)
                )
            }
        }

        // 6. Draw Frame Border and Cutouts (STRICTLY LAST)
        val strokeWidth = 5 * (frameWidth / 300f)

        // 6a. Metallic Frame (Outer Rim)
        drawPath(
            path = framePath,
            brush = Brush.linearGradient(
                0.0f to Color(0xFFB0B0B0),
                0.3f to Color(0xFFF5F5F7),
                0.5f to Color(0xFF8E8E93),
                0.7f to Color(0xFFF5F5F7),
                1.0f to Color(0xFFB0B0B0),
                start = Offset(frameLeft, frameTop),
                end = Offset(frameLeft + frameWidth, frameTop + frameHeight)
            ),
            style = Stroke(width = strokeWidth)
        )

        // 6b. Inner Bezel (Deep Black)
        val bezelWidthPx = (deviceModel.bezelWidthDp * 0.3527f) * pixelScale
        drawPath(
            path = framePath,
            color = Color.Black,
            style = Stroke(width = bezelWidthPx)
        )

        // 6c. Cutouts
        when (deviceModel.cutoutType) {
            CutoutType.DYNAMIC_ISLAND -> {
                val islandWidthPx = 18 * pixelScale
                val islandHeightPx = 5 * pixelScale
                val islandRect = Rect(
                    offset = Offset(frameLeft + (frameWidth - islandWidthPx) / 2, frameTop + 4 * pixelScale),
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
                val notchWidthPx = 34 * pixelScale
                val notchHeightPx = 5.5f * pixelScale
                val notchRect = Rect(
                    offset = Offset(frameLeft + (frameWidth - notchWidthPx) / 2, frameTop),
                    size = Size(notchWidthPx, notchHeightPx)
                )
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
                    center = Offset(frameLeft + frameWidth / 2, frameTop + 4 * pixelScale)
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

        // 6d. Specular Highlights (Corner glints)
        drawPath(
            path = framePath,
            brush = Brush.linearGradient(
                0.0f to Color.White.copy(alpha = 0.5f),
                0.15f to Color.Transparent,
                0.85f to Color.Transparent,
                1.0f to Color.White.copy(alpha = 0.3f),
                start = Offset(frameLeft, frameTop),
                end = Offset(frameLeft + frameWidth, frameTop + frameHeight)
            ),
            style = Stroke(width = strokeWidth * 0.3f)
        )

        // 7. Draw Watermark
        if (showWatermark && watermarkText.isNotEmpty()) {
            val paint = android.graphics.Paint().apply {
                color = Color.White.copy(alpha = 0.7f).toArgb()
                // Use a relative text size based on density
                textSize = 16 * if (isExport) (compWidth / 400f) else density
                textAlign = android.graphics.Paint.Align.RIGHT
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                watermarkText,
                compLeft + compWidth - (if (isExport) 40f else 16.dp.toPx()),
                compTop + compHeight - (if (isExport) 40f else 16.dp.toPx()),
                paint
            )
        }
    }
}
