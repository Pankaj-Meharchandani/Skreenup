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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Save
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
import com.example.skreenup.navigation.About
import com.example.skreenup.navigation.AdjustTab
import com.example.skreenup.navigation.BackgroundTab
import com.example.skreenup.navigation.Editor
import com.example.skreenup.navigation.FrameTab
import com.example.skreenup.navigation.SkreenupNavKey
import com.example.skreenup.navigation.SkreenupTabKey
import com.example.skreenup.ui.screens.AboutScreen
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
import com.example.skreenup.ui.models.CutoutType
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

@Composable
fun SkreenupApp() {
    val mainBackStack = rememberNavBackStack(Editor)

    NavDisplay(
        backStack = mainBackStack,
        onBack = { 
            if (mainBackStack.size > 1) {
                mainBackStack.removeAt(mainBackStack.size - 1)
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Editor> {
                EditorScreen(
                    onNavigateToAbout = { mainBackStack.add(About) }
                )
            }
            entry<About> {
                AboutScreen(
                    onBack = { 
                        if (mainBackStack.size > 1) {
                            mainBackStack.removeAt(mainBackStack.size - 1)
                        }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateToAbout: () -> Unit,
    editorViewModel: EditorViewModel = viewModel()
) {
    val tabBackStack = rememberNavBackStack(FrameTab)
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedDevice by editorViewModel.selectedDevice.collectAsState()
    val screenshot by editorViewModel.screenshot.collectAsState()
    val backgroundType by editorViewModel.backgroundType.collectAsState()
    val backgroundColor by editorViewModel.backgroundColor.collectAsState()
    val gradientColors by editorViewModel.gradientColors.collectAsState()
    val backgroundImage by editorViewModel.backgroundImage.collectAsState()
    val scale by editorViewModel.scale.collectAsState()
    val imageScale by editorViewModel.imageScale.collectAsState()
    val aspectRatio by editorViewModel.aspectRatio.collectAsState()
    
    val frameOffsetX by editorViewModel.frameOffsetX.collectAsState()
    val frameOffsetY by editorViewModel.frameOffsetY.collectAsState()
    val screenshotOffsetX by editorViewModel.screenshotOffsetX.collectAsState()
    val screenshotOffsetY by editorViewModel.screenshotOffsetY.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { editorViewModel.setScreenshot(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skreenup") },
                actions = {
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(Icons.Rounded.Info, contentDescription = "About")
                    }
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
                                imageScale = imageScale,
                                frameOffsetX = frameOffsetX,
                                frameOffsetY = frameOffsetY,
                                screenshotOffsetX = screenshotOffsetX,
                                screenshotOffsetY = screenshotOffsetY,
                                aspectRatio = aspectRatio
                            )
                            val success = saveBitmapToGallery(context, bitmap)
                            if (success) {
                                Toast.makeText(context, "Export Saved!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Export failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Rounded.Save, contentDescription = "Save")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val currentTab = tabBackStack.lastOrNull()

                NavigationBarItem(
                    selected = currentTab == FrameTab,
                    onClick = { 
                        if (currentTab != FrameTab) {
                            tabBackStack.clear()
                            tabBackStack.add(FrameTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Smartphone, contentDescription = "Frame") },
                    label = { Text("Frame") }
                )
                NavigationBarItem(
                    selected = currentTab == BackgroundTab,
                    onClick = { 
                        if (currentTab != BackgroundTab) {
                            tabBackStack.clear()
                            tabBackStack.add(BackgroundTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Palette, contentDescription = "Background") },
                    label = { Text("Background") }
                )
                NavigationBarItem(
                    selected = currentTab == AdjustTab,
                    onClick = { 
                        if (currentTab != AdjustTab) {
                            tabBackStack.clear()
                            tabBackStack.add(AdjustTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Tune, contentDescription = "Adjust") },
                    label = { Text("Adjust") }
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
                    imageScale = imageScale,
                    frameOffsetX = frameOffsetX,
                    frameOffsetY = frameOffsetY,
                    screenshotOffsetX = screenshotOffsetX,
                    screenshotOffsetY = screenshotOffsetY,
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
                backStack = tabBackStack,
                onBack = { 
                    if (tabBackStack.size > 1) {
                        tabBackStack.removeAt(tabBackStack.size - 1)
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            )
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
    imageScale: Float,
    frameOffsetX: Float,
    frameOffsetY: Float,
    screenshotOffsetX: Float,
    screenshotOffsetY: Float,
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

        val exportScaleFactor = exportWidth / 1000f // Scaling px to export
        val fLeft = (exportWidth - fWidth) / 2 + frameOffsetX * exportScaleFactor
        val fTop = (exportHeight - fHeight) / 2 + frameOffsetY * exportScaleFactor
        
        val scaleFactor = exportWidth / 400f
        val pixelScale = fWidth / deviceModel.widthMm
        val baseWidthMm = 78f
        val cornerRadius = (deviceModel.cornerRadiusDp * (fWidth / baseWidthMm)) * 0.5f

        // Draw Laptop Chassis
        if (deviceModel.hasChassis && deviceModel.type == FrameType.DESKTOP) {
            val chassisHeightPx = 8 * pixelScale
            val chassisWidthPx = fWidth * 1.15f
            val chassisRect = android.graphics.RectF(
                fLeft - (chassisWidthPx - fWidth) / 2, 
                fTop + fHeight,
                fLeft + fWidth + (chassisWidthPx - fWidth) / 2,
                fTop + fHeight + chassisHeightPx
            )
            paint.style = android.graphics.Paint.Style.FILL
            paint.color = android.graphics.Color.parseColor("#2C2C2C")
            canvas.drawRoundRect(chassisRect, 4 * pixelScale, 4 * pixelScale, paint)
            
            // Notch
            val notchWidthPx = chassisWidthPx * 0.15f
            val notchHeightPx = chassisHeightPx * 0.4f
            paint.color = android.graphics.Color.parseColor("#1A1A1A")
            canvas.drawRect(
                chassisRect.left + (chassisWidthPx - notchWidthPx) / 2,
                chassisRect.top,
                chassisRect.left + (chassisWidthPx + notchWidthPx) / 2,
                chassisRect.top + notchHeightPx,
                paint
            )
        }

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
            
            // 'Fit' logic
            val imgAspectRatio = screenshot.width.toFloat() / screenshot.height.toFloat()
            var imgWidth: Float
            var imgHeight: Float
            if (fWidth / fHeight > imgAspectRatio) {
                imgHeight = fHeight * imageScale
                imgWidth = imgHeight * imgAspectRatio
            } else {
                imgWidth = fWidth * imageScale
                imgHeight = imgWidth / imgAspectRatio
            }
            val imgLeft = fLeft + (fWidth - imgWidth) / 2 + screenshotOffsetX * exportScaleFactor
            val imgTop = fTop + (fHeight - imgHeight) / 2 + screenshotOffsetY * exportScaleFactor
            
            canvas.drawBitmap(screenshot.asAndroidBitmap(), null, android.graphics.RectF(imgLeft, imgTop, imgLeft + imgWidth, imgTop + imgHeight), paint)
            canvas.restore()
        }

        // Frame Border
        paint.color = android.graphics.Color.BLACK
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = 4 * scaleFactor
        canvas.drawRoundRect(fLeft, fTop, fLeft + fWidth, fTop + fHeight, cornerRadius, cornerRadius, paint)

        // 3. Draw Camera Cutout / Notch
        when (deviceModel.cutoutType) {
            CutoutType.DYNAMIC_ISLAND -> {
                val islandWidthPx = 15 * pixelScale
                val islandHeightPx = 5 * pixelScale
                paint.style = android.graphics.Paint.Style.FILL
                paint.color = android.graphics.Color.BLACK
                canvas.drawRoundRect(
                    fLeft + (fWidth - islandWidthPx) / 2,
                    fTop + 6 * pixelScale,
                    fLeft + (fWidth + islandWidthPx) / 2,
                    fTop + 6 * pixelScale + islandHeightPx,
                    islandHeightPx / 2, islandHeightPx / 2, paint
                )
            }
            CutoutType.NOTCH -> {
                val notchWidthPx = fWidth * 0.45f
                val notchHeightPx = 8 * pixelScale
                paint.style = android.graphics.Paint.Style.FILL
                paint.color = android.graphics.Color.BLACK
                canvas.drawRect(
                    fLeft + (fWidth - notchWidthPx) / 2,
                    fTop,
                    fLeft + (fWidth + notchWidthPx) / 2,
                    fTop + notchHeightPx,
                    paint
                )
            }
            CutoutType.DOT -> {
                val dotDiameterPx = 4 * pixelScale
                paint.style = android.graphics.Paint.Style.FILL
                paint.color = android.graphics.Color.BLACK
                canvas.drawCircle(fLeft + fWidth / 2, fTop + 6 * pixelScale, dotDiameterPx / 2, paint)
            }
            CutoutType.LAPTOP_NOTCH -> {
                val notchWidthPx = fWidth * 0.12f
                val notchHeightPx = 4 * pixelScale
                paint.style = android.graphics.Paint.Style.FILL
                paint.color = android.graphics.Color.BLACK
                canvas.drawRect(
                    fLeft + (fWidth - notchWidthPx) / 2,
                    fTop,
                    fLeft + (fWidth + notchWidthPx) / 2,
                    fTop + notchHeightPx,
                    paint
                )
            }
            CutoutType.NONE -> {}
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
