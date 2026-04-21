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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import android.graphics.BlurMaskFilter
import android.graphics.Paint as NativePaint

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
        backgroundImageScale: Float = 1.0f,
        backgroundImageBlur: Float = 0f,
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
        showReflection: Boolean = true,
        showTextShadow: Boolean = true,
        shadowIntensity: Float = 0.3f,
        shadowSoftness: Float = 1.0f,
        textZIndex: Int = 1
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
        val resolutionScale = compWidth / 1000f

        // 1. Draw Background (STRICTLY FIRST)
        clipPath(Path().apply { addRect(compRect) }) {
            when (backgroundType) {
                BackgroundType.SOLID -> {
                    drawRect(color = backgroundColor, topLeft = Offset(compLeft, compTop), size = Size(compWidth, compHeight))
                }
                BackgroundType.GRADIENT -> {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = gradientColors,
                            start = Offset(compLeft, compTop),
                            end = Offset(compLeft + compWidth, compTop + compHeight)
                        ),
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
                            drawHeight = compHeight * backgroundImageScale
                            drawWidth = drawHeight * imgAspectRatio
                        } else {
                            drawWidth = compWidth * backgroundImageScale
                            drawHeight = drawWidth / imgAspectRatio
                        }

                        val drawLeft = compLeft + (compWidth - drawWidth) / 2 + (backgroundImageOffsetX * resolutionScale)
                        val drawTop = compTop + (compHeight - drawHeight) / 2 + (backgroundImageOffsetY * resolutionScale)

                        val useBlur = backgroundImageBlur > 0

                        val imageToDraw = if (useBlur) {
                            applyBlurToBitmap(backgroundImage.asAndroidBitmap(), backgroundImageBlur)
                                ?.asImageBitmap() ?: backgroundImage
                        } else {
                            backgroundImage
                        }

                        drawImage(
                            image = imageToDraw,
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

        val currentFrameOffsetX = frameOffsetX * resolutionScale
        val currentFrameOffsetY = frameOffsetY * resolutionScale
        val currentScreenshotOffsetX = screenshotOffsetX * resolutionScale
        val currentScreenshotOffsetY = screenshotOffsetY * resolutionScale

        val frameLeft = compLeft + (compWidth - frameWidth) / 2 + currentFrameOffsetX
        val frameTop = compTop + (compHeight - frameHeight) / 2 + currentFrameOffsetY

        val pixelScale = frameWidth / deviceModel.widthMm
        val cornerRadiusPx = (deviceModel.cornerRadiusDp * 0.3527f) * pixelScale

        // Pivot for rotation = center of frame + chassis area
        val isLaptop = deviceModel.type == FrameType.LAPTOP && deviceModel.hasChassis
        val chassisTotalHeight = if (isLaptop) 14f * pixelScale else 0f
        val pivotX = frameLeft + frameWidth / 2
        val pivotY = frameTop + (frameHeight + chassisTotalHeight) / 2

        // ── 3. Helper for Drawing Text ──
        val drawTextContent = {
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
                        textSize = size * resolutionScale
                        isAntiAlias = true
                        this.textAlign = when (textAlignment) {
                            TextAlignLabel.LEFT -> NativePaint.Align.LEFT
                            TextAlignLabel.CENTER -> NativePaint.Align.CENTER
                            TextAlignLabel.RIGHT -> NativePaint.Align.RIGHT
                        }
                        if (showTextShadow) {
                            setShadowLayer(10f * resolutionScale, 2f * resolutionScale, 2f * resolutionScale, Color.Black.copy(alpha = 0.5f).toArgb())
                        }
                    }
                }

                var currentHeadingSize = headingSize
                var currentSubheadingSize = subheadingSize

                // ── Automatic Text Scaling & Position Guard (Prevent cutting off) ──
                val horizontalPadding = 60f * resolutionScale
                
                fun getWidestLine(paint: android.graphics.Paint, lines: List<String>): Float {
                    return lines.maxOfOrNull { paint.measureText(it) } ?: 0f
                }

                val headingLinesInitial = hText.split("\n")
                val subheadingLinesInitial = sText.split("\n")

                var hPaint = createPaint(headingFont, currentHeadingSize, headingBold)
                var sPaint = createPaint(subheadingFont, currentSubheadingSize, subheadingBold)

                // Initial position calculation
                var centerX = when (textAlignment) {
                    TextAlignLabel.LEFT -> compLeft + horizontalPadding + (textOffsetX * resolutionScale)
                    TextAlignLabel.CENTER -> compLeft + compWidth / 2 + (textOffsetX * resolutionScale)
                    TextAlignLabel.RIGHT -> compLeft + compWidth - horizontalPadding + (textOffsetX * resolutionScale)
                }

                var widestH = getWidestLine(hPaint, headingLinesInitial)
                var widestS = getWidestLine(sPaint, subheadingLinesInitial)
                var maxW = maxOf(widestH, widestS)

                // 1. Position Guard: Ensure the text start/end doesn't go off canvas
                when (textAlignment) {
                    TextAlignLabel.LEFT -> {
                        if (centerX < compLeft + 20f * resolutionScale) {
                            centerX = compLeft + 20f * resolutionScale
                        }
                    }
                    TextAlignLabel.RIGHT -> {
                        if (centerX > compLeft + compWidth - 20f * resolutionScale) {
                            centerX = compLeft + compWidth - 20f * resolutionScale
                        }
                    }
                    TextAlignLabel.CENTER -> {
                        // Center is already handled by initial calculation
                    }
                }

                // 2. Scale Guard: Shrink text if it still exceeds available width from its position
                val availableWidth = when (textAlignment) {
                    TextAlignLabel.LEFT -> (compLeft + compWidth - 20f * resolutionScale) - centerX
                    TextAlignLabel.CENTER -> compWidth - 40f * resolutionScale
                    TextAlignLabel.RIGHT -> centerX - (compLeft + 20f * resolutionScale)
                }

                while (maxW > availableWidth && currentHeadingSize > 12f) {
                    currentHeadingSize *= 0.95f
                    currentSubheadingSize *= 0.95f
                    hPaint = createPaint(headingFont, currentHeadingSize, headingBold)
                    sPaint = createPaint(subheadingFont, currentSubheadingSize, subheadingBold)
                    widestH = getWidestLine(hPaint, headingLinesInitial)
                    widestS = getWidestLine(sPaint, subheadingLinesInitial)
                    maxW = maxOf(widestH, widestS)
                }

                val hMetrics = hPaint.fontMetrics
                val sMetrics = sPaint.fontMetrics

                val headingLineHeight = hPaint.fontSpacing
                val subheadingLineHeight = sPaint.fontSpacing
                val gap = textGap * resolutionScale

                val headingBlockHeight = if (hText.isNotEmpty()) {
                    (headingLinesInitial.size - 1) * headingLineHeight + (hMetrics.descent - hMetrics.ascent)
                } else 0f

                val subheadingBlockHeight = if (sText.isNotEmpty()) {
                    (subheadingLinesInitial.size - 1) * subheadingLineHeight + (sMetrics.descent - sMetrics.ascent)
                } else 0f

                val totalTextHeight = headingBlockHeight + (if (hText.isNotEmpty() && sText.isNotEmpty()) gap else 0f) + subheadingBlockHeight

                val blockTop = compTop + compHeight / 2 + (textOffsetY * resolutionScale) - (totalTextHeight / 2)

                if (hText.isNotEmpty()) {
                    val firstBaseline = blockTop - hMetrics.ascent
                    headingLinesInitial.forEachIndexed { index, line ->
                        drawContext.canvas.nativeCanvas.drawText(
                            line,
                            centerX,
                            firstBaseline + (index * headingLineHeight),
                            hPaint
                        )
                    }
                }

                if (sText.isNotEmpty()) {
                    val subBlockTop = if (hText.isNotEmpty()) {
                        blockTop + headingBlockHeight + gap
                    } else {
                        blockTop
                    }
                    val firstSubBaseline = subBlockTop - sMetrics.ascent
                    subheadingLinesInitial.forEachIndexed { index, line ->
                        drawContext.canvas.nativeCanvas.drawText(
                            line,
                            centerX,
                            firstSubBaseline + (index * subheadingLineHeight),
                            sPaint
                        )
                    }
                }
            }
        }

        // ── 4. Draw Content based on Z-Index ──
        if (textZIndex < 0) {
            drawTextContent()
        }

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
                    showReflection = showReflection,
                    shadowIntensity = shadowIntensity,
                    shadowSoftness = shadowSoftness
                )
            }
        }

        if (textZIndex >= 0) {
            drawTextContent()
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
        showReflection: Boolean,
        shadowIntensity: Float,
        shadowSoftness: Float
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

        // ── 3. Realistic Multi-layered Soft Shadow ──
        // We use the native canvas for BlurMaskFilter which provides much more realistic shadows
        if (shadowIntensity > 0) {
            val shadowPaint = NativePaint().apply {
                color = android.graphics.Color.BLACK
                isAntiAlias = true
            }

            // Layer 1: Ambient Occlusion (Close, dark)
            shadowPaint.alpha = (255 * shadowIntensity * 0.8f).toInt()
            shadowPaint.maskFilter = BlurMaskFilter((2 * pixelScale * shadowSoftness).coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL)
            drawContext.canvas.nativeCanvas.drawRoundRect(
                frameLeft, frameTop + 1 * pixelScale,
                frameLeft + frameWidth, frameTop + frameHeight + 1 * pixelScale,
                cornerRadiusPx, cornerRadiusPx, shadowPaint
            )

            // Layer 2: Main Drop Shadow
            shadowPaint.alpha = (255 * shadowIntensity * 0.4f).toInt()
            shadowPaint.maskFilter = BlurMaskFilter((10 * pixelScale * shadowSoftness).coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL)
            drawContext.canvas.nativeCanvas.drawRoundRect(
                frameLeft + 4 * pixelScale, frameTop + 8 * pixelScale,
                frameLeft + frameWidth + 4 * pixelScale, frameTop + frameHeight + 8 * pixelScale,
                cornerRadiusPx, cornerRadiusPx, shadowPaint
            )

            // Layer 3: Distant Soft Shadow
            shadowPaint.alpha = (255 * shadowIntensity * 0.2f).toInt()
            shadowPaint.maskFilter = BlurMaskFilter((25 * pixelScale * shadowSoftness).coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL)
            drawContext.canvas.nativeCanvas.drawRoundRect(
                frameLeft + 10 * pixelScale, frameTop + 20 * pixelScale,
                frameLeft + frameWidth + 10 * pixelScale, frameTop + frameHeight + 20 * pixelScale,
                cornerRadiusPx, cornerRadiusPx, shadowPaint
            )
        }

        // ── 4. Draw device body with metallic depth ──
        drawPath(
            path = framePath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E), Color(0xFF2C2C2E)),
                start = Offset(frameLeft, frameTop),
                end = Offset(frameLeft + frameWidth, frameTop + frameHeight)
            ),
            style = Fill
        )

        // ── 4.1 Subtle Rim Highlight (Makes it look 3D) ──
        drawPath(
            path = framePath,
            color = Color.White.copy(alpha = 0.1f),
            style = Stroke(width = 1f * pixelScale)
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

                // Screen Inner depth (Subtle shadow on the edges of the screenshot)
                drawPath(
                    path = framePath,
                    color = Color.Black.copy(alpha = 0.15f),
                    style = Stroke(width = 2 * pixelScale)
                )

                // Reflection Effect (Glossy Diagonal Slash)
                if (showReflection) {
                    drawRect(
                        brush = Brush.linearGradient(
                            0.3f to Color.Transparent,
                            0.45f to Color.White.copy(alpha = 0.15f),
                            0.5f to Color.White.copy(alpha = 0.25f),
                            0.55f to Color.White.copy(alpha = 0.15f),
                            0.7f to Color.Transparent,
                            start = Offset(frameLeft, frameTop),
                            end = Offset(frameLeft + frameWidth, frameTop + frameHeight)
                        ),
                        topLeft = Offset(frameLeft, frameTop),
                        size = Size(frameWidth, frameHeight),
                        blendMode = BlendMode.Screen
                    )
                    
                    // Subtle top-down gradient to simulate ambient light
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
                            startY = frameTop,
                            endY = frameTop + frameHeight * 0.5f
                        ),
                        topLeft = Offset(frameLeft, frameTop),
                        size = Size(frameWidth, frameHeight),
                        blendMode = BlendMode.Plus
                    )
                }
            }
        }

        // 6. Draw Frame Border and Cutouts ──
        val isHandheld = deviceModel.type == FrameType.IPHONE || 
                         deviceModel.type == FrameType.ANDROID_PHONE || 
                         deviceModel.type == FrameType.TABLET
        val strokeWidth = 5 * (frameWidth / 300f)

        // 6a. Frame (Outer Rim)
        val frameOutlineColor = if (deviceModel.cutoutType == CutoutType.SAFARI) {
            Color.White
        } else {
            Color(0xFF2C2C2C)
        }

        drawPath(
            path = framePath,
            color = frameOutlineColor,
            style = Stroke(width = if (isHandheld) strokeWidth else 2 * pixelScale)
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
            CutoutType.SAFARI -> {
                drawSafariHeader(frameLeft, frameTop, frameWidth, pixelScale, cornerRadiusPx)
            }
            CutoutType.CHROME -> {
                drawChromeHeader(frameLeft, frameTop, frameWidth, pixelScale, cornerRadiusPx)
            }
            CutoutType.OPERA -> {
                drawOperaHeader(frameLeft, frameTop, frameWidth, frameHeight, pixelScale, cornerRadiusPx)
            }
            CutoutType.NONE -> {}
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
        val laptopShadowWidth = chassisWidth * 0.95f
        val laptopShadowHeight = 4f * pixelScale
        drawOval(
            brush = Brush.radialGradient(
                0.0f to Color.Black.copy(alpha = 0.12f),
                1.0f to Color.Transparent,
                center = Offset(chassisLeft + chassisWidth / 2, baseTopY + baseHeight),
                radius = laptopShadowWidth / 2
            ),
            topLeft = Offset(chassisLeft + (chassisWidth - laptopShadowWidth) / 2, baseTopY + baseHeight - laptopShadowHeight / 2),
            size = Size(laptopShadowWidth, laptopShadowHeight)
        )
    }

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

        val baseWidth = frameWidth * 0.35f
        val baseHeight = 3f * pixelScale
        val baseLeft = frameLeft + (frameWidth - baseWidth) / 2
        val baseTop = neckTop + neckHeight

        // ── 1. Floor Shadow (Below the stand base) ──
        // This creates the contact point on the "table" surface.
        val shadowWidth = baseWidth * 1.6f
        val shadowHeight = 8f * pixelScale
        drawOval(
            brush = Brush.radialGradient(
                0.0f to Color.Black.copy(alpha = 0.3f),
                0.6f to Color.Black.copy(alpha = 0.1f),
                1.0f to Color.Transparent,
                center = Offset(baseLeft + baseWidth / 2, baseTop + baseHeight),
                radius = shadowWidth / 2
            ),
            topLeft = Offset(baseLeft + baseWidth / 2 - shadowWidth / 2, baseTop + baseHeight - shadowHeight / 2),
            size = Size(shadowWidth, shadowHeight)
        )

        // ── 2. Ambient Occlusion Shadow (Under the monitor screen) ──
        // Softens the area where the monitor's bulk hangs over the surface.
        drawOval(
            brush = Brush.radialGradient(
                0.0f to Color.Black.copy(alpha = 0.15f),
                1.0f to Color.Transparent,
                center = Offset(frameLeft + frameWidth / 2, neckTop),
                radius = frameWidth * 0.6f
            ),
            topLeft = Offset(frameLeft - frameWidth * 0.1f, neckTop - 4f * pixelScale),
            size = Size(frameWidth * 1.2f, 16f * pixelScale)
        )

        // 3. Neck
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

        // 4. Base
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

    private fun DrawScope.drawSafariHeader(
        frameLeft: Float,
        frameTop: Float,
        frameWidth: Float,
        pixelScale: Float,
        cornerRadiusPx: Float
    ) {
        val headerHeight = 16f * pixelScale
        // Draw header background
        val headerPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, headerHeight + cornerRadiusPx)),
                    topLeft = CornerRadius(cornerRadiusPx),
                    topRight = CornerRadius(cornerRadiusPx)
                )
            )
        }
        
        // Use a slight gradient for the Safari header
        drawPath(
            path = headerPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFF6F6F6), Color(0xFFEBEBEB)),
                startY = frameTop,
                endY = frameTop + headerHeight
            )
        )

        // Bottom border of the header
        drawLine(
            color = Color(0xFFD1D1D1),
            start = Offset(frameLeft, frameTop + headerHeight),
            end = Offset(frameLeft + frameWidth, frameTop + headerHeight),
            strokeWidth = 0.5f * pixelScale
        )

        // Window controls (Red, Yellow, Green)
        val btnRadius = 2.2f * pixelScale
        val btnY = frameTop + headerHeight / 2
        val startX = frameLeft + 8f * pixelScale
        val spacing = 6f * pixelScale

        drawCircle(Color(0xFFFF5F56), radius = btnRadius, center = Offset(startX, btnY))
        drawCircle(Color(0xFFFFBD2E), radius = btnRadius, center = Offset(startX + spacing, btnY))
        drawCircle(Color(0xFF27C93F), radius = btnRadius, center = Offset(startX + spacing * 2, btnY))

        // URL Bar
        val urlBarWidth = frameWidth * 0.55f
        val urlBarHeight = 10f * pixelScale
        val urlBarLeft = frameLeft + (frameWidth - urlBarWidth) / 2
        val urlBarTop = frameTop + (headerHeight - urlBarHeight) / 2

        drawRoundRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = Offset(urlBarLeft, urlBarTop),
            size = Size(urlBarWidth, urlBarHeight),
            cornerRadius = CornerRadius(3f * pixelScale)
        )
        
        // Subtle border for URL Bar
        drawRoundRect(
            color = Color(0xFFD1D1D1),
            topLeft = Offset(urlBarLeft, urlBarTop),
            size = Size(urlBarWidth, urlBarHeight),
            cornerRadius = CornerRadius(3f * pixelScale),
            style = Stroke(width = 0.3f * pixelScale)
        )

        // Search icon (simplified magnifying glass)
        val iconSize = 2.5f * pixelScale
        val iconX = urlBarLeft + 4f * pixelScale
        val iconY = urlBarTop + urlBarHeight / 2
        drawCircle(
            color = Color(0xFF999999),
            radius = iconSize / 2,
            center = Offset(iconX, iconY),
            style = Stroke(width = 0.5f * pixelScale)
        )
        drawLine(
            color = Color(0xFF999999),
            start = Offset(iconX + iconSize / 2.5f, iconY + iconSize / 2.5f),
            end = Offset(iconX + iconSize / 1.5f, iconY + iconSize / 1.5f),
            strokeWidth = 0.5f * pixelScale
        )
    }

    private fun DrawScope.drawChromeHeader(
        frameLeft: Float,
        frameTop: Float,
        frameWidth: Float,
        pixelScale: Float,
        cornerRadiusPx: Float
    ) {
        val tabAreaHeight = 14f * pixelScale
        val navAreaHeight = 16f * pixelScale
        val totalHeaderHeight = tabAreaHeight + navAreaHeight
        
        // 1. Background for the whole header
        val headerPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, totalHeaderHeight + cornerRadiusPx)),
                    topLeft = CornerRadius(cornerRadiusPx),
                    topRight = CornerRadius(cornerRadiusPx)
                )
            )
        }
        
        drawPath(
            path = headerPath,
            color = Color(0xFF202124) // Dark Chrome background
        )

        // 2. Active Tab
        val tabWidth = 80f * pixelScale
        val tabHeight = 11f * pixelScale
        val tabLeft = frameLeft + 4f * pixelScale
        val tabTop = frameTop + 3f * pixelScale
        
        // Complex tab shape with rounded bottom "wings"
        val tabPath = Path().apply {
            moveTo(tabLeft - 4f * pixelScale, frameTop + tabAreaHeight)
            quadraticTo(tabLeft, frameTop + tabAreaHeight, tabLeft, frameTop + tabAreaHeight - 4f * pixelScale)
            lineTo(tabLeft, tabTop + 3f * pixelScale)
            quadraticTo(tabLeft, tabTop, tabLeft + 3f * pixelScale, tabTop)
            lineTo(tabLeft + tabWidth - 3f * pixelScale, tabTop)
            quadraticTo(tabLeft + tabWidth, tabTop, tabLeft + tabWidth, tabTop + 3f * pixelScale)
            lineTo(tabLeft + tabWidth, frameTop + tabAreaHeight - 4f * pixelScale)
            quadraticTo(tabLeft + tabWidth, frameTop + tabAreaHeight, tabLeft + tabWidth + 4f * pixelScale, frameTop + tabAreaHeight)
            close()
        }
        drawPath(path = tabPath, color = Color(0xFF35363A))

        // Tab Text "New tab" (Simplified as lines/dots)
        val textY = tabTop + tabHeight / 2
        // Favicon placeholder
        drawCircle(Color.White.copy(0.4f), radius = 1.2f * pixelScale, center = Offset(tabLeft + 6f * pixelScale, textY))
        // Text line
        drawLine(
            color = Color.White.copy(0.8f),
            start = Offset(tabLeft + 10f * pixelScale, textY),
            end = Offset(tabLeft + 35f * pixelScale, textY),
            strokeWidth = 1f * pixelScale
        )
        // Tab close 'x'
        val xSize = 1.5f * pixelScale
        val xCenterX = tabLeft + tabWidth - 6f * pixelScale
        drawLine(Color.White.copy(0.6f), Offset(xCenterX - xSize, textY - xSize), Offset(xCenterX + xSize, textY + xSize), 0.5f * pixelScale)
        drawLine(Color.White.copy(0.6f), Offset(xCenterX + xSize, textY - xSize), Offset(xCenterX - xSize, textY + xSize), 0.5f * pixelScale)

        // New Tab '+' Button
        val plusX = tabLeft + tabWidth + 8f * pixelScale
        val plusY = frameTop + tabAreaHeight / 2 + 1f * pixelScale
        val plusSize = 2f * pixelScale
        drawLine(Color.White.copy(0.7f), Offset(plusX - plusSize, plusY), Offset(plusX + plusSize, plusY), 0.8f * pixelScale)
        drawLine(Color.White.copy(0.7f), Offset(plusX, plusY - plusSize), Offset(plusX, plusY + plusSize), 0.8f * pixelScale)

        // 3. Navigation/URL Bar Area
        drawRect(
            color = Color(0xFF35363A),
            topLeft = Offset(frameLeft, frameTop + tabAreaHeight),
            size = Size(frameWidth, navAreaHeight)
        )

        val navBtnY = frameTop + tabAreaHeight + navAreaHeight / 2
        val navBtnStartX = frameLeft + 10f * pixelScale
        val navBtnSpacing = 12f * pixelScale
        val navIconSize = 2.5f * pixelScale

        // Back Arrow
        drawLine(Color.White.copy(0.7f), Offset(navBtnStartX - navIconSize, navBtnY), Offset(navBtnStartX + navIconSize, navBtnY), 1f * pixelScale)
        drawLine(Color.White.copy(0.7f), Offset(navBtnStartX - navIconSize, navBtnY), Offset(navBtnStartX, navBtnY - navIconSize), 1f * pixelScale)
        drawLine(Color.White.copy(0.7f), Offset(navBtnStartX - navIconSize, navBtnY), Offset(navBtnStartX, navBtnY + navIconSize), 1f * pixelScale)

        // Forward Arrow
        val fwdX = navBtnStartX + navBtnSpacing
        drawLine(Color.White.copy(0.4f), Offset(fwdX - navIconSize, navBtnY), Offset(fwdX + navIconSize, navBtnY), 1f * pixelScale)
        drawLine(Color.White.copy(0.4f), Offset(fwdX + navIconSize, navBtnY), Offset(fwdX, navBtnY - navIconSize), 1f * pixelScale)
        drawLine(Color.White.copy(0.4f), Offset(fwdX + navIconSize, navBtnY), Offset(fwdX, navBtnY + navIconSize), 1f * pixelScale)

        // Refresh Icon
        val refreshX = fwdX + navBtnSpacing
        drawArc(
            color = Color.White.copy(0.7f),
            startAngle = 0f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(refreshX - navIconSize, navBtnY - navIconSize),
            size = Size(navIconSize * 2, navIconSize * 2),
            style = Stroke(1f * pixelScale)
        )
        // Refresh Arrow Head
        val path = Path().apply {
            moveTo(refreshX + navIconSize, navBtnY)
            lineTo(refreshX + navIconSize + 1.5f * pixelScale, navBtnY - 1.5f * pixelScale)
            lineTo(refreshX + navIconSize - 1.5f * pixelScale, navBtnY - 1.5f * pixelScale)
            close()
        }
        drawPath(path, color = Color.White.copy(0.7f))

        // URL Bar
        val urlBarLeft = refreshX + navBtnSpacing + 4f * pixelScale
        val urlBarWidth = frameWidth - (urlBarLeft - frameLeft) - 12f * pixelScale
        val urlBarHeight = 10f * pixelScale
        val urlBarTop = frameTop + tabAreaHeight + (navAreaHeight - urlBarHeight) / 2

        drawRoundRect(
            color = Color(0xFF202124),
            topLeft = Offset(urlBarLeft, urlBarTop),
            size = Size(urlBarWidth, urlBarHeight),
            cornerRadius = CornerRadius(urlBarHeight / 2)
        )

        // Search icon in URL bar
        val searchIconX = urlBarLeft + 4f * pixelScale
        val searchIconY = urlBarTop + urlBarHeight / 2
        val sSize = 1.2f * pixelScale
        drawCircle(Color.White.copy(0.4f), radius = sSize, center = Offset(searchIconX, searchIconY), style = Stroke(0.5f * pixelScale))
        drawLine(Color.White.copy(0.4f), Offset(searchIconX + sSize * 0.7f, searchIconY + sSize * 0.7f), Offset(searchIconX + sSize * 1.5f, searchIconY + sSize * 1.5f), 0.5f * pixelScale)

        // 4. Window Controls (Right side)
        val controlWidth = 14f * pixelScale
        val controlHeight = tabAreaHeight
        val rightEdge = frameLeft + frameWidth

        // Close button (Transparent background now)
        val cx = rightEdge - controlWidth / 2
        val cy = frameTop + controlHeight / 2
        val xs = 2f * pixelScale
        drawLine(Color.White.copy(0.8f), Offset(cx - xs, cy - xs), Offset(cx + xs, cy + xs), 0.8f * pixelScale)
        drawLine(Color.White.copy(0.8f), Offset(cx + xs, cy - xs), Offset(cx - xs, cy + xs), 0.8f * pixelScale)

        // Maximize
        val mx = rightEdge - controlWidth * 2 + controlWidth / 2
        drawRect(
            color = Color.White.copy(0.8f),
            topLeft = Offset(mx - 2f * pixelScale, cy - 2f * pixelScale),
            size = Size(4f * pixelScale, 4f * pixelScale),
            style = Stroke(0.5f * pixelScale)
        )

        // Minimize
        val minx = rightEdge - controlWidth * 3 + controlWidth / 2
        drawLine(
            color = Color.White.copy(0.8f),
            start = Offset(minx - 2f * pixelScale, cy),
            end = Offset(minx + 2f * pixelScale, cy),
            strokeWidth = 0.5f * pixelScale
        )
        drawLine(
            color = Color.White.copy(0.8f),
            start = Offset(minx - 2f * pixelScale, cy),
            end = Offset(minx + 2f * pixelScale, cy),
            strokeWidth = 0.5f * pixelScale
        )
    }

    private fun DrawScope.drawOperaHeader(
        frameLeft: Float,
        frameTop: Float,
        frameWidth: Float,
        frameHeight: Float,
        pixelScale: Float,
        cornerRadiusPx: Float
    ) {
        val sidebarWidth = 14f * pixelScale
        val topBarHeight = 12f * pixelScale
        val addressBarHeight = 14f * pixelScale
        val totalHeaderHeight = topBarHeight + addressBarHeight
        
        val accentColor = Color(0xFFFA1E4E) // Opera GX Red
        val darkBg = Color(0xFF0B0B0E)
        val surfaceColor = Color(0xFF1C1C21)

        // 1. Sidebar Background
        val sidebarPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset(frameLeft, frameTop), Size(sidebarWidth, frameHeight)),
                    topLeft = CornerRadius(cornerRadiusPx),
                    bottomLeft = CornerRadius(cornerRadiusPx)
                )
            )
        }
        drawPath(path = sidebarPath, color = darkBg)
        
        // Sidebar Border (Right)
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(frameLeft + sidebarWidth, frameTop),
            end = Offset(frameLeft + sidebarWidth, frameTop + frameHeight),
            strokeWidth = 0.5f * pixelScale
        )

        // 2. Top Header Background
        val headerPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset(frameLeft, frameTop), Size(frameWidth, totalHeaderHeight)),
                    topLeft = CornerRadius(cornerRadiusPx),
                    topRight = CornerRadius(cornerRadiusPx)
                )
            )
        }
        drawPath(path = headerPath, color = darkBg)

        // 3. Opera "O" Logo (Top Left)
        val logoSize = 6f * pixelScale
        val logoX = frameLeft + sidebarWidth / 2
        val logoY = frameTop + topBarHeight / 2
        drawCircle(
            color = accentColor,
            radius = logoSize / 2,
            center = Offset(logoX, logoY),
            style = Stroke(width = 1.2f * pixelScale)
        )

        // 4. Active Tab
        val tabWidth = 50f * pixelScale
        val tabHeight = 8f * pixelScale
        val tabLeft = frameLeft + sidebarWidth + 4f * pixelScale
        val tabTop = frameTop + (topBarHeight - tabHeight)
        
        drawRoundRect(
            color = accentColor.copy(alpha = 0.8f),
            topLeft = Offset(tabLeft, tabTop),
            size = Size(tabWidth, tabHeight),
            cornerRadius = CornerRadius(2f * pixelScale, 2f * pixelScale)
        )
        // Tab Text indicator
        drawLine(
            color = Color.White,
            start = Offset(tabLeft + 12f * pixelScale, tabTop + tabHeight / 2),
            end = Offset(tabLeft + 35f * pixelScale, tabTop + tabHeight / 2),
            strokeWidth = 0.8f * pixelScale
        )

        // 5. Address Bar Area
        drawRect(
            color = darkBg,
            topLeft = Offset(frameLeft + sidebarWidth, frameTop + topBarHeight),
            size = Size(frameWidth - sidebarWidth, addressBarHeight)
        )

        // Navigation Icons
        val navY = frameTop + topBarHeight + addressBarHeight / 2
        val navStartX = frameLeft + sidebarWidth + 8f * pixelScale
        val navSpacing = 10f * pixelScale
        
        // Back/Forward/Refresh (Simplified)
        repeat(3) { i ->
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = 1f * pixelScale,
                center = Offset(navStartX + i * navSpacing, navY)
            )
        }

        // URL Field
        val urlLeft = navStartX + 3 * navSpacing
        val urlWidth = frameWidth - (urlLeft - frameLeft) - 10f * pixelScale
        val urlHeight = 8f * pixelScale
        val urlTop = frameTop + topBarHeight + (addressBarHeight - urlHeight) / 2

        drawRoundRect(
            color = surfaceColor,
            topLeft = Offset(urlLeft, urlTop),
            size = Size(urlWidth, urlHeight),
            cornerRadius = CornerRadius(urlHeight / 2)
        )
        // URL Search Icon
        drawCircle(
            color = Color.White.copy(0.3f),
            radius = 1f * pixelScale,
            center = Offset(urlLeft + 4f * pixelScale, urlTop + urlHeight / 2),
            style = Stroke(0.5f * pixelScale)
        )

        // 6. Sidebar Icons (Opera GX style)
        val iconStartY = frameTop + totalHeaderHeight + 10f * pixelScale
        val iconSpacing = 18f * pixelScale
        val iconSize = 4f * pixelScale
        val iconX = frameLeft + sidebarWidth / 2

        val sidebarIcons = listOf(
            accentColor, // GX Control
            Color.White.copy(0.7f), // Home
            accentColor.copy(0.7f), // Speed Dial
            Color.White.copy(0.5f), // Cleaner
            Color.White.copy(0.5f), // CPU/RAM
            Color.White.copy(0.5f), // Network
            Color.White.copy(0.5f), // Downloads
            Color.White.copy(0.5f), // Extensions
            accentColor.copy(0.6f)  // Player/Twitch
        )

        sidebarIcons.forEachIndexed { index, color ->
            val y = iconStartY + index * iconSpacing
            if (index == 0) {
                // GX Control Icon (Square-ish with dots)
                drawRect(
                    color = color,
                    topLeft = Offset(iconX - iconSize/2, y - iconSize/2),
                    size = Size(iconSize, iconSize),
                    style = Stroke(0.8f * pixelScale)
                )
            } else {
                drawCircle(
                    color = color,
                    radius = iconSize / 2.5f,
                    center = Offset(iconX, y)
                )
            }
        }
        
        // Active Sidebar Indicator (Glow on left)
        drawRect(
            color = accentColor,
            topLeft = Offset(frameLeft, iconStartY - iconSize / 2),
            size = Size(1f * pixelScale, iconSize)
        )
    }

    /**
     * True blur via repeated downscale → upscale (box blur approximation).
     * Works on ALL API levels, on both software and hardware canvases.
     * Increasing passes improves quality; radius controls strength.
     */
    private fun applyBlurToBitmap(source: android.graphics.Bitmap, blurRadius: Float): android.graphics.Bitmap? {
        if (blurRadius <= 0f) return source
        return try {
            // Use multiple small downscale steps instead of one big jump.
            // Each step is only 0.7x the previous, which keeps bilinear filtering smooth.
            // More passes = more blur, no pixelation.
            val passes = (1 + (blurRadius / 100f) * 5f).toInt().coerceIn(1, 6)
            val stepScale = 0.7f

            var current = source
            val intermediates = mutableListOf<android.graphics.Bitmap>()

            repeat(passes) {
                val w = (current.width * stepScale).toInt().coerceAtLeast(1)
                val h = (current.height * stepScale).toInt().coerceAtLeast(1)
                val down = android.graphics.Bitmap.createScaledBitmap(current, w, h, true)
                if (current !== source) intermediates.add(current)
                current = down
            }

            // Upscale back to original in one smooth step
            val result = android.graphics.Bitmap.createScaledBitmap(current, source.width, source.height, true)
            intermediates.forEach { it.recycle() }
            current.recycle()
            result
        } catch (e: Exception) {
            source
        }
    }
}