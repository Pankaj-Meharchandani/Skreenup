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
import androidx.compose.ui.graphics.drawscope.withTransform
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
import com.example.skreenup.ui.models.TextFont
import com.example.skreenup.ui.models.TextAlignLabel
import android.graphics.Typeface

object MockupRenderer {
    fun DrawScope.drawMockup(
        screenshot: ImageBitmap?,
        deviceModel: DeviceModel,
        backgroundType: BackgroundType,
        backgroundColor: Color,
        gradientColors: List<Color>,
        backgroundImage: ImageBitmap?,
        backgroundImageOffsetX: Float = 0f,
        backgroundImageOffsetY: Float = 0f,
        scale: Float,
        imageScale: Float,
        frameOffsetX: Float,
        frameOffsetY: Float,
        screenshotOffsetX: Float,
        screenshotOffsetY: Float,
        aspectRatio: CompositionAspectRatio,
        showWatermark: Boolean,
        watermarkText: String,
        screenBackgroundColor: Color = Color(0xFF2C2C2C),
        isExport: Boolean = false,
        rotationDegrees: Float = 0f,
        screenshotRotation: Float = 0f,
        heading: String = "",
        subheading: String = "",
        headingFont: TextFont = TextFont.POPPINS,
        subheadingFont: TextFont = TextFont.POPPINS,
        headingSize: Float = 60f,
        subheadingSize: Float = 40f,
        textGap: Float = 20f,
        textColor: Color = Color.White,
        textOffsetX: Float = 0f,
        textOffsetY: Float = 0f,
        textAlignment: TextAlignLabel = TextAlignLabel.CENTER,
        headingBold: Boolean = true,
        subheadingBold: Boolean = false,
        showReflection: Boolean = true
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

        // Normalized Scaling Factor: Base everything on a 1000px width "design space"
        // This ensures the preview and export are visually identical regardless of resolution.
        val exportTextFactor = compWidth / 1000f

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
                BackgroundType.IMAGE -> {
                    if (backgroundImage != null) {
                        val imgAspectRatio = backgroundImage.width.toFloat() / backgroundImage.height.toFloat()
                        val compAspectRatio = compWidth / compHeight
                        
                        var drawWidth: Float
                        var drawHeight: Float

                        if (imgAspectRatio > compAspectRatio) {
                            drawHeight = compHeight
                            drawWidth = drawHeight * imgAspectRatio
                        } else {
                            drawWidth = compWidth
                            drawHeight = drawWidth / imgAspectRatio
                        }

                        val exportScaleFactor = if (isExport) compWidth / 1000f else 1f
                        val drawLeft = compLeft + (compWidth - drawWidth) / 2 + (backgroundImageOffsetX * exportScaleFactor)
                        val drawTop = compTop + (compHeight - drawHeight) / 2 + (backgroundImageOffsetY * exportScaleFactor)

                        drawImage(
                            image = backgroundImage,
                            dstOffset = IntOffset(drawLeft.toInt(), drawTop.toInt()),
                            dstSize = IntSize(drawWidth.toInt(), drawHeight.toInt()),
                            blendMode = BlendMode.SrcOver
                        )
                    } else {
                        drawRect(color = Color.LightGray, topLeft = Offset(compLeft, compTop), size = Size(compWidth, compHeight))
                    }
                }
                BackgroundType.TRANSPARENT -> {
                    // Do nothing, leave it transparent
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

        val exportScaleFactor = if (isExport) compWidth / 1000f else 1f
        val currentFrameOffsetX = frameOffsetX * exportScaleFactor
        val currentFrameOffsetY = frameOffsetY * exportScaleFactor
        val currentScreenshotOffsetX = screenshotOffsetX * exportScaleFactor
        val currentScreenshotOffsetY = screenshotOffsetY * exportScaleFactor

        val frameLeft = compLeft + (compWidth - frameWidth) / 2 + currentFrameOffsetX
        val frameTop = compTop + (compHeight - frameHeight) / 2 + currentFrameOffsetY

        val pixelScale = frameWidth / deviceModel.widthMm
        val cornerRadiusPx = (deviceModel.cornerRadiusDp * 0.3527f) * pixelScale

        // Pivot for rotation = center of frame + chassis area
        val isLaptop = deviceModel.type == FrameType.LAPTOP && deviceModel.hasChassis
        val chassisTotalHeight = if (isLaptop) 14f * pixelScale else 0f
        val pivotX = frameLeft + frameWidth / 2
        val pivotY = frameTop + (frameHeight + chassisTotalHeight) / 2

        // Wrap all device drawing in a rotation transform
        clipPath(Path().apply { addRect(compRect) }) {
            withTransform({
                rotate(degrees = rotationDegrees, pivot = Offset(pivotX, pivotY))
            }) {
                drawDeviceAndContent(
                    deviceModel = deviceModel,
                    screenshot = screenshot,
                    imageScale = imageScale,
                    frameLeft = frameLeft,
                    frameTop = frameTop,
                    frameWidth = frameWidth,
                    frameHeight = frameHeight,
                    pixelScale = pixelScale,
                    cornerRadiusPx = cornerRadiusPx,
                    currentScreenshotOffsetX = currentScreenshotOffsetX,
                    currentScreenshotOffsetY = currentScreenshotOffsetY,
                    screenshotRotation = screenshotRotation,
                    screenBackgroundColor = screenBackgroundColor,
                    showReflection = showReflection
                )
            }
        }

        // 6.5 Draw Heading & Subheading
        val hText = heading.trim()
        val sText = subheading.trim()
        
        if (hText.isNotEmpty() || sText.isNotEmpty()) {
            val finalTextColor = textColor.toArgb()

            fun createPaint(font: TextFont, size: Float, isBold: Boolean): android.graphics.Paint {
                val style = if (isBold) Typeface.BOLD else Typeface.NORMAL
                val tf = when (font) {
                    TextFont.POPPINS -> Typeface.create("sans-serif", style)
                    TextFont.INTER -> Typeface.create("sans-serif-medium", style)
                    TextFont.MONTSERRAT -> Typeface.create("sans-serif-light", style)
                    TextFont.BEBAS -> Typeface.create("sans-serif-black", style)
                    TextFont.PACIFICO -> Typeface.create("cursive", style)
                    TextFont.PLAYFAIR -> Typeface.create("serif-monospace", style)
                    TextFont.TIMES -> Typeface.create("serif", style)
                    TextFont.OSWALD -> Typeface.create("sans-serif-condensed", style)
                    TextFont.RALEWAY -> Typeface.create("sans-serif-thin", style)
                    TextFont.ANTON -> Typeface.create("sans-serif-black", style)
                    TextFont.QUICKSAND -> Typeface.create("sans-serif-light", style)
                    TextFont.LIBRE_BASKERVILLE -> Typeface.create("serif", style)
                }
                return android.graphics.Paint().apply {
                    color = finalTextColor
                    typeface = tf
                    textSize = size * exportTextFactor
                    isAntiAlias = true
                    this.textAlign = when (textAlignment) {
                        TextAlignLabel.LEFT -> android.graphics.Paint.Align.LEFT
                        TextAlignLabel.CENTER -> android.graphics.Paint.Align.CENTER
                        TextAlignLabel.RIGHT -> android.graphics.Paint.Align.RIGHT
                    }
                }
            }

            val headingPaint = createPaint(headingFont, headingSize, headingBold)
            val subheadingPaint = createPaint(subheadingFont, subheadingSize, subheadingBold)

            val hMetrics = headingPaint.fontMetrics
            val sMetrics = subheadingPaint.fontMetrics

            val headingLines = if (hText.isNotEmpty()) hText.split("\n") else emptyList()
            val subheadingLines = if (sText.isNotEmpty()) sText.split("\n") else emptyList()

            val headingLineHeight = headingPaint.fontSpacing
            val subheadingLineHeight = subheadingPaint.fontSpacing
            val gap = textGap * exportTextFactor

            // Precise block heights (Top of first line to bottom of last line)
            val headingBlockHeight = if (hText.isNotEmpty()) {
                (headingLines.size - 1) * headingLineHeight + (hMetrics.descent - hMetrics.ascent)
            } else 0f
            
            val subheadingBlockHeight = if (sText.isNotEmpty()) {
                (subheadingLines.size - 1) * subheadingLineHeight + (sMetrics.descent - sMetrics.ascent)
            } else 0f

            val totalTextHeight = headingBlockHeight + (if (hText.isNotEmpty() && sText.isNotEmpty()) gap else 0f) + subheadingBlockHeight

            val centerX = when (textAlignment) {
                TextAlignLabel.LEFT -> compLeft + 60f * exportTextFactor + (textOffsetX * exportTextFactor)
                TextAlignLabel.CENTER -> compLeft + compWidth / 2 + (textOffsetX * exportTextFactor)
                TextAlignLabel.RIGHT -> compLeft + compWidth - 60f * exportTextFactor + (textOffsetX * exportTextFactor)
            }

            // Top of the entire text block, centered vertically
            val blockTop = compTop + compHeight / 2 + (textOffsetY * exportTextFactor) - (totalTextHeight / 2)

            if (hText.isNotEmpty()) {
                // First baseline is at blockTop + the distance from font top to baseline (which is -hMetrics.ascent)
                val firstBaseline = blockTop - hMetrics.ascent
                headingLines.forEachIndexed { index, line ->
                    drawContext.canvas.nativeCanvas.drawText(
                        line,
                        centerX,
                        firstBaseline + (index * headingLineHeight),
                        headingPaint
                    )
                }
            }

            if (sText.isNotEmpty()) {
                val subBlockTop = if (hText.isNotEmpty()) {
                    blockTop + headingBlockHeight + gap
                } else {
                    blockTop
                }
                // First baseline of subheading
                val firstSubBaseline = subBlockTop - sMetrics.ascent
                subheadingLines.forEachIndexed { index, line ->
                    drawContext.canvas.nativeCanvas.drawText(
                        line,
                        centerX,
                        firstSubBaseline + (index * subheadingLineHeight),
                        subheadingPaint
                    )
                }
            }
        }

        // 7. Draw Watermark (outside rotation)
        if (showWatermark && watermarkText.isNotEmpty()) {
            val paint = android.graphics.Paint().apply {
                color = Color.White.copy(alpha = 0.7f).toArgb()
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

    /**
     * Draws the complete device: chassis, body, shadow, screenshot, frame border, cutouts, and highlights.
     */
    private fun DrawScope.drawDeviceAndContent(
        deviceModel: DeviceModel,
        screenshot: ImageBitmap?,
        imageScale: Float,
        frameLeft: Float,
        frameTop: Float,
        frameWidth: Float,
        frameHeight: Float,
        pixelScale: Float,
        cornerRadiusPx: Float,
        currentScreenshotOffsetX: Float,
        currentScreenshotOffsetY: Float,
        screenshotRotation: Float,
        screenBackgroundColor: Color,
        showReflection: Boolean
    ) {
        val frameRect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, frameHeight))

        val framePath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = frameRect,
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            )
        }

        val isLaptop = deviceModel.type == FrameType.LAPTOP && deviceModel.hasChassis

        // ── 2.1 Draw Laptop Chassis (MacBook-style) ──
        if (isLaptop) {
            drawLaptopChassis(frameLeft, frameTop, frameWidth, frameHeight, pixelScale)
        } else if (deviceModel.hasChassis && deviceModel.type == FrameType.DESKTOP) {
            // PC Monitor stand
            drawMonitorStand(frameLeft, frameTop, frameWidth, frameHeight, pixelScale)
        }

        // ── 3. Draw solid device body ──
        drawPath(
            path = framePath,
            color = Color(0xFF1A1A1A),
            style = Fill
        )

        // ── 4. Draw Shadow (Double layered for depth) ──
        val shadowAlpha = 0.15f
        val shadowOffset = 4 * pixelScale
        
        // Use a simple diffuse shadow by drawing a slightly larger, offset, semi-transparent path
        val shadowPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = frameRect.translate(Offset(shadowOffset, shadowOffset)),
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            )
        }
        drawPath(
            path = shadowPath,
            color = Color.Black.copy(alpha = shadowAlpha),
            style = Fill
        )
        
        // Optional: second softer layer
        val shadowPath2 = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = frameRect.translate(Offset(shadowOffset * 2, shadowOffset * 2)),
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            )
        }
        drawPath(
            path = shadowPath2,
            color = Color.Black.copy(alpha = shadowAlpha * 0.5f),
            style = Fill
        )

        // ── 5. Clip and Draw Screenshot ──
        clipPath(framePath) {
            drawRect(
                color = screenBackgroundColor,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(frameWidth, frameHeight)
            )

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

                withTransform({
                    rotate(degrees = screenshotRotation, pivot = Offset(imgLeft + imgWidth / 2, imgTop + imgHeight / 2))
                }) {
                    drawImage(
                        image = screenshot,
                        dstOffset = IntOffset(imgLeft.toInt(), imgTop.toInt()),
                        dstSize = IntSize(imgWidth.toInt(), imgHeight.toInt()),
                        blendMode = BlendMode.SrcOver
                    )
                }

                // Reflection Effect (Glossy Diagonal Slash)
                if (showReflection && deviceModel.hasReflection) {
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
            }
        }

        // 6. Draw Frame Border and Cutouts ──
        val isMobile = deviceModel.type == FrameType.IPHONE || deviceModel.type == FrameType.ANDROID_PHONE
        val strokeWidth = 5 * (frameWidth / 300f)

        // 6a. Metallic Frame (Outer Rim) - MOBILE ONLY for high-fidelity look
        if (isMobile) {
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
        } else {
            // Tablet/Laptop/PC: Simple dark border
            drawPath(
                path = framePath,
                color = Color(0xFF2C2C2C),
                style = Stroke(width = 2 * pixelScale)
            )
        }

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
                // Small webcam notch at top-center of screen
                val notchWidthPx = 8f * pixelScale
                val notchHeightPx = 3.5f * pixelScale
                drawRoundRect(
                    color = Color(0xFF0A0A0A),
                    topLeft = Offset(
                        frameLeft + (frameWidth - notchWidthPx) / 2,
                        frameTop + 1.5f * pixelScale
                    ),
                    size = Size(notchWidthPx, notchHeightPx),
                    cornerRadius = CornerRadius(notchHeightPx / 2)
                )
                // Camera dot
                val dotRadius = 1.2f * pixelScale
                drawCircle(
                    color = Color(0xFF333333),
                    radius = dotRadius,
                    center = Offset(
                        frameLeft + frameWidth / 2,
                        frameTop + 1.5f * pixelScale + notchHeightPx / 2
                    )
                )
            }
            CutoutType.NONE -> {}
        }

        // 6d. Specular Highlights (Corner glints) - MOBILE ONLY
        if (isMobile) {
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
        }
    }

    /**
     * Draws a MacBook-style chassis: lid edge, wedge base with metallic gradient,
     * front lip, and subtle trackpad indent.
     */
    private fun DrawScope.drawLaptopChassis(
        frameLeft: Float,
        frameTop: Float,
        frameWidth: Float,
        frameHeight: Float,
        pixelScale: Float
    ) {
        val chassisOverhang = frameWidth * 0.03f
        val chassisWidth = frameWidth + chassisOverhang * 2
        val chassisLeft = frameLeft - chassisOverhang
        val chassisTopY = frameTop + frameHeight

        // ── Lid hinge edge (thin dark line between screen and base) ──
        val hingeHeight = 2f * pixelScale
        drawRoundRect(
            color = Color(0xFF1C1C1E),
            topLeft = Offset(chassisLeft, chassisTopY),
            size = Size(chassisWidth, hingeHeight),
            cornerRadius = CornerRadius(0f)
        )

        // ── Main base body (wedge: thicker at rear, thinner at front) ──
        val baseHeight = 8f * pixelScale
        val baseTopY = chassisTopY + hingeHeight
        val baseFrontRadius = 2f * pixelScale
        val baseRearRadius = 1f * pixelScale

        val basePath = Path().apply {
            // Top-left (rear, slightly rounded)
            moveTo(chassisLeft + baseRearRadius, baseTopY)
            // Top edge
            lineTo(chassisLeft + chassisWidth - baseRearRadius, baseTopY)
            // Top-right corner
            quadraticTo(
                chassisLeft + chassisWidth, baseTopY,
                chassisLeft + chassisWidth, baseTopY + baseRearRadius
            )
            // Right edge to bottom-right
            lineTo(chassisLeft + chassisWidth - chassisOverhang * 0.3f, baseTopY + baseHeight - baseFrontRadius)
            quadraticTo(
                chassisLeft + chassisWidth - chassisOverhang * 0.3f, baseTopY + baseHeight,
                chassisLeft + chassisWidth - chassisOverhang * 0.3f - baseFrontRadius, baseTopY + baseHeight
            )
            // Bottom edge
            lineTo(chassisLeft + chassisOverhang * 0.3f + baseFrontRadius, baseTopY + baseHeight)
            // Bottom-left corner
            quadraticTo(
                chassisLeft + chassisOverhang * 0.3f, baseTopY + baseHeight,
                chassisLeft + chassisOverhang * 0.3f, baseTopY + baseHeight - baseFrontRadius
            )
            // Left edge to top-left
            lineTo(chassisLeft, baseTopY + baseRearRadius)
            quadraticTo(chassisLeft, baseTopY, chassisLeft + baseRearRadius, baseTopY)
            close()
        }

        // Metallic gradient fill for the base
        drawPath(
            path = basePath,
            brush = Brush.verticalGradient(
                0.0f to Color(0xFFD4D4D8),
                0.3f to Color(0xFFE8E8ED),
                0.5f to Color(0xFFC7C7CC),
                0.8f to Color(0xFFB0B0B5),
                1.0f to Color(0xFFA0A0A5),
                startY = baseTopY,
                endY = baseTopY + baseHeight
            )
        )

        // Top highlight on the base
        drawLine(
            brush = Brush.horizontalGradient(
                0.0f to Color.Transparent,
                0.2f to Color.White.copy(alpha = 0.5f),
                0.5f to Color.White.copy(alpha = 0.7f),
                0.8f to Color.White.copy(alpha = 0.5f),
                1.0f to Color.Transparent,
                startX = chassisLeft,
                endX = chassisLeft + chassisWidth
            ),
            start = Offset(chassisLeft + chassisOverhang, baseTopY + 0.5f * pixelScale),
            end = Offset(chassisLeft + chassisWidth - chassisOverhang, baseTopY + 0.5f * pixelScale),
            strokeWidth = 1f * pixelScale
        )

        // ── Front lip / opening notch ──
        val lipWidth = chassisWidth * 0.14f
        val lipHeight = 1.5f * pixelScale
        val lipLeft = chassisLeft + (chassisWidth - lipWidth) / 2
        val lipTop = baseTopY + baseHeight - lipHeight
        drawRoundRect(
            color = Color(0xFF8E8E93),
            topLeft = Offset(lipLeft, lipTop),
            size = Size(lipWidth, lipHeight),
            cornerRadius = CornerRadius(lipHeight / 2)
        )

        // Base edge outline
        drawPath(
            path = basePath,
            color = Color(0xFF8A8A8E),
            style = Stroke(width = 0.5f * pixelScale)
        )

        // ── Bottom shadow (under laptop) ──
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(chassisLeft + chassisOverhang, baseTopY + baseHeight),
            size = Size(chassisWidth - chassisOverhang * 2, 3f * pixelScale),
            cornerRadius = CornerRadius(2f * pixelScale)
        )
    }

    /**
     * Draws a PC monitor stand (simple thin neck + base).
     */
    private fun DrawScope.drawMonitorStand(
        frameLeft: Float,
        frameTop: Float,
        frameWidth: Float,
        frameHeight: Float,
        pixelScale: Float
    ) {
        val neckWidth = frameWidth * 0.08f
        val neckHeight = 12f * pixelScale
        val neckLeft = frameLeft + (frameWidth - neckWidth) / 2
        val neckTop = frameTop + frameHeight

        // Neck
        drawRect(
            brush = Brush.horizontalGradient(
                0.0f to Color(0xFFA0A0A5),
                0.5f to Color(0xFFD4D4D8),
                1.0f to Color(0xFFA0A0A5),
                startX = neckLeft,
                endX = neckLeft + neckWidth
            ),
            topLeft = Offset(neckLeft, neckTop),
            size = Size(neckWidth, neckHeight)
        )

        // Base
        val baseWidth = frameWidth * 0.35f
        val baseHeight = 3f * pixelScale
        val baseLeft = frameLeft + (frameWidth - baseWidth) / 2
        val baseTop = neckTop + neckHeight
        drawRoundRect(
            brush = Brush.verticalGradient(
                0.0f to Color(0xFFD4D4D8),
                1.0f to Color(0xFFA0A0A5),
                startY = baseTop,
                endY = baseTop + baseHeight
            ),
            topLeft = Offset(baseLeft, baseTop),
            size = Size(baseWidth, baseHeight),
            cornerRadius = CornerRadius(baseHeight / 2)
        )
    }
}
