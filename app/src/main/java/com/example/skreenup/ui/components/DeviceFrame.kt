package com.example.skreenup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import android.graphics.Typeface
import android.graphics.Paint as NativePaint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.OverlayLayer
import com.example.skreenup.ui.models.OverlayType
import com.example.skreenup.ui.models.DecorationShape
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.components.MockupRenderer.drawMockup

@Composable
fun DeviceFrame(
    screenshot: ImageBitmap?,
    deviceModel: DeviceModel,
    modifier: Modifier = Modifier,
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
    backgroundImageOffsetX: Float = 0f,
    backgroundImageOffsetY: Float = 0f,
    backgroundImageScale: Float = 1.0f,
    backgroundImageBlur: Float = 0f,
    rotationDegrees: Float = 0f,
    screenshotRotation: Float = 0f,
    screenBackgroundColor: Color = Color(0xFF2C2C2C),
    textLayers: List<OverlayLayer> = emptyList(),
    selectedTextLayerId: String? = null,
    showReflection: Boolean = true,
    shadowIntensity: Float = 0.3f,
    shadowSoftness: Float = 1.0f,
    showWatermark: Boolean = true,
    watermarkText: String = "Made with Skreenup",
    onScaleChange: (Float) -> Unit = {},
    onRotationChange: (Float) -> Unit = {},
    onFrameOffsetChange: (Float, Float) -> Unit = { _, _ -> },
    onTextLayerUpdate: (String, (OverlayLayer) -> OverlayLayer) -> Unit = { _, _ -> },
    onDeleteTextLayer: (String) -> Unit = {},
    onSelectTextLayer: (String?) -> Unit = {},
    onAddScreenshot: () -> Unit = {}
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var activeTarget by remember { mutableStateOf<Target>(Target.NONE) }
    var isEditingTextId by remember { mutableStateOf<String?>(null) }
    
    // Internal trackers to manage "breakable" snapping
    var sessionPanX by remember { mutableStateOf(0f) }
    var sessionPanY by remember { mutableStateOf(0f) }
    var sessionRotation by remember { mutableStateOf(0f) }

    // Snapping guides state
    var showSnapLineX by remember { mutableStateOf(false) }
    var showSnapLineY by remember { mutableStateOf(false) }

    // Use rememberUpdatedState to avoid restarting pointerInput when values change
    val currentScale by rememberUpdatedState(scale)
    val currentRotation by rememberUpdatedState(rotationDegrees)
    val currentFrameOffsetX by rememberUpdatedState(frameOffsetX)
    val currentFrameOffsetY by rememberUpdatedState(frameOffsetY)
    val currentDevice by rememberUpdatedState(deviceModel)
    val currentRatio by rememberUpdatedState(aspectRatio)
    val currentTextLayers by rememberUpdatedState(textLayers)
    val currentSelectedId by rememberUpdatedState(selectedTextLayerId)

    // Calculate frame rect for hit-testing and prompt positioning
    val frameRect = remember(canvasSize, currentDevice, currentScale, currentFrameOffsetX, currentFrameOffsetY, currentRatio) {
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return@remember Rect.Zero
        
        val canvasWidth = canvasSize.width.toFloat()
        val canvasHeight = canvasSize.height.toFloat()
        
        val compWidth: Float
        val compHeight: Float
        if (canvasWidth / canvasHeight > currentRatio.ratio) {
            compHeight = canvasHeight
            compWidth = compHeight * currentRatio.ratio
        } else {
            compWidth = canvasWidth
            compHeight = compWidth / currentRatio.ratio
        }
        val compLeft = (canvasWidth - compWidth) / 2
        val compTop = (canvasHeight - compHeight) / 2
        val exportScaleFactor = compWidth / 1000f

        val frameAspectRatio = currentDevice.aspectRatio
        val frameWidth: Float
        val frameHeight: Float
        if (compWidth / compHeight > frameAspectRatio) {
            frameHeight = compHeight * currentScale
            frameWidth = frameHeight * frameAspectRatio
        } else {
            frameWidth = compWidth * currentScale
            frameHeight = frameWidth / frameAspectRatio
        }

        val fx = currentFrameOffsetX * exportScaleFactor
        val fy = currentFrameOffsetY * exportScaleFactor
        val frameLeft = compLeft + (compWidth - frameWidth) / 2 + fx
        val frameTop = compTop + (compHeight - frameHeight) / 2 + fy
        
        Rect(Offset(frameLeft, frameTop), androidx.compose.ui.geometry.Size(frameWidth, frameHeight))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .pointerInput(canvasSize) {
                detectTapGestures(
                    onTap = { offset ->
                        val hit = hitTest(
                            point = offset,
                            canvasSize = canvasSize,
                            aspectRatio = currentRatio,
                            deviceModel = currentDevice,
                            scale = currentScale,
                            frameOffsetX = currentFrameOffsetX,
                            frameOffsetY = currentFrameOffsetY,
                            textLayers = currentTextLayers,
                            selectedId = currentSelectedId
                        )
                        when (hit) {
                            is HitResult.Overlay -> {
                                onSelectTextLayer(hit.id)
                                isEditingTextId = null // Reset editing if just selecting
                            }
                            is HitResult.DeleteOverlay -> {
                                onDeleteTextLayer(hit.id)
                                isEditingTextId = null
                            }
                            is HitResult.Frame -> onAddScreenshot()
                            else -> {
                                onSelectTextLayer(null)
                                isEditingTextId = null
                            }
                        }
                    },
                    onDoubleTap = { offset ->
                        val hit = hitTest(
                            point = offset,
                            canvasSize = canvasSize,
                            aspectRatio = currentRatio,
                            deviceModel = currentDevice,
                            scale = currentScale,
                            frameOffsetX = currentFrameOffsetX,
                            frameOffsetY = currentFrameOffsetY,
                            textLayers = currentTextLayers,
                            selectedId = currentSelectedId
                        )
                        if (hit is HitResult.Overlay) {
                            onSelectTextLayer(hit.id)
                            val layer = currentTextLayers.find { it.id == hit.id }
                            if (layer?.type == OverlayType.TEXT) {
                                isEditingTextId = hit.id
                            }
                        }
                    }
                )
            }
            .pointerInput(canvasSize) {
                detectTransformGestures { centroid, pan, zoom, rotationChange ->
                    // Determine target on first movement if not set
                    if (activeTarget == Target.NONE) {
                        val hit = hitTest(
                            point = centroid,
                            canvasSize = canvasSize,
                            aspectRatio = currentRatio,
                            deviceModel = currentDevice,
                            scale = currentScale,
                            frameOffsetX = currentFrameOffsetX,
                            frameOffsetY = currentFrameOffsetY,
                            textLayers = currentTextLayers
                        )
                        
                        when (hit) {
                            is HitResult.Frame -> {
                                activeTarget = Target.FRAME
                                sessionPanX = currentFrameOffsetX
                                sessionPanY = currentFrameOffsetY
                                sessionRotation = currentRotation
                            }
                            is HitResult.Overlay -> {
                                activeTarget = Target.TEXT
                                onSelectTextLayer(hit.id)
                                val layer = currentTextLayers.find { it.id == hit.id }
                                sessionPanX = layer?.offsetX ?: 0f
                                sessionPanY = layer?.offsetY ?: 0f
                                sessionRotation = layer?.rotation ?: 0f
                            }
                            is HitResult.DeleteOverlay -> {
                                activeTarget = Target.NONE
                            }
                            HitResult.NONE -> {
                                activeTarget = Target.BACKGROUND
                                sessionPanX = 0f
                                sessionPanY = 0f
                            }
                        }
                    }

                    // 1. Handle Scale (Zoom)
                    if (zoom != 1f) {
                        if (activeTarget == Target.TEXT && currentSelectedId != null) {
                            onTextLayerUpdate(currentSelectedId!!) { layer ->
                                if (layer.type == OverlayType.TEXT) {
                                    layer.copy(
                                        headingSize = (layer.headingSize * zoom).coerceIn(10f, 300f),
                                        subheadingSize = (layer.subheadingSize * zoom).coerceIn(8f, 200f)
                                    )
                                } else {
                                    layer.copy(scale = (layer.scale * zoom).coerceIn(0.1f, 10f))
                                }
                            }
                        } else {
                            onScaleChange((currentScale * zoom).coerceIn(0.1f, 2.0f))
                        }
                    }

                    // 2. Handle Rotation (Two fingers)
                    if (rotationChange != 0f) {
                        if (activeTarget == Target.TEXT && currentSelectedId != null) {
                            sessionRotation = (sessionRotation + rotationChange) % 360f
                            if (sessionRotation < 0) sessionRotation += 360f
                            onRotationChange(sessionRotation)
                        } else if (activeTarget == Target.FRAME) {
                            sessionRotation = (sessionRotation + rotationChange) % 360f
                            if (sessionRotation < 0) sessionRotation += 360f
                            
                            val snapPoints = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f, 360f)
                            val snapThreshold = 6f // Degrees
                            
                            var finalRotation = sessionRotation
                            for (point in snapPoints) {
                                if (kotlin.math.abs(sessionRotation - point) < snapThreshold) {
                                    finalRotation = if (point == 360f) 0f else point
                                    break
                                }
                            }
                            onRotationChange(finalRotation)
                        }
                    }

                    // 3. Handle Pan
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

                        sessionPanX += dx
                        sessionPanY += dy

                        when (activeTarget) {
                            Target.FRAME -> {
                                val snapThreshold = 25f
                                val snapX = kotlin.math.abs(sessionPanX) < snapThreshold
                                val snapY = kotlin.math.abs(sessionPanY) < snapThreshold
                                val finalX = if (snapX) 0f else sessionPanX
                                val finalY = if (snapY) 0f else sessionPanY
                                showSnapLineX = snapX
                                showSnapLineY = snapY
                                onFrameOffsetChange(finalX, finalY)
                            }
                            Target.TEXT -> {
                                if (currentSelectedId != null) {
                                    val snapThreshold = 25f
                                    val snapX = kotlin.math.abs(sessionPanX) < snapThreshold
                                    val snapY = kotlin.math.abs(sessionPanY) < snapThreshold
                                    val finalX = if (snapX) 0f else sessionPanX
                                    val finalY = if (snapY) 0f else sessionPanY
                                    showSnapLineX = snapX
                                    showSnapLineY = snapY
                                    onTextLayerUpdate(currentSelectedId!!) { it.copy(offsetX = finalX, offsetY = finalY) }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
            // Reset target and guides when all fingers are lifted
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.all { !it.pressed }) {
                            activeTarget = Target.NONE
                            showSnapLineX = false
                            showSnapLineY = false
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
                screenBackgroundColor = screenBackgroundColor,
                isExport = false,
                rotationDegrees = rotationDegrees,
                screenshotRotation = screenshotRotation,
                textLayers = textLayers,
                selectedTextLayerId = selectedTextLayerId,
                editingTextLayerId = isEditingTextId,
                showReflection = showReflection,
                shadowIntensity = shadowIntensity,
                shadowSoftness = shadowSoftness,
                showWatermark = showWatermark,
                watermarkText = watermarkText
            )
        }

        // ── Snapping Guidelines (Overlay) ──
        if (showSnapLineX || showSnapLineY) {
            val compWidth = if (canvasSize.width.toFloat() / canvasSize.height.toFloat() > currentRatio.ratio) {
                canvasSize.height * currentRatio.ratio
            } else {
                canvasSize.width.toFloat()
            }
            val compHeight = compWidth / currentRatio.ratio
            val compLeft = (canvasSize.width - compWidth) / 2
            val compTop = (canvasSize.height - compHeight) / 2

            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.dp.toPx()
                val dashPathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                val guideColor = Color(0xFFFA1E4E).copy(alpha = 0.6f) // Theme accent or bright red

                if (showSnapLineX) {
                    // Vertical center line
                    drawLine(
                        color = guideColor,
                        start = Offset(compLeft + compWidth / 2, compTop),
                        end = Offset(compLeft + compWidth / 2, compTop + compHeight),
                        strokeWidth = strokeWidth,
                        pathEffect = dashPathEffect
                    )
                }
                if (showSnapLineY) {
                    // Horizontal center line
                    drawLine(
                        color = guideColor,
                        start = Offset(compLeft, compTop + compHeight / 2),
                        end = Offset(compLeft + compWidth, compTop + compHeight / 2),
                        strokeWidth = strokeWidth,
                        pathEffect = dashPathEffect
                    )
                }
            }
        }

        // ── Direct Edit Dialog (REMOVED) ──

        // ── On-Screen Selection & Edit UI ──
        if (selectedTextLayerId != null && canvasSize.width > 0) {
            val layer = textLayers.find { it.id == selectedTextLayerId }
            if (layer != null && layer.isVisible) {
                val canvasWidth = canvasSize.width.toFloat()
                val canvasHeight = canvasSize.height.toFloat()
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
                val resScale = compWidth / 1000f

                var rectLeft: Float
                var rectTop: Float
                var rectW: Float
                var rectH: Float
                var gap = 0f

                if (layer.type == OverlayType.TEXT) {
                    // Re-calculate bounds (same logic as renderer)
                    val hPaint = NativePaint().apply {
                        textSize = layer.headingSize * resScale
                        typeface = getNativeTypeface(layer.headingFont, layer.headingBold)
                    }
                    val sPaint = NativePaint().apply {
                        textSize = layer.subheadingSize * resScale
                        typeface = getNativeTypeface(layer.subheadingFont, layer.subheadingBold)
                    }

                    val hLines = layer.heading.split("\n")
                    val sLines = layer.subheading.split("\n")
                    
                    var maxW = 0f
                    hLines.forEach { maxW = maxOf(maxW, hPaint.measureText(it)) }
                    sLines.forEach { maxW = maxOf(maxW, sPaint.measureText(it)) }

                    val hSpacing = hPaint.fontSpacing
                    val sSpacing = sPaint.fontSpacing
                    val hMetrics = hPaint.fontMetrics
                    val sMetrics = sPaint.fontMetrics
                    
                    val hBlockH = if (layer.heading.isNotEmpty()) (hLines.size - 1) * hSpacing + (hMetrics.descent - hMetrics.ascent) else 0f
                    val sBlockH = if (layer.subheading.isNotEmpty()) (sLines.size - 1) * sSpacing + (sMetrics.descent - sMetrics.ascent) else 0f
                    gap = layer.textGap * resScale
                    val totalH = hBlockH + (if (layer.heading.isNotEmpty() && layer.subheading.isNotEmpty()) gap else 0f) + sBlockH

                    val textCenterY = compTop + compHeight / 2 + (layer.offsetY * resScale)
                    val blockTop = textCenterY - (totalH / 2)

                    val horizontalMargin = 60f * resScale
                    val centerX = when (layer.textAlign) {
                        "LEFT" -> compLeft + horizontalMargin + (layer.offsetX * resScale)
                        "RIGHT" -> compLeft + compWidth - horizontalMargin + (layer.offsetX * resScale)
                        else -> compLeft + compWidth / 2 + (layer.offsetX * resScale)
                    }

                    val paddingPx = 0f // No padding for closer alignment
                    
                    rectLeft = when (layer.textAlign) {
                        "LEFT" -> centerX - paddingPx
                        "RIGHT" -> centerX - maxW - paddingPx
                        else -> centerX - (maxW / 2) - paddingPx
                    }
                    rectTop = blockTop - paddingPx
                    rectW = maxW + (paddingPx * 2)
                    rectH = totalH + (paddingPx * 2)
                } else {
                    val size = 150f * resScale * layer.scale
                    rectW = size
                    rectH = size
                    rectLeft = compLeft + compWidth / 2 + (layer.offsetX * resScale) - (rectW / 2)
                    rectTop = compTop + compHeight / 2 + (layer.offsetY * resScale) - (rectH / 2)
                }

                // Selection Box & Edit Fields
                Box(
                    modifier = Modifier
                        .size(
                            width = with(LocalDensity.current) { rectW.toDp() },
                            height = with(LocalDensity.current) { rectH.toDp() }
                        )
                        .graphicsLayer {
                            translationX = rectLeft
                            translationY = rectTop
                            rotationZ = layer.rotation
                        }
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFA1E4E).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    // Delete Button (X) - Top Left, bigger
                    Box(
                        modifier = Modifier
                            .offset(x = (-20).dp, y = (-20).dp)
                            .size(40.dp)
                            .background(Color(0xFFFA1E4E), CircleShape)
                            .clickable { onDeleteTextLayer(layer.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }

                    // Direct Edit Fields (Only for Text)
                    if (isEditingTextId == layer.id && layer.type == OverlayType.TEXT) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = when (layer.textAlign) {
                                "LEFT" -> Alignment.Start
                                "RIGHT" -> Alignment.End
                                else -> Alignment.CenterHorizontally
                            }
                        ) {
                            BasicTextField(
                                value = layer.heading,
                                onValueChange = { newVal -> onTextLayerUpdate(layer.id) { it.copy(heading = newVal) } },
                                textStyle = TextStyle(
                                    fontSize = with(LocalDensity.current) { (layer.headingSize * resScale).toSp() },
                                    textAlign = when (layer.textAlign) {
                                        "LEFT" -> androidx.compose.ui.text.style.TextAlign.Left
                                        "RIGHT" -> androidx.compose.ui.text.style.TextAlign.Right
                                        else -> androidx.compose.ui.text.style.TextAlign.Center
                                    },
                                    color = Color(layer.color),
                                    fontWeight = if (layer.headingBold) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = getComposeFontFamily(layer.headingFont),
                                    shadow = if (layer.textShadow) Shadow(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        offset = Offset(2f * resScale, 2f * resScale),
                                        blurRadius = 10f * resScale
                                    ) else null
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(with(LocalDensity.current) { gap.toDp() }))
                            BasicTextField(
                                value = layer.subheading,
                                onValueChange = { newVal -> onTextLayerUpdate(layer.id) { it.copy(subheading = newVal) } },
                                textStyle = TextStyle(
                                    fontSize = with(LocalDensity.current) { (layer.subheadingSize * resScale).toSp() },
                                    textAlign = when (layer.textAlign) {
                                        "LEFT" -> androidx.compose.ui.text.style.TextAlign.Left
                                        "RIGHT" -> androidx.compose.ui.text.style.TextAlign.Right
                                        else -> androidx.compose.ui.text.style.TextAlign.Center
                                    },
                                    color = Color(layer.color).copy(alpha = 0.8f),
                                    fontWeight = if (layer.subheadingBold) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = getComposeFontFamily(layer.subheadingFont),
                                    shadow = if (layer.textShadow) Shadow(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        offset = Offset(2f * resScale, 2f * resScale),
                                        blurRadius = 10f * resScale
                                    ) else null
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // ── "Select Screenshot" UI (Only inside the frame) ──
        if (screenshot == null && frameRect != Rect.Zero) {
            Box(
                modifier = Modifier
                    .size(
                        width = with(LocalDensity.current) { frameRect.width.toDp() },
                        height = with(LocalDensity.current) { frameRect.height.toDp() }
                    )
                    .graphicsLayer {
                        translationX = frameRect.left
                        translationY = frameRect.top
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.graphicsLayer {
                        rotationZ = -rotationDegrees 
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(
                                with(LocalDensity.current) { (frameRect.width * 0.25f).coerceIn(48.dp.toPx(), 80.dp.toPx()).toDp() }
                            )
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    if (frameRect.width > 120f) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "TAP TO ADD",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "SCREENSHOT",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.3f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class Target { NONE, FRAME, TEXT, BACKGROUND }

private sealed class HitResult {
    object NONE : HitResult()
    object Frame : HitResult()
    data class Overlay(val id: String) : HitResult()
    data class DeleteOverlay(val id: String) : HitResult()
}

private fun hitTest(
    point: Offset,
    canvasSize: IntSize,
    aspectRatio: CompositionAspectRatio,
    deviceModel: DeviceModel,
    scale: Float,
    frameOffsetX: Float,
    frameOffsetY: Float,
    textLayers: List<OverlayLayer>,
    selectedId: String? = null
): HitResult {
    val canvasWidth = canvasSize.width.toFloat()
    val canvasHeight = canvasSize.height.toFloat()
    if (canvasWidth <= 0 || canvasHeight <= 0) return HitResult.NONE

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

    // 2. Check Overlay Hit
    textLayers.asReversed().forEach { layer ->
        if (!layer.isVisible) return@forEach

        val hitPadding = 20f * exportScaleFactor
        var rectLeft: Float
        var rectTop: Float
        var rectWidth: Float
        var rectHeight: Float

        if (layer.type == OverlayType.TEXT) {
            val textCenterX = compLeft + compWidth / 2 + (layer.offsetX * exportScaleFactor)
            val textCenterY = compTop + compHeight / 2 + (layer.offsetY * exportScaleFactor)
            
            val hPaint = NativePaint().apply {
                textSize = layer.headingSize * exportScaleFactor
                typeface = getNativeTypeface(layer.headingFont, layer.headingBold)
            }
            val sPaint = NativePaint().apply {
                textSize = layer.subheadingSize * exportScaleFactor
                typeface = getNativeTypeface(layer.subheadingFont, layer.subheadingBold)
            }

            val hLines = layer.heading.split("\n")
            val sLines = layer.subheading.split("\n")
            
            var maxW = 0f
            hLines.forEach { maxW = maxOf(maxW, hPaint.measureText(it)) }
            sLines.forEach { maxW = maxOf(maxW, sPaint.measureText(it)) }

            val hSpacing = hPaint.fontSpacing
            val sSpacing = sPaint.fontSpacing
            val hMetrics = hPaint.fontMetrics
            val sMetrics = sPaint.fontMetrics
            
            val hBlockH = if (layer.heading.isNotEmpty()) (hLines.size - 1) * hSpacing + (hMetrics.descent - hMetrics.ascent) else 0f
            val sBlockH = if (layer.subheading.isNotEmpty()) (sLines.size - 1) * sSpacing + (sMetrics.descent - sMetrics.ascent) else 0f
            val gap = layer.textGap * exportScaleFactor
            val totalHeight = hBlockH + (if (layer.heading.isNotEmpty() && layer.subheading.isNotEmpty()) gap else 0f) + sBlockH
            
            val blockTop = textCenterY - (totalHeight / 2)
            
            val horizontalMargin = 60f * exportScaleFactor
            val centerX = when (layer.textAlign) {
                "LEFT" -> compLeft + horizontalMargin + (layer.offsetX * exportScaleFactor)
                "RIGHT" -> compLeft + compWidth - horizontalMargin + (layer.offsetX * exportScaleFactor)
                else -> compLeft + compWidth / 2 + (layer.offsetX * exportScaleFactor)
            }

            rectLeft = when (layer.textAlign) {
                "LEFT" -> centerX - hitPadding
                "RIGHT" -> centerX - maxW - hitPadding
                else -> centerX - (maxW / 2) - hitPadding
            }
            rectTop = blockTop - hitPadding
            rectWidth = maxW + (hitPadding * 2)
            rectHeight = totalHeight + (hitPadding * 2)
        } else {
            // SHAPE / ARROW / BUBBLE hit test (simplified to 100x100 base scaled)
            val size = 150f * exportScaleFactor * layer.scale
            rectWidth = size + hitPadding * 2
            rectHeight = size + hitPadding * 2
            rectLeft = compLeft + compWidth / 2 + (layer.offsetX * exportScaleFactor) - (rectWidth / 2)
            rectTop = compTop + compHeight / 2 + (layer.offsetY * exportScaleFactor) - (rectHeight / 2)
        }

        val hitRect = Rect(
            left = rectLeft,
            right = rectLeft + rectWidth,
            top = rectTop,
            bottom = rectTop + rectHeight
        )

        // TODO: Handle rotation in hit test properly
        if (hitRect.contains(point)) {
            // Check for delete button hit if selected
            if (layer.id == selectedId) {
                val deleteRectTop = rectTop
                val deleteRectLeft = rectLeft
                
                val xSize = 40f * exportScaleFactor // Make it big for clicking
                val xHitRect = Rect(Offset(deleteRectLeft - xSize/2, deleteRectTop - xSize/2), androidx.compose.ui.geometry.Size(xSize, xSize))
                if (xHitRect.contains(point)) return HitResult.DeleteOverlay(layer.id)
            }
            return HitResult.Overlay(layer.id)
        }
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
    if (frameRect.contains(point)) return HitResult.Frame

    return HitResult.NONE
}

private fun getNativeTypeface(fontName: String, isBold: Boolean): Typeface {
    val style = if (isBold) Typeface.BOLD else Typeface.NORMAL
    return when (fontName) {
        "POPPINS" -> Typeface.create("sans-serif", style)
        "INTER" -> Typeface.create("sans-serif-medium", style)
        "MONTSERRAT" -> Typeface.create("sans-serif-light", style)
        "BEBAS" -> Typeface.create("sans-serif-black", style)
        "PACIFICO" -> Typeface.create("cursive", style)
        "PLAYFAIR" -> Typeface.create("serif-monospace", style)
        "TIMES" -> Typeface.create("serif", style)
        "OSWALD" -> Typeface.create("sans-serif-condensed", style)
        "RALEWAY" -> Typeface.create("sans-serif-thin", style)
        "ANTON" -> Typeface.create("sans-serif-black", style)
        "QUICKSAND" -> Typeface.create("sans-serif-light", style)
        "LIBRE_BASKERVILLE" -> Typeface.create("serif", style)
        else -> Typeface.create("sans-serif", style)
    }
}

private fun getComposeFontFamily(fontName: String): FontFamily {
    return when (fontName) {
        "POPPINS" -> FontFamily.SansSerif
        "INTER" -> FontFamily.SansSerif
        "MONTSERRAT" -> FontFamily.SansSerif
        "BEBAS" -> FontFamily.SansSerif
        "PACIFICO" -> FontFamily.Cursive
        "PLAYFAIR" -> FontFamily.Serif
        "TIMES" -> FontFamily.Serif
        "OSWALD" -> FontFamily.SansSerif
        "RALEWAY" -> FontFamily.SansSerif
        "ANTON" -> FontFamily.SansSerif
        "QUICKSAND" -> FontFamily.SansSerif
        "LIBRE_BASKERVILLE" -> FontFamily.Serif
        else -> FontFamily.SansSerif
    }
}
