package com.example.skreenup

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.skreenup.navigation.AboutTab
import com.example.skreenup.navigation.AdjustTab
import com.example.skreenup.navigation.BackgroundTab
import com.example.skreenup.navigation.FrameTab
import com.example.skreenup.navigation.SkreenupTabKey
import com.example.skreenup.ui.screens.tabs.AboutTabScreen
import com.example.skreenup.ui.screens.tabs.AdjustTabScreen
import com.example.skreenup.ui.screens.tabs.BackgroundTabScreen
import com.example.skreenup.ui.screens.tabs.FrameTabScreen
import com.example.skreenup.ui.theme.SkreenupTheme
import com.example.skreenup.ui.components.BackgroundType
import com.example.skreenup.ui.components.CompositionAspectRatio
import com.example.skreenup.ui.components.DeviceFrame
import com.example.skreenup.ui.components.FrameType
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.screens.EditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkreenupTheme {
                SkreenupApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkreenupApp(editorViewModel: EditorViewModel = viewModel()) {
    val backStack = rememberNavBackStack(FrameTab)
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedDevice by editorViewModel.selectedDevice.collectAsState()
    val screenshot by editorViewModel.screenshot.collectAsState()
    val backgroundType by editorViewModel.backgroundType.collectAsState()
    val backgroundColor by editorViewModel.backgroundColor.collectAsState()
    val gradientColors by editorViewModel.gradientColors.collectAsState()
    val backgroundImage by editorViewModel.backgroundImage.collectAsState()
    val scale by editorViewModel.scale.collectAsState()
    val aspectRatio by editorViewModel.aspectRatio.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { editorViewModel.setScreenshot(it) }
    }

    SkreenupTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Skreenup") },
                    actions = {
                        IconButton(onClick = { 
                            scope.launch {
                                val bitmap = captureToBitmap(
                                    screenshot = screenshot,
                                    deviceModel = selectedDevice,
                                    backgroundType = backgroundType,
                                    backgroundColor = backgroundColor,
                                    gradientColors = gradientColors,
                                    backgroundImage = backgroundImage,
                                    scale = scale,
                                    aspectRatio = aspectRatio
                                )
                                val success = saveBitmapToGallery(context, bitmap)
                                if (success) {
                                    Toast.makeText(context, "Exported to Gallery!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Export failed.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(Icons.Rounded.Share, contentDescription = "Share")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    val currentKey = backStack.lastOrNull()

                    NavigationBarItem(
                        selected = currentKey == FrameTab,
                        onClick = { 
                            if (currentKey != FrameTab) {
                                backStack.clear()
                                backStack.add(FrameTab)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Smartphone, contentDescription = "Frame") },
                        label = { Text("Frame") }
                    )
                    NavigationBarItem(
                        selected = currentKey == BackgroundTab,
                        onClick = { 
                            if (currentKey != BackgroundTab) {
                                backStack.clear()
                                backStack.add(BackgroundTab)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Palette, contentDescription = "Background") },
                        label = { Text("Background") }
                    )
                    NavigationBarItem(
                        selected = currentKey == AdjustTab,
                        onClick = { 
                            if (currentKey != AdjustTab) {
                                backStack.clear()
                                backStack.add(AdjustTab)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Tune, contentDescription = "Adjust") },
                        label = { Text("Adjust") }
                    )
                    NavigationBarItem(
                        selected = currentKey == AboutTab,
                        onClick = { 
                            if (currentKey != AboutTab) {
                                backStack.clear()
                                backStack.add(AboutTab)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Info, contentDescription = "About") },
                        label = { Text("About") }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top area: Device Preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    DeviceFrame(
                        screenshot = screenshot,
                        deviceModel = selectedDevice,
                        backgroundType = backgroundType,
                        backgroundColor = backgroundColor,
                        gradientColors = gradientColors,
                        backgroundImage = backgroundImage,
                        scale = scale,
                        aspectRatio = aspectRatio
                    )

                    if (screenshot == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "Select Screenshot",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Bottom area: Tab Content
                NavDisplay(
                    backStack = backStack,
                    onBack = { 
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<FrameTab> {
                            FrameTabScreen(editorViewModel)
                        }
                        entry<BackgroundTab> {
                            BackgroundTabScreen(editorViewModel)
                        }
                        entry<AdjustTab> {
                            AdjustTabScreen(editorViewModel)
                        }
                        entry<AboutTab> {
                            AboutTabScreen()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                )
            }
        }
    }
}

suspend fun captureToBitmap(
    screenshot: ImageBitmap?,
    deviceModel: DeviceModel,
    backgroundType: BackgroundType,
    backgroundColor: Color,
    gradientColors: List<Color>,
    backgroundImage: ImageBitmap?,
    scale: Float,
    aspectRatio: CompositionAspectRatio
): Bitmap {
    return withContext(Dispatchers.Default) {
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
            BackgroundType.IMAGE -> {
                if (backgroundImage != null) {
                    canvas.drawBitmap(backgroundImage.asAndroidBitmap(), null, android.graphics.Rect(0, 0, exportWidth, exportHeight), paint)
                } else {
                    paint.color = android.graphics.Color.LTGRAY
                    canvas.drawRect(0f, 0f, exportWidth.toFloat(), exportHeight.toFloat(), paint)
                }
            }
        }

        // 2. Draw Frame and Screenshot
        val frameAspectRatio = deviceModel.aspectRatio
        var fWidth: Float
        var fHeight: Float
        if (exportWidth / exportHeight.toFloat() > frameAspectRatio) {
            fHeight = exportHeight * scale
            fWidth = fHeight * frameAspectRatio
        } else {
            fWidth = exportWidth * scale
            fHeight = fWidth / frameAspectRatio
        }

        val fLeft = (exportWidth - fWidth) / 2
        val fTop = (exportHeight - fHeight) / 2
        
        val scaleFactor = exportWidth / 400f // Assuming 400dp as base width for relative sizing
        val cornerRadius = deviceModel.cornerRadiusDp * scaleFactor

        // Shadow
        paint.style = android.graphics.Paint.Style.FILL
        paint.color = android.graphics.Color.argb(60, 0, 0, 0)
        canvas.drawRoundRect(fLeft - 10 * scaleFactor, fTop - 10 * scaleFactor, fLeft + fWidth + 10 * scaleFactor, fTop + fHeight + 10 * scaleFactor, cornerRadius, cornerRadius, paint)

        // Screenshot
        if (screenshot != null) {
            val path = android.graphics.Path()
            path.addRoundRect(fLeft, fTop, fLeft + fWidth, fTop + fHeight, cornerRadius, cornerRadius, android.graphics.Path.Direction.CW)
            canvas.save()
            canvas.clipPath(path)
            canvas.drawBitmap(screenshot.asAndroidBitmap(), null, android.graphics.RectF(fLeft, fTop, fLeft + fWidth, fTop + fHeight), paint)
            canvas.restore()
        }

        // Frame Border
        paint.color = android.graphics.Color.BLACK
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = 4 * scaleFactor
        canvas.drawRoundRect(fLeft, fTop, fLeft + fWidth, fTop + fHeight, cornerRadius, cornerRadius, paint)

        // 3. Draw Camera Cutout / Notch
        if (deviceModel.type == FrameType.ANDROID_PHONE || deviceModel.type == FrameType.IPHONE) {
            val speakerWidth = fWidth * 0.15f
            val speakerHeight = 4 * scaleFactor
            paint.style = android.graphics.Paint.Style.FILL
            paint.color = android.graphics.Color.BLACK
            canvas.drawRoundRect(
                fLeft + (fWidth - speakerWidth) / 2,
                fTop + 10 * scaleFactor,
                fLeft + (fWidth + speakerWidth) / 2,
                fTop + 10 * scaleFactor + speakerHeight,
                2 * scaleFactor, 2 * scaleFactor, paint
            )
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
