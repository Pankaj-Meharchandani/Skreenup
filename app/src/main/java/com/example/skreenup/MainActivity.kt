package com.example.skreenup

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.BookmarkAdd
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalContext
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
import com.example.skreenup.ui.screens.AboutScreen
import com.example.skreenup.ui.screens.tabs.AdjustTabScreen
import com.example.skreenup.ui.screens.tabs.BackgroundTabScreen
import com.example.skreenup.ui.screens.tabs.FrameTabScreen
import com.example.skreenup.ui.screens.tabs.TextTabScreen
import com.example.skreenup.ui.theme.SkreenupTheme
import com.example.skreenup.ui.components.DeviceFrame
import com.example.skreenup.ui.components.MockupRenderer.drawMockup
import com.example.skreenup.ui.components.UpdateDialog
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.update.GitHubRelease
import com.example.skreenup.update.UpdateChecker
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
    val context = LocalContext.current
    var showUpdateDialog by remember { mutableStateOf<GitHubRelease?>(null) }
    
    LaunchedEffect(Unit) {
        val checker = UpdateChecker()
        val latestRelease = withContext(Dispatchers.IO) {
            checker.checkForUpdate()
        }
        
        latestRelease?.let { release ->
            val currentVersion = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
            
            // Basic version comparison (tag_name vs versionName)
            // Assuming tag_name is like "1.0.2" and versionName is "1.0.1"
            if (release.tag_name != currentVersion) {
                showUpdateDialog = release
            }
        }
    }

    if (showUpdateDialog != null) {
        UpdateDialog(
            release = showUpdateDialog!!,
            onDismiss = { showUpdateDialog = null },
            onUpdate = { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
                showUpdateDialog = null
            }
        )
    }

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

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    onNavigateToAbout: () -> Unit,
    editorViewModel: EditorViewModel = viewModel()
) {
    val tabBackStack = rememberNavBackStack(FrameTab)
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    
    val isKeyboardVisible = WindowInsets.isImeVisible
    val previewWeight = if (isKeyboardVisible) 0.5f else 1f

    val selectedDevice by editorViewModel.selectedDevice.collectAsState()
    val screenshot by editorViewModel.screenshot.collectAsState()
    val backgroundType by editorViewModel.backgroundType.collectAsState()
    val backgroundColor by editorViewModel.backgroundColor.collectAsState()
    val gradientColors by editorViewModel.gradientColors.collectAsState()
    val backgroundImage by editorViewModel.backgroundImage.collectAsState()
    val backgroundImageOffsetX by editorViewModel.backgroundImageOffsetX.collectAsState()
    val backgroundImageOffsetY by editorViewModel.backgroundImageOffsetY.collectAsState()
    val backgroundImageScale by editorViewModel.backgroundImageScale.collectAsState()
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

    val heading by editorViewModel.heading.collectAsState()
    val subheading by editorViewModel.subheading.collectAsState()
    val headingFont by editorViewModel.headingFont.collectAsState()
    val subheadingFont by editorViewModel.subheadingFont.collectAsState()
    val headingSize by editorViewModel.headingSize.collectAsState()
    val subheadingSize by editorViewModel.subheadingSize.collectAsState()
    val textGap by editorViewModel.textGap.collectAsState()
    val textOffsetX by editorViewModel.textOffsetX.collectAsState()
    val textOffsetY by editorViewModel.textOffsetY.collectAsState()
    val textColor by editorViewModel.textColor.collectAsState()
    val textAlign by editorViewModel.textAlign.collectAsState()
    val headingBold by editorViewModel.headingBold.collectAsState()
    val subheadingBold by editorViewModel.subheadingBold.collectAsState()
    val showReflection by editorViewModel.showReflection.collectAsState()

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
                                backgroundImageOffsetX = backgroundImageOffsetX,
                                backgroundImageOffsetY = backgroundImageOffsetY,
                                backgroundImageScale = backgroundImageScale,
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
                                heading = heading,
                                subheading = subheading,
                                headingFont = headingFont,
                                subheadingFont = subheadingFont,
                                headingSize = headingSize,
                                subheadingSize = subheadingSize,
                                textGap = textGap,
                                textColor = textColor,
                                textOffsetX = textOffsetX,
                                textOffsetY = textOffsetY,
                                textAlignment = textAlign,
                                headingBold = headingBold,
                                subheadingBold = subheadingBold,
                                showReflection = showReflection
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
                    .weight(previewWeight)
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
                    backgroundImageOffsetX = backgroundImageOffsetX,
                    backgroundImageOffsetY = backgroundImageOffsetY,
                    backgroundImageScale = backgroundImageScale,
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
                    heading = heading,
                    subheading = subheading,
                    headingFont = headingFont,
                    subheadingFont = subheadingFont,
                    headingSize = headingSize,
                    subheadingSize = subheadingSize,
                    textGap = textGap,
                    textColor = textColor,
                    textOffsetX = textOffsetX,
                    textOffsetY = textOffsetY,
                    textAlign = textAlign,
                    headingBold = headingBold,
                    subheadingBold = subheadingBold,
                    showReflection = showReflection
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
    backgroundImageOffsetX: Float = 0f,
    backgroundImageOffsetY: Float = 0f,
    backgroundImageScale: Float = 1.0f,
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
    heading: String = "",
    subheading: String = "",
    headingFont: com.example.skreenup.ui.models.TextFont = com.example.skreenup.ui.models.TextFont.POPPINS,
    subheadingFont: com.example.skreenup.ui.models.TextFont = com.example.skreenup.ui.models.TextFont.POPPINS,
    headingSize: Float = 60f,
    subheadingSize: Float = 40f,
    textGap: Float = 20f,
    textColor: Color = Color.White,
    textOffsetX: Float = 0f,
    textOffsetY: Float = 0f,
    textAlignment: com.example.skreenup.ui.models.TextAlignLabel = com.example.skreenup.ui.models.TextAlignLabel.CENTER,
    headingBold: Boolean = true,
    subheadingBold: Boolean = false,
    showReflection: Boolean = true
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
                backgroundImageOffsetX = backgroundImageOffsetX,
                backgroundImageOffsetY = backgroundImageOffsetY,
                backgroundImageScale = backgroundImageScale,
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
                heading = heading,
                subheading = subheading,
                headingFont = headingFont,
                subheadingFont = subheadingFont,
                headingSize = headingSize,
                subheadingSize = subheadingSize,
                textGap = textGap,
                textColor = textColor,
                textOffsetX = textOffsetX,
                textOffsetY = textOffsetY,
                textAlignment = textAlignment,
                headingBold = headingBold,
                subheadingBold = subheadingBold,
                showReflection = showReflection
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
