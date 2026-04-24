package com.example.skreenup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.skreenup.R
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
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
    textLayers: List<com.example.skreenup.ui.models.TextLayer> = emptyList(),
    selectedTextLayerId: String? = null,
    showReflection: Boolean = true,
    shadowIntensity: Float = 0.3f,
    shadowSoftness: Float = 1.0f,
    showWatermark: Boolean = true,
    watermarkText: String = "Made with Skreenup",
    onScaleChange: (Float) -> Unit = {},
    onRotationChange: (Float) -> Unit = {},
    onFrameOffsetChange: (Float, Float) -> Unit = { _, _ -> },
    onTextLayerUpdate: (String, (com.example.skreenup.ui.models.TextLayer) -> com.example.skreenup.ui.models.TextLayer) -> Unit = { _, _ -> },
    onDeleteTextLayer: (String) -> Unit = {},
    onSelectTextLayer: (String?) -> Unit = {},
    onAddScreenshot: () -> Unit = {}
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var activeTarget by remember { mutableStateOf<Target>(Target.NONE) }
    var showDirectEditDialog by remember { mutableStateOf<com.example.skreenup.ui.models.TextLayer?>(null) }
    
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
                            is HitResult.Text -> onSelectTextLayer(hit.id)
                            is HitResult.DeleteText -> onDeleteTextLayer(hit.id)
                            is HitResult.Frame -> onAddScreenshot()
                            else -> onSelectTextLayer(null)
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
                        if (hit is HitResult.Text) {
                            showDirectEditDialog = currentTextLayers.find { it.id == hit.id }
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
                            is HitResult.Text -> {
                                activeTarget = Target.TEXT
                                onSelectTextLayer(hit.id)
                                val layer = currentTextLayers.find { it.id == hit.id }
                                sessionPanX = layer?.offsetX ?: 0f
                                sessionPanY = layer?.offsetY ?: 0f
                                sessionRotation = 0f
                            }
                            is HitResult.DeleteText -> {
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
                                layer.copy(
                                    headingSize = (layer.headingSize * zoom).coerceIn(10f, 300f),
                                    subheadingSize = (layer.subheadingSize * zoom).coerceIn(8f, 200f)
                                )
                            }
                        } else {
                            onScaleChange((currentScale * zoom).coerceIn(0.1f, 2.0f))
                        }
                    }

                    // 2. Handle Rotation (Two fingers)
                    if (rotationChange != 0f && activeTarget == Target.FRAME) {
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

        // ── Direct Edit Dialog ──
        showDirectEditDialog?.let { layer ->
            var hText by remember(layer.id) { mutableStateOf(layer.heading) }
            var sText by remember(layer.id) { mutableStateOf(layer.subheading) }
            
            AlertDialog(
                onDismissRequest = { showDirectEditDialog = null },
                title = { Text("Edit Text") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = hText,
                            onValueChange = { 
                                hText = it
                                onTextLayerUpdate(layer.id) { l -> l.copy(heading = it) }
                            },
                            label = { Text("Heading") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = sText,
                            onValueChange = { 
                                sText = it
                                onTextLayerUpdate(layer.id) { l -> l.copy(subheading = it) }
                            },
                            label = { Text("Subheading") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showDirectEditDialog = null }) {
                        Text("Done")
                    }
                }
            )
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
    data class Text(val id: String) : HitResult()
    data class DeleteText(val id: String) : HitResult()
}

private fun hitTest(
    point: Offset,
    canvasSize: IntSize,
    aspectRatio: CompositionAspectRatio,
    deviceModel: DeviceModel,
    scale: Float,
    frameOffsetX: Float,
    frameOffsetY: Float,
    textLayers: List<com.example.skreenup.ui.models.TextLayer>,
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

    // 2. Check Text Hit (Rough bounding box) - Reverse order for top-most first
    textLayers.asReversed().forEach { layer ->
        val textCenterX = compLeft + compWidth / 2 + (layer.offsetX * exportScaleFactor)
        val textCenterY = compTop + compHeight / 2 + (layer.offsetY * exportScaleFactor)
        
        // Rough estimate of total height based on sizes and gap
        val totalHeight = (layer.headingSize + layer.subheadingSize + layer.textGap) * exportScaleFactor
        val blockTop = textCenterY - (totalHeight / 2)
        
        // Use a more accurate hit test if needed, but for now heuristic
        val hitPadding = 20f * exportScaleFactor
        val textHitRect = Rect(
            left = textCenterX - 300f * exportScaleFactor,
            right = textCenterX + 300f * exportScaleFactor,
            top = blockTop - hitPadding,
            bottom = blockTop + totalHeight + hitPadding
        )

        if (textHitRect.contains(point)) {
            // Check for delete button hit if selected
            if (layer.id == selectedId) {
                val padding = 12f * exportScaleFactor
                val rectTop = blockTop - padding
                val rectLeft = when (layer.textAlign) {
                    "LEFT" -> textCenterX - padding
                    "RIGHT" -> textCenterX - 400f * exportScaleFactor - padding // Still rough
                    else -> textCenterX - 200f * exportScaleFactor - padding
                }
                
                val xSize = 24f * exportScaleFactor
                val xHitRect = Rect(Offset(rectLeft - xSize/2, rectTop - xSize/2), androidx.compose.ui.geometry.Size(xSize, xSize))
                if (xHitRect.contains(point)) return HitResult.DeleteText(layer.id)
            }
            return HitResult.Text(layer.id)
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
