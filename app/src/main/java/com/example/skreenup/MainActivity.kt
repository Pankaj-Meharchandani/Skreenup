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
import androidx.compose.material.icons.rounded.TextFields
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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
import com.example.skreenup.navigation.TextTab
import com.example.skreenup.navigation.SkreenupNavKey
import com.example.skreenup.navigation.SkreenupTabKey
import com.example.skreenup.ui.screens.AboutScreen
import com.example.skreenup.ui.screens.tabs.AdjustTabScreen
import com.example.skreenup.ui.screens.tabs.BackgroundTabScreen
import com.example.skreenup.ui.screens.tabs.FrameTabScreen
import com.example.skreenup.ui.screens.tabs.TextTabScreen
import com.example.skreenup.ui.theme.SkreenupTheme
import com.example.skreenup.ui.components.DeviceFrame
import com.example.skreenup.ui.components.MockupRenderer.drawMockup
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.CutoutType
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.models.FrameType
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
    val screenBackgroundColor by editorViewModel.screenBackgroundColor.collectAsState()
    val scale by editorViewModel.scale.collectAsState()
    val imageScale by editorViewModel.imageScale.collectAsState()
    val aspectRatio by editorViewModel.aspectRatio.collectAsState()
    
    val frameOffsetX by editorViewModel.frameOffsetX.collectAsState()
    val frameOffsetY by editorViewModel.frameOffsetY.collectAsState()
    val screenshotOffsetX by editorViewModel.screenshotOffsetX.collectAsState()
    val screenshotOffsetY by editorViewModel.screenshotOffsetY.collectAsState()
    val screenshotRotation by editorViewModel.screenshotRotation.collectAsState()
    val rotation by editorViewModel.rotation.collectAsState()

    val text by editorViewModel.text.collectAsState()
    val textFont by editorViewModel.textFont.collectAsState()
    val textSize by editorViewModel.textSize.collectAsState()
    val textOffsetX by editorViewModel.textOffsetX.collectAsState()
    val textOffsetY by editorViewModel.textOffsetY.collectAsState()
    val textColor by editorViewModel.textColor.collectAsState()

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
                                density = Density(context),
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
                                aspectRatio = aspectRatio,
                                rotationDegrees = rotation,
                                screenshotRotation = screenshotRotation,
                                screenBackgroundColor = screenBackgroundColor,
                                text = text,
                                textFont = textFont,
                                textSize = textSize,
                                textColor = textColor,
                                textOffsetX = textOffsetX,
                                textOffsetY = textOffsetY
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
                NavigationBarItem(
                    selected = currentTab == TextTab,
                    onClick = { 
                        if (currentTab != TextTab) {
                            tabBackStack.clear()
                            tabBackStack.add(TextTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.TextFields, contentDescription = "Text") },
                    label = { Text("Text") }
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
                    aspectRatio = aspectRatio,
                    rotationDegrees = rotation,
                    screenshotRotation = screenshotRotation,
                    screenBackgroundColor = screenBackgroundColor,
                    text = text,
                    textFont = textFont,
                    textSize = textSize,
                    textColor = textColor,
                    textOffsetX = textOffsetX,
                    textOffsetY = textOffsetY
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
                    entry<TextTab> {
                        TextTabScreen(editorViewModel)
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
    density: Density,
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
    rotationDegrees: Float = 0f,
    screenshotRotation: Float = 0f,
    screenBackgroundColor: Color = Color(0xFF2C2C2C),
    text: String = "",
    textFont: com.example.skreenup.ui.models.TextFont = com.example.skreenup.ui.models.TextFont.ROBOTO,
    textSize: Float = 48f,
    textColor: Color = Color.White,
    textOffsetX: Float = 0f,
    textOffsetY: Float = 0f
): Bitmap {
    return withContext(Dispatchers.Default) {
        val exportWidth = 2048
        val exportHeight = (exportWidth / aspectRatio.ratio).toInt()
        val bitmap = Bitmap.createBitmap(exportWidth, exportHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = androidx.compose.ui.graphics.Canvas(canvas),
            size = Size(exportWidth.toFloat(), exportHeight.toFloat())
        ) {
            drawMockup(
                screenshot = screenshot,
                deviceModel = deviceModel,
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
                aspectRatio = aspectRatio,
                showWatermark = false,
                watermarkText = "",
                isExport = true,
                rotationDegrees = rotationDegrees,
                screenshotRotation = screenshotRotation,
                screenBackgroundColor = screenBackgroundColor,
                text = text,
                textFont = textFont,
                textFontSize = textSize,
                textColor = textColor,
                textOffsetX = textOffsetX,
                textOffsetY = textOffsetY
            )
        }

        bitmap
    }
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
