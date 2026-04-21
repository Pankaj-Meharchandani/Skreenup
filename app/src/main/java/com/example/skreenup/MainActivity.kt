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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.skreenup.data.PRESET_TEMPLATES
import com.example.skreenup.ui.models.DeviceModels
import com.example.skreenup.ui.models.TextFont
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.navigation.About
import com.example.skreenup.navigation.AdjustTab
import com.example.skreenup.navigation.BackgroundTab
import com.example.skreenup.navigation.Editor
import com.example.skreenup.navigation.FrameTab
import com.example.skreenup.navigation.History
import com.example.skreenup.navigation.Home
import com.example.skreenup.navigation.Presets
import com.example.skreenup.navigation.Settings
import com.example.skreenup.navigation.TextTab
import com.example.skreenup.navigation.YourTemplates
import com.example.skreenup.ui.screens.AboutScreen
import com.example.skreenup.ui.screens.HomeScreen
import com.example.skreenup.ui.screens.SettingsScreen
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
import java.io.File
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
    
    // Generate previews for preset templates if they don't exist
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            PRESET_TEMPLATES.forEach { template ->
                val file = File(context.filesDir, "preset_v2_${template.id}.png")
                if (!file.exists()) {
                    val device = DeviceModels.find { it.name == template.config.selectedDeviceName } ?: DeviceModels.first()
                    val bitmap = captureToBitmap(
                        density = Density(context),
                        screenshot = null,
                        deviceModel = device,
                        backgroundType = BackgroundType.valueOf(template.config.backgroundType),
                        backgroundColor = Color(template.config.backgroundColor),
                        gradientColors = template.config.gradientColors.map { Color(it) },
                        backgroundImage = null,
                        scale = template.config.scale,
                        imageScale = template.config.imageScale,
                        frameOffsetX = template.config.frameOffsetX,
                        frameOffsetY = template.config.frameOffsetY,
                        screenshotOffsetX = template.config.screenshotOffsetX,
                        screenshotOffsetY = template.config.screenshotOffsetY,
                        aspectRatio = CompositionAspectRatio.valueOf(template.config.aspectRatio),
                        rotationDegrees = template.config.rotation,
                        screenshotRotation = template.config.screenshotRotation,
                        screenBackgroundColor = Color(template.config.screenBackgroundColor),
                        heading = template.config.heading,
                        subheading = template.config.subheading,
                        headingFont = TextFont.valueOf(template.config.headingFont),
                        subheadingFont = TextFont.valueOf(template.config.subheadingFont),
                        headingSize = template.config.headingSize,
                        subheadingSize = template.config.subheadingSize,
                        textGap = template.config.textGap,
                        textColor = Color(template.config.textColor),
                        textOffsetX = template.config.textOffsetX,
                        textOffsetY = template.config.textOffsetY,
                        textAlignment = TextAlignLabel.valueOf(template.config.textAlign),
                        headingBold = template.config.headingBold,
                        subheadingBold = template.config.subheadingBold,
                        showReflection = template.config.showReflection,
                        showTextShadow = template.config.textShadow,
                        shadowIntensity = template.config.shadowIntensity,
                        shadowSoftness = template.config.shadowSoftness,
                        ignoreScreenshot = true
                    )
                    file.outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, it)
                    }
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        val checker = UpdateChecker()
        val latestRelease = withContext(Dispatchers.IO) {
            checker.checkForUpdate()
        }
        
        latestRelease?.let { release ->
            val currentVersion = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
            
            // Basic version comparison (tag_name vs versionName)
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

    // Check if we should start with a preset directly
    val startWithPreset = remember { 
        val intent = (context as? ComponentActivity)?.intent
        intent?.getBooleanExtra("START_WITH_PRESET", false) ?: false
    }

    val startDestination: NavKey = if (startWithPreset) Editor() else Home
    val mainBackStack: NavBackStack<NavKey> = rememberNavBackStack(startDestination)
    val mainBackStackList: MutableList<NavKey> = mainBackStack

    NavDisplay(
        backStack = mainBackStack,
        onBack = { 
            if (mainBackStackList.size > 1) {
                mainBackStackList.removeAt(mainBackStackList.size - 1)
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    onNavigateToEditor = { presetId, projectId, staticTemplateId -> 
                        mainBackStackList.add(Editor(presetId = presetId, projectId = projectId, staticTemplateId = staticTemplateId))
                    },
                    onNavigateToPresets = { /* mainBackStackList.add(Presets) */ },
                    onNavigateToYourTemplates = { /* mainBackStackList.add(YourTemplates) */ },
                    onNavigateToHistory = { /* mainBackStackList.add(History) */ },
                    onNavigateToSettings = { mainBackStackList.add(Settings) }
                )
            }
            entry<Settings> {
                SettingsScreen(
                    onNavigateToAbout = { mainBackStackList.add(About) },
                    onBack = { 
                        if (mainBackStackList.size > 1) {
                            mainBackStackList.removeAt(mainBackStackList.size - 1)
                        }
                    }
                )
            }
            entry<Presets> {
                // TODO: Implement PresetsScreen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Presets Screen (Coming Soon)")
                }
            }
            entry<History> {
                // TODO: Implement HistoryScreen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("History Screen (Coming Soon)")
                }
            }
            entry<YourTemplates> {
                // TODO: Implement YourTemplatesScreen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your Templates Screen (Coming Soon)")
                }
            }
            entry<Editor> { key ->
                EditorScreen(
                    onNavigateToAbout = { mainBackStackList.add(About) },
                    presetId = key.presetId,
                    projectId = key.projectId,
                    staticTemplateId = key.staticTemplateId
                )
            }
            entry<About> {
                AboutScreen(
                    onBack = { 
                        if (mainBackStackList.size > 1) {
                            mainBackStackList.removeAt(mainBackStackList.size - 1)
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
    presetId: Long? = null,
    projectId: Long? = null,
    staticTemplateId: String? = null,
    editorViewModel: EditorViewModel = viewModel()
) {
    val tabBackStack: NavBackStack<NavKey> = rememberNavBackStack(FrameTab as NavKey)
    val tabBackStackList: MutableList<NavKey> = tabBackStack
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(presetId, projectId, staticTemplateId) {
        if (presetId != null) {
            editorViewModel.loadPreset(presetId)
        } else if (projectId != null) {
            editorViewModel.loadProject(projectId)
        } else if (staticTemplateId != null) {
            editorViewModel.loadStaticTemplate(staticTemplateId)
        }
    }
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
    val backgroundImageBlur by editorViewModel.backgroundImageBlur.collectAsState()
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
    val textShadow by editorViewModel.textShadow.collectAsState()

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
                    val isSaved by editorViewModel.isSaved.collectAsState()
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
                                backgroundImageBlur = backgroundImageBlur,
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
                                showReflection = showReflection,
                                showTextShadow = textShadow,
                                shadowIntensity = editorViewModel.shadowIntensity.value,
                                shadowSoftness = editorViewModel.shadowSoftness.value,
                                ignoreScreenshot = true
                            )
                            val path = savePreviewToInternal(context, bitmap)
                            editorViewModel.saveTemplate(previewUri = path)
                            Toast.makeText(context, "Template Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Template"
                        )
                    }
                    IconButton(onClick = { editorViewModel.resetAll() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset All")
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
                                backgroundImageBlur = backgroundImageBlur,
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
                                showReflection = showReflection,
                                showTextShadow = textShadow,
                                shadowIntensity = editorViewModel.shadowIntensity.value,
                                shadowSoftness = editorViewModel.shadowSoftness.value,
                                ignoreScreenshot = false
                            )
                            val path = savePreviewToInternal(context, bitmap)
                            val success = saveBitmapToGallery(context, bitmap)
                            if (success) {
                                editorViewModel.saveToHistory(previewUri = path)
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
                val currentTab = tabBackStackList.lastOrNull()

                NavigationBarItem(
                    selected = currentTab == FrameTab,
                    onClick = { 
                        if (currentTab != FrameTab) {
                            tabBackStackList.clear()
                            tabBackStackList.add(FrameTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Smartphone, contentDescription = "Frame") },
                    label = { Text("Frame") }
                )
                NavigationBarItem(
                    selected = currentTab == BackgroundTab,
                    onClick = { 
                        if (currentTab != BackgroundTab) {
                            tabBackStackList.clear()
                            tabBackStackList.add(BackgroundTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Palette, contentDescription = "Background") },
                    label = { Text("Background") }
                )
                NavigationBarItem(
                    selected = currentTab == AdjustTab,
                    onClick = { 
                        if (currentTab != AdjustTab) {
                            tabBackStackList.clear()
                            tabBackStackList.add(AdjustTab)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Tune, contentDescription = "Adjust") },
                    label = { Text("Adjust") }
                )
                NavigationBarItem(
                    selected = currentTab == TextTab,
                    onClick = { 
                        if (currentTab != TextTab) {
                            tabBackStackList.clear()
                            tabBackStackList.add(TextTab)
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
                    backgroundImageBlur = backgroundImageBlur,
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
                    showReflection = showReflection,
                    showTextShadow = textShadow,
                    shadowIntensity = editorViewModel.shadowIntensity.collectAsState().value,
                    shadowSoftness = editorViewModel.shadowSoftness.collectAsState().value,
                    onScaleChange = { editorViewModel.setScale(it) },
                    onRotationChange = { editorViewModel.setRotation(it) },
                    onFrameOffsetChange = { x, y ->
                        editorViewModel.setFrameOffsetX(x)
                        editorViewModel.setFrameOffsetY(y)
                    },
                    onTextOffsetChange = { x, y ->
                        editorViewModel.setTextOffsetX(x)
                        editorViewModel.setTextOffsetY(y)
                    }
                )
            }

            // Bottom area: Tab Content
            NavDisplay(
                backStack = tabBackStack,
                onBack = { 
                    if (tabBackStackList.size > 1) {
                        tabBackStackList.removeAt(tabBackStackList.size - 1)
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
    backgroundImageBlur: Float = 0f,
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
    showReflection: Boolean = true,
    showTextShadow: Boolean = true,
    shadowIntensity: Float = 0.3f,
    shadowSoftness: Float = 1.0f,
    ignoreScreenshot: Boolean = false
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
                screenshot = if (ignoreScreenshot) null else screenshot,
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
                showReflection = showReflection,
                showTextShadow = showTextShadow,
                shadowIntensity = shadowIntensity,
                shadowSoftness = shadowSoftness
            )
        }

        bitmap
    }
}

suspend fun savePreviewToInternal(context: android.content.Context, bitmap: Bitmap): String {
    return withContext(Dispatchers.IO) {
        val filename = "preview_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, filename)
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, it)
        }
        file.absolutePath
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
