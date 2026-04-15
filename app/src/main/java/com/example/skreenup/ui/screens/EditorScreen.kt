package com.example.skreenup.ui.screens

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skreenup.data.Preset
import com.example.skreenup.data.Project
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.skreenup.ui.components.BackgroundType
import com.example.skreenup.ui.components.CompositionAspectRatio
import com.example.skreenup.ui.components.DeviceFrame
import com.example.skreenup.ui.components.FrameType
import com.example.skreenup.ui.theme.SkreenupTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectId: Long? = null,
    presetId: Long? = null,
    onBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var screenshotBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var selectedFrame by remember { mutableStateOf(FrameType.ANDROID_PHONE) }
    var scale by remember { mutableFloatStateOf(0.8f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    
    var backgroundType by remember { mutableStateOf(BackgroundType.GRADIENT) }
    var backgroundColor by remember { mutableStateOf(Color(0xFF3F51B5)) }
    var compositionAspectRatio by remember { mutableStateOf(CompositionAspectRatio.SQUARE) }
    
    var showWatermark by remember { mutableStateOf(false) }
    var watermarkText by remember { mutableStateOf("Created with Skreenup") }
    var projectName by remember { mutableStateOf("New Project") }

    LaunchedEffect(projectId, presetId) {
        if (projectId != null) {
            viewModel.loadProject(projectId)
        } else if (presetId != null) {
            // Logic to load from preset would be here
            // For now, let's assume we can fetch preset by id
        }
    }

    val currentProject by viewModel.currentProject.collectAsState()
    val presets by viewModel.presets.collectAsState()

    LaunchedEffect(currentProject) {
        currentProject?.let { project ->
            projectName = project.name
            selectedFrame = FrameType.valueOf(project.frameType)
            backgroundType = BackgroundType.valueOf(project.backgroundType)
            backgroundColor = Color(project.backgroundValue.toLong(16).toInt())
            scale = project.scale
            offsetX = project.positionX
            offsetY = project.positionY
            showWatermark = project.showWatermark
            watermarkText = project.watermarkText
            compositionAspectRatio = CompositionAspectRatio.valueOf(project.aspectRatio)
            // screenshotBitmap loading from Uri would be here if persisted
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            scope.launch {
                val bitmap = loadBitmapFromUri(context, it)
                screenshotBitmap = bitmap?.asImageBitmap()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(projectName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Import Screenshot")
                    }
                    IconButton(onClick = {
                        val project = Project(
                            id = projectId ?: 0L,
                            name = projectName,
                            screenshotUri = null, // Path would be here
                            frameType = selectedFrame.name,
                            backgroundType = backgroundType.name,
                            backgroundValue = backgroundColor.value.toString(16),
                            scale = scale,
                            positionX = offsetX,
                            positionY = offsetY,
                            showWatermark = showWatermark,
                            watermarkText = watermarkText,
                            aspectRatio = compositionAspectRatio.name
                        )
                        viewModel.saveProject(project)
                        Toast.makeText(context, "Project Saved", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Project")
                    }
                    IconButton(onClick = {
                        // Capture and Export
                        scope.launch {
                            val bitmap = captureToBitmap(
                                screenshot = screenshotBitmap,
                                frameType = selectedFrame,
                                backgroundType = backgroundType,
                                backgroundColor = backgroundColor,
                                gradientColors = listOf(backgroundColor, Color(0xFF006A6A)),
                                scale = scale,
                                offsetX = offsetX,
                                offsetY = offsetY,
                                aspectRatio = compositionAspectRatio,
                                showWatermark = showWatermark,
                                watermarkText = watermarkText
                            )
                            val success = saveBitmapToGallery(context, bitmap)
                            if (success) {
                                Toast.makeText(context, "Exported to Gallery!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Export failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                    }
                }
            )
        },
        bottomBar = {
            EditorControls(
                selectedFrame = selectedFrame,
                onFrameSelected = { selectedFrame = it },
                scale = scale,
                onScaleChanged = { scale = it },
                offsetX = offsetX,
                onOffsetXChanged = { offsetX = it },
                offsetY = offsetY,
                onOffsetYChanged = { offsetY = it },
                backgroundType = backgroundType,
                onBackgroundTypeSelected = { backgroundType = it },
                aspectRatio = compositionAspectRatio,
                onAspectRatioSelected = { compositionAspectRatio = it },
                showWatermark = showWatermark,
                onWatermarkToggled = { showWatermark = it },
                watermarkText = watermarkText,
                onWatermarkTextChanged = { watermarkText = it },
                presets = presets,
                onApplyPreset = { preset ->
                    selectedFrame = FrameType.valueOf(preset.frameType)
                    backgroundType = BackgroundType.valueOf(preset.backgroundType)
                    backgroundColor = Color(preset.backgroundValue.toLong(16).toInt())
                    scale = preset.scale
                    offsetX = preset.positionX
                    offsetY = preset.positionY
                    showWatermark = preset.showWatermark
                    watermarkText = preset.watermarkText
                    compositionAspectRatio = CompositionAspectRatio.valueOf(preset.aspectRatio)
                },
                onSavePreset = {
                    val preset = Preset(
                        name = "Preset ${System.currentTimeMillis() % 1000}",
                        frameType = selectedFrame.name,
                        backgroundType = backgroundType.name,
                        backgroundValue = backgroundColor.value.toString(16),
                        scale = scale,
                        positionX = offsetX,
                        positionY = offsetY,
                        showWatermark = showWatermark,
                        watermarkText = watermarkText,
                        aspectRatio = compositionAspectRatio.name
                    )
                    viewModel.savePreset(preset)
                    Toast.makeText(context, "Preset Saved", Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            DeviceFrame(
                screenshot = screenshotBitmap,
                frameType = selectedFrame,
                backgroundType = backgroundType,
                backgroundColor = backgroundColor,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                aspectRatio = compositionAspectRatio,
                showWatermark = showWatermark,
                watermarkText = watermarkText
            )
        }
    }
}

@Composable
fun EditorControls(
    selectedFrame: FrameType,
    onFrameSelected: (FrameType) -> Unit,
    scale: Float,
    onScaleChanged: (Float) -> Unit,
    offsetX: Float,
    onOffsetXChanged: (Float) -> Unit,
    offsetY: Float,
    onOffsetYChanged: (Float) -> Unit,
    backgroundType: BackgroundType,
    onBackgroundTypeSelected: (BackgroundType) -> Unit,
    aspectRatio: CompositionAspectRatio,
    onAspectRatioSelected: (CompositionAspectRatio) -> Unit,
    showWatermark: Boolean,
    onWatermarkToggled: (Boolean) -> Unit,
    watermarkText: String,
    onWatermarkTextChanged: (String) -> Unit,
    presets: List<Preset> = emptyList(),
    onApplyPreset: (Preset) -> Unit,
    onSavePreset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Presets List
        if (presets.isNotEmpty()) {
            Text("Apply Preset", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                items(presets) { preset ->
                    FilterChip(
                        selected = false,
                        onClick = { onApplyPreset(preset) },
                        label = { Text(preset.name) },
                        leadingIcon = { Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }

        // Frame Selection
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Device Frame", style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = onSavePreset) {
                Icon(Icons.Default.Add, contentDescription = "Save as Preset", tint = MaterialTheme.colorScheme.primary)
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(FrameType.entries) { frame ->
                FrameChip(
                    frameType = frame,
                    isSelected = selectedFrame == frame,
                    onClick = { onFrameSelected(frame) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Watermark Controls
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(8.dp))
            Text("Watermark", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Switch(checked = showWatermark, onCheckedChange = onWatermarkToggled)
        }
        
        if (showWatermark) {
            TextField(
                value = watermarkText,
                onValueChange = onWatermarkTextChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter watermark text") },
                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null) },
                singleLine = true
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Background and Aspect Ratio
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Background", style = MaterialTheme.typography.titleSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(BackgroundType.entries) { type ->
                        FilterChip(
                            selected = backgroundType == type,
                            onClick = { onBackgroundTypeSelected(type) },
                            label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Export Ratio", style = MaterialTheme.typography.titleSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(CompositionAspectRatio.entries) { ratio ->
                        FilterChip(
                            selected = aspectRatio == ratio,
                            onClick = { onAspectRatioSelected(ratio) },
                            label = { Text(ratio.label) },
                            leadingIcon = { Icon(Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Transformation Controls
        Text("Scale", style = MaterialTheme.typography.labelMedium)
        Slider(value = scale, onValueChange = onScaleChanged, valueRange = 0.1f..1f)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Offset X", style = MaterialTheme.typography.labelMedium)
                Slider(value = offsetX, onValueChange = onOffsetXChanged, valueRange = -500f..500f)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Offset Y", style = MaterialTheme.typography.labelMedium)
                Slider(value = offsetY, onValueChange = onOffsetYChanged, valueRange = -500f..500f)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrameChip(frameType: FrameType, isSelected: Boolean, onClick: () -> Unit) {
    val icon = when (frameType) {
        FrameType.ANDROID_PHONE -> Icons.Default.PhoneAndroid
        FrameType.IPHONE -> Icons.Default.PhoneIphone
        FrameType.TABLET -> Icons.Default.TabletMac
        FrameType.DESKTOP -> Icons.Default.Tv
    }
    val label = when (frameType) {
        FrameType.ANDROID_PHONE -> "Android"
        FrameType.IPHONE -> "iPhone"
        FrameType.TABLET -> "Tablet"
        FrameType.DESKTOP -> "Desktop"
    }

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
    )
}

suspend fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false)
            .build()

        val result = loader.execute(request)
        if (result is SuccessResult) {
            (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
        } else {
            null
        }
    }
}

suspend fun captureToBitmap(
    screenshot: ImageBitmap?,
    frameType: FrameType,
    backgroundType: BackgroundType,
    backgroundColor: Color,
    gradientColors: List<Color>,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    aspectRatio: CompositionAspectRatio,
    showWatermark: Boolean = false,
    watermarkText: String = ""
): Bitmap {
    return withContext(Dispatchers.Default) {
        // High-res export size (e.g., 2048px width)
        val exportWidth = 2048
        val exportHeight = (exportWidth / aspectRatio.ratio).toInt()
        
        val bitmap = Bitmap.createBitmap(exportWidth, exportHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        
        // 1. Draw Background
        when (backgroundType) {
            BackgroundType.SOLID -> {
                paint.color = backgroundColor.toArgbInt()
                canvas.drawRect(0f, 0f, exportWidth.toFloat(), exportHeight.toFloat(), paint)
            }
            BackgroundType.GRADIENT -> {
                val shader = android.graphics.LinearGradient(
                    0f, 0f, exportWidth.toFloat(), exportHeight.toFloat(),
                    gradientColors.map { it.toArgbInt() }.toIntArray(),
                    null, android.graphics.Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, exportWidth.toFloat(), exportHeight.toFloat(), paint)
                paint.shader = null
            }
            BackgroundType.BLUR -> {
                if (screenshot != null) {
                    canvas.drawBitmap(screenshot.asAndroidBitmap(), null, android.graphics.Rect(0, 0, exportWidth, exportHeight), paint)
                    paint.color = android.graphics.Color.argb(100, 255, 255, 255)
                    canvas.drawRect(0f, 0f, exportWidth.toFloat(), exportHeight.toFloat(), paint)
                } else {
                    paint.color = android.graphics.Color.LTGRAY
                    canvas.drawRect(0f, 0f, exportWidth.toFloat(), exportHeight.toFloat(), paint)
                }
            }
        }

        // 2. Draw Frame and Screenshot
        val frameAspectRatio = when (frameType) {
            FrameType.ANDROID_PHONE, FrameType.IPHONE -> 9f / 19.5f
            FrameType.TABLET -> 4f / 3f
            FrameType.DESKTOP -> 16f / 10f
        }

        var fWidth: Float
        var fHeight: Float
        if (exportWidth / exportHeight.toFloat() > frameAspectRatio) {
            fHeight = exportHeight * scale
            fWidth = fHeight * frameAspectRatio
        } else {
            fWidth = exportWidth * scale
            fHeight = fWidth / frameAspectRatio
        }

        // Adjust offsets to be relative to export size
        val fLeft = (exportWidth - fWidth) / 2 + offsetX * (exportWidth / 500f)
        val fTop = (exportHeight - fHeight) / 2 + offsetY * (exportHeight / 500f)
        
        // Draw Shadow
        paint.style = android.graphics.Paint.Style.FILL
        paint.color = android.graphics.Color.argb(60, 0, 0, 0)
        canvas.drawRoundRect(fLeft - 20, fTop - 20, fLeft + fWidth + 20, fTop + fHeight + 20, 80f, 80f, paint)

        // Draw Screenshot clipped to frame
        if (screenshot != null) {
            val path = android.graphics.Path()
            path.addRoundRect(fLeft, fTop, fLeft + fWidth, fTop + fHeight, 80f, 80f, android.graphics.Path.Direction.CW)
            canvas.save()
            canvas.clipPath(path)
            canvas.drawBitmap(screenshot.asAndroidBitmap(), null, android.graphics.RectF(fLeft, fTop, fLeft + fWidth, fTop + fHeight), paint)
            canvas.restore()
        }

        // Draw Frame Border
        paint.color = android.graphics.Color.BLACK
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = 15f
        canvas.drawRoundRect(fLeft, fTop, fLeft + fWidth, fTop + fHeight, 80f, 80f, paint)

        // 3. Draw Watermark
        if (showWatermark && watermarkText.isNotEmpty()) {
            paint.style = android.graphics.Paint.Style.FILL
            paint.color = android.graphics.Color.argb(180, 255, 255, 255)
            paint.textSize = 48f
            paint.textAlign = android.graphics.Paint.Align.RIGHT
            canvas.drawText(watermarkText, exportWidth - 40f, exportHeight - 40f, paint)
        }

        bitmap
    }
}

fun Color.toArgbInt(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}

suspend fun saveBitmapToGallery(context: android.content.Context, bitmap: Bitmap): Boolean {
    return withContext(Dispatchers.IO) {
        val filename = "Skreenup_${System.currentTimeMillis()}.png"
        var imageUri: Uri?

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Skreenup")
            }
            imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val fos: OutputStream? = imageUri?.let { context.contentResolver.openOutputStream(it) }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditorPreview() {
    SkreenupTheme {
        EditorScreen(onBack = {})
    }
}
