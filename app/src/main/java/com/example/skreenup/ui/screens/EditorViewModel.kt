package com.example.skreenup.ui.screens

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.models.DeviceModels
import com.example.skreenup.ui.models.TextFont
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.models.TextBackgroundStyle
import com.example.skreenup.ui.models.TextLayer
import kotlinx.coroutines.Dispatchers
import com.example.skreenup.data.Project
import com.example.skreenup.data.Preset
import com.example.skreenup.data.SkreenupDatabase
import com.example.skreenup.ui.models.EditorConfig
import com.example.skreenup.data.PRESET_TEMPLATES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import com.example.skreenup.data.SettingsManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SkreenupDatabase.getDatabase(application)
    private val presetDao = db.presetDao()
    private val projectDao = db.projectDao()
    private val settingsManager = SettingsManager.getInstance(application)

    private var initialConfig: EditorConfig? = null

    private val _selectedDevice = MutableStateFlow(DeviceModels.first())
    val selectedDevice: StateFlow<DeviceModel> = _selectedDevice.asStateFlow()

    private val _screenshot = MutableStateFlow<ImageBitmap?>(null)
    val screenshot: StateFlow<ImageBitmap?> = _screenshot.asStateFlow()

    private val _screenshotUri = MutableStateFlow<String?>(null)
    val screenshotUri: StateFlow<String?> = _screenshotUri.asStateFlow()

    // Background State
    private val _backgroundType = MutableStateFlow(BackgroundType.GRADIENT)
    val backgroundType: StateFlow<BackgroundType> = _backgroundType.asStateFlow()

    private val _backgroundColor = MutableStateFlow(Color(0xFF3F51B5))
    val backgroundColor: StateFlow<Color> = _backgroundColor.asStateFlow()

    private val _gradientColors = MutableStateFlow(listOf(Color(0xFF3F51B5), Color(0xFF006A6A)))
    val gradientColors: StateFlow<List<Color>> = _gradientColors.asStateFlow()

    private val _backgroundImage = MutableStateFlow<ImageBitmap?>(null)
    val backgroundImage: StateFlow<ImageBitmap?> = _backgroundImage.asStateFlow()

    private val _backgroundImageUri = MutableStateFlow<String?>(null)
    val backgroundImageUri: StateFlow<String?> = _backgroundImageUri.asStateFlow()

    private val _backgroundImageOffsetX = MutableStateFlow(0f)
    val backgroundImageOffsetX: StateFlow<Float> = _backgroundImageOffsetX.asStateFlow()

    private val _backgroundImageOffsetY = MutableStateFlow(0f)
    val backgroundImageOffsetY: StateFlow<Float> = _backgroundImageOffsetY.asStateFlow()

    private val _backgroundImageScale = MutableStateFlow(1.0f)
    val backgroundImageScale: StateFlow<Float> = _backgroundImageScale.asStateFlow()

    private val _backgroundImageBlur = MutableStateFlow(0f)
    val backgroundImageBlur: StateFlow<Float> = _backgroundImageBlur.asStateFlow()

    private val _screenBackgroundColor = MutableStateFlow(Color(0xFF2C2C2C))
    val screenBackgroundColor: StateFlow<Color> = _screenBackgroundColor.asStateFlow()

    // Text State
    private val _textLayers = MutableStateFlow<List<TextLayer>>(emptyList())
    val textLayers: StateFlow<List<TextLayer>> = _textLayers.asStateFlow()

    private val _selectedTextLayerId = MutableStateFlow<String?>(null)
    val selectedTextLayerId: StateFlow<String?> = _selectedTextLayerId.asStateFlow()

    private val _heading = MutableStateFlow("")
    val heading: StateFlow<String> = _heading.asStateFlow()

    private val _subheading = MutableStateFlow("")
    val subheading: StateFlow<String> = _subheading.asStateFlow()

    private val _headingFont = MutableStateFlow(TextFont.POPPINS)
    val headingFont: StateFlow<TextFont> = _headingFont.asStateFlow()

    private val _subheadingFont = MutableStateFlow(TextFont.POPPINS)
    val subheadingFont: StateFlow<TextFont> = _subheadingFont.asStateFlow()

    private val _headingSize = MutableStateFlow(60f)
    val headingSize: StateFlow<Float> = _headingSize.asStateFlow()

    private val _subheadingSize = MutableStateFlow(40f)
    val subheadingSize: StateFlow<Float> = _subheadingSize.asStateFlow()

    private val _textGap = MutableStateFlow(20f)
    val textGap: StateFlow<Float> = _textGap.asStateFlow()

    private val _textOffsetX = MutableStateFlow(0f)
    val textOffsetX: StateFlow<Float> = _textOffsetX.asStateFlow()

    private val _textOffsetY = MutableStateFlow(0f)
    val textOffsetY: StateFlow<Float> = _textOffsetY.asStateFlow()

    private val _textColor = MutableStateFlow(Color.White)
    val textColor: StateFlow<Color> = _textColor.asStateFlow()

    private val _textAlign = MutableStateFlow(TextAlignLabel.CENTER)
    val textAlign: StateFlow<TextAlignLabel> = _textAlign.asStateFlow()

    private val _textBackgroundStyle = MutableStateFlow(TextBackgroundStyle.NONE)
    val textBackgroundStyle: StateFlow<TextBackgroundStyle> = _textBackgroundStyle.asStateFlow()

    private val _textBackgroundColor = MutableStateFlow(Color.Black)
    val textBackgroundColor: StateFlow<Color> = _textBackgroundColor.asStateFlow()

    private val _textBackgroundAlpha = MutableStateFlow(0.5f)
    val textBackgroundAlpha: StateFlow<Float> = _textBackgroundAlpha.asStateFlow()

    private val _textBackgroundPadding = MutableStateFlow(24f)
    val textBackgroundPadding: StateFlow<Float> = _textBackgroundPadding.asStateFlow()

    private val _textBackgroundCornerRadius = MutableStateFlow(16f)
    val textBackgroundCornerRadius: StateFlow<Float> = _textBackgroundCornerRadius.asStateFlow()

    private val _headingBold = MutableStateFlow(true)
    val headingBold: StateFlow<Boolean> = _headingBold.asStateFlow()

    private val _subheadingBold = MutableStateFlow(false)
    val subheadingBold: StateFlow<Boolean> = _subheadingBold.asStateFlow()

    // Adjust State
    private val _scale = MutableStateFlow(0.8f)
    val scale: StateFlow<Float> = _scale.asStateFlow()

    private val _imageScale = MutableStateFlow(1.0f)
    val imageScale: StateFlow<Float> = _imageScale.asStateFlow()

    private val _screenshotRotation = MutableStateFlow(0f)
    val screenshotRotation: StateFlow<Float> = _screenshotRotation.asStateFlow()

    private val _aspectRatio = MutableStateFlow(CompositionAspectRatio.SQUARE)
    val aspectRatio: StateFlow<CompositionAspectRatio> = _aspectRatio.asStateFlow()

    private val _frameOffsetX = MutableStateFlow(0f)
    val frameOffsetX: StateFlow<Float> = _frameOffsetX.asStateFlow()

    private val _frameOffsetY = MutableStateFlow(0f)
    val frameOffsetY: StateFlow<Float> = _frameOffsetY.asStateFlow()

    private val _screenshotOffsetX = MutableStateFlow(0f)
    val screenshotOffsetX: StateFlow<Float> = _screenshotOffsetX.asStateFlow()

    private val _screenshotOffsetY = MutableStateFlow(0f)
    val screenshotOffsetY: StateFlow<Float> = _screenshotOffsetY.asStateFlow()

    // Rotation State
    private val _rotation = MutableStateFlow(0f)
    val rotation: StateFlow<Float> = _rotation.asStateFlow()

    private val _showReflection = MutableStateFlow(true)
    val showReflection: StateFlow<Boolean> = _showReflection.asStateFlow()

    private val _shadowIntensity = MutableStateFlow(0.3f)
    val shadowIntensity: StateFlow<Float> = _shadowIntensity.asStateFlow()

    private val _shadowSoftness = MutableStateFlow(1.0f)
    val shadowSoftness: StateFlow<Float> = _shadowSoftness.asStateFlow()

    private val _textShadow = MutableStateFlow(true)
    val textShadow: StateFlow<Boolean> = _textShadow.asStateFlow()

    private val _textZIndex = MutableStateFlow(1)
    val textZIndex: StateFlow<Int> = _textZIndex.asStateFlow()

    private val _showWatermark = MutableStateFlow(settingsManager.showWatermark.value)
    val showWatermark: StateFlow<Boolean> = _showWatermark.asStateFlow()

    private val _watermarkText = MutableStateFlow(settingsManager.customWatermark.value)
    val watermarkText: StateFlow<String> = _watermarkText.asStateFlow()

    private val _hexColorSolid = MutableStateFlow("#3F51B5")
    val hexColorSolid: StateFlow<String> = _hexColorSolid.asStateFlow()

    private val _hexColorGradientStart = MutableStateFlow("#3F51B5")
    val hexColorGradientStart: StateFlow<String> = _hexColorGradientStart.asStateFlow()

    private val _hexColorGradientEnd = MutableStateFlow("#006A6A")
    val hexColorGradientEnd: StateFlow<String> = _hexColorGradientEnd.asStateFlow()

    private val _hexColorScreen = MutableStateFlow("#2C2C2C")
    val hexColorScreen: StateFlow<String> = _hexColorScreen.asStateFlow()

    fun selectDevice(device: DeviceModel) {
        _selectedDevice.value = device
        _isSaved.value = false
    }

    fun setScreenshot(uri: Uri) {
        _screenshotUri.value = uri.toString()
        viewModelScope.launch {
            val bitmap = ImageLoaderHelper.loadBitmapFromUri(getApplication(), uri)
            _screenshot.value = bitmap?.asImageBitmap()
        }
    }

    fun setBackgroundType(type: BackgroundType) {
        _backgroundType.value = type
        _isSaved.value = false
    }

    fun setBackgroundColor(color: Color) {
        _backgroundColor.value = color
        _hexColorSolid.value = "#" + Integer.toHexString(color.toArgb()).uppercase().substring(2)
        _isSaved.value = false
    }

    fun setGradientColors(colors: List<Color>) {
        _gradientColors.value = colors
        _hexColorGradientStart.value = "#" + Integer.toHexString(colors[0].toArgb()).uppercase().substring(2)
        _hexColorGradientEnd.value = "#" + Integer.toHexString(colors[1].toArgb()).uppercase().substring(2)
        _isSaved.value = false
    }

    fun swapGradientColors() {
        val colors = _gradientColors.value
        if (colors.size >= 2) {
            setGradientColors(listOf(colors[1], colors[0]))
        }
    }

    fun setBackgroundImage(uri: Uri) {
        _backgroundImageUri.value = uri.toString()
        viewModelScope.launch {
            val bitmap = ImageLoaderHelper.loadBitmapFromUri(getApplication(), uri)
            _backgroundImage.value = bitmap?.asImageBitmap()
            _backgroundType.value = BackgroundType.IMAGE
        }
    }

    fun setBackgroundImageOffsetX(value: Float) {
        _backgroundImageOffsetX.value = value
    }

    fun setBackgroundImageOffsetY(value: Float) {
        _backgroundImageOffsetY.value = value
    }

    fun setBackgroundImageScale(value: Float) {
        _backgroundImageScale.value = value
    }

    fun setBackgroundImageBlur(value: Float) {
        _backgroundImageBlur.value = value
    }

    fun setPresetBackgroundImage(url: String) {
        _backgroundImageUri.value = url
        viewModelScope.launch {
            val bitmap = ImageLoaderHelper.loadBitmapFromUrl(getApplication(), url)
            _backgroundImage.value = bitmap?.asImageBitmap()
            _backgroundType.value = BackgroundType.IMAGE
        }
    }

    fun setScale(value: Float) {
        _scale.value = value
        _isSaved.value = false
    }

    fun setImageScale(value: Float) {
        _imageScale.value = value
        _isSaved.value = false
    }

    fun setScreenshotRotation(value: Float) {
        _screenshotRotation.value = value
        _isSaved.value = false
    }

    fun setAspectRatio(ratio: CompositionAspectRatio) {
        _aspectRatio.value = ratio
        _isSaved.value = false
    }

    fun setFrameOffsetX(value: Float) {
        _frameOffsetX.value = value
        _isSaved.value = false
    }

    fun setFrameOffsetY(value: Float) {
        _frameOffsetY.value = value
        _isSaved.value = false
    }

    fun setScreenshotOffsetX(value: Float) {
        _screenshotOffsetX.value = value
        _isSaved.value = false
    }

    fun setScreenshotOffsetY(value: Float) {
        _screenshotOffsetY.value = value
        _isSaved.value = false
    }

    // Rotation with snap
    fun setRotation(degrees: Float) {
        _rotation.value = degrees
        _isSaved.value = false
    }

    fun setHexColorSolid(value: String) {
        _hexColorSolid.value = value
        parseHexColor(value)?.let { _backgroundColor.value = it }
    }

    fun setHexColorGradientStart(value: String) {
        _hexColorGradientStart.value = value
        parseHexColor(value)?.let { start ->
            _gradientColors.value = listOf(start, _gradientColors.value[1])
        }
    }

    fun setHexColorGradientEnd(value: String) {
        _hexColorGradientEnd.value = value
        parseHexColor(value)?.let { end ->
            _gradientColors.value = listOf(_gradientColors.value[0], end)
        }
    }

    fun setScreenBackgroundColor(color: Color) {
        _screenBackgroundColor.value = color
        _hexColorScreen.value = "#" + Integer.toHexString(color.toArgb()).uppercase().substring(2)
    }

    fun setHexColorScreen(value: String) {
        _hexColorScreen.value = value
        parseHexColor(value)?.let { _screenBackgroundColor.value = it }
    }

    // Text Layer Management
    fun addTextLayer(layer: TextLayer = TextLayer()) {
        _textLayers.value = _textLayers.value + layer
        _selectedTextLayerId.value = layer.id
        _isSaved.value = false
    }

    fun removeTextLayer(id: String) {
        _textLayers.value = _textLayers.value.filter { it.id != id }
        if (_selectedTextLayerId.value == id) {
            _selectedTextLayerId.value = _textLayers.value.lastOrNull()?.id
        }
        _isSaved.value = false
    }

    fun selectTextLayer(id: String?) {
        _selectedTextLayerId.value = id
        val layer = _textLayers.value.find { it.id == id }
        if (layer != null) {
            _heading.value = layer.heading
            _subheading.value = layer.subheading
            _headingFont.value = try { TextFont.valueOf(layer.headingFont) } catch(e: Exception) { TextFont.POPPINS }
            _subheadingFont.value = try { TextFont.valueOf(layer.subheadingFont) } catch(e: Exception) { TextFont.POPPINS }
            _headingSize.value = layer.headingSize
            _subheadingSize.value = layer.subheadingSize
            _textGap.value = layer.textGap
            _textOffsetX.value = layer.offsetX
            _textOffsetY.value = layer.offsetY
            _textColor.value = Color(layer.textColor)
            _textAlign.value = try { TextAlignLabel.valueOf(layer.textAlign) } catch(e: Exception) { TextAlignLabel.CENTER }
            _textBackgroundStyle.value = try { TextBackgroundStyle.valueOf(layer.backgroundStyle) } catch(e: Exception) { TextBackgroundStyle.NONE }
            _textBackgroundColor.value = Color(layer.backgroundColor)
            _textBackgroundAlpha.value = layer.backgroundAlpha
            _textBackgroundPadding.value = layer.backgroundPadding
            _textBackgroundCornerRadius.value = layer.backgroundCornerRadius
            _headingBold.value = layer.headingBold
            _subheadingBold.value = layer.subheadingBold
            _textShadow.value = layer.textShadow
            _textZIndex.value = layer.zIndex
        }
    }

    fun updateSelectedTextLayer(update: (TextLayer) -> TextLayer) {
        val selectedId = _selectedTextLayerId.value ?: return
        _textLayers.value = _textLayers.value.map {
            if (it.id == selectedId) update(it) else it
        }
        _isSaved.value = false
    }

    // Text Setters
    fun setHeading(value: String) { 
        _heading.value = value 
        updateSelectedTextLayer { it.copy(heading = value) }
        _isSaved.value = false
    }
    fun setSubheading(value: String) { 
        _subheading.value = value 
        updateSelectedTextLayer { it.copy(subheading = value) }
        _isSaved.value = false
    }
    fun setHeadingFont(value: TextFont) { 
        _headingFont.value = value 
        updateSelectedTextLayer { it.copy(headingFont = value.name) }
    }
    fun setSubheadingFont(value: TextFont) { 
        _subheadingFont.value = value 
        updateSelectedTextLayer { it.copy(subheadingFont = value.name) }
    }
    fun setHeadingSize(value: Float) { 
        _headingSize.value = value 
        updateSelectedTextLayer { it.copy(headingSize = value) }
        _isSaved.value = false
    }
    fun setSubheadingSize(value: Float) { 
        _subheadingSize.value = value 
        updateSelectedTextLayer { it.copy(subheadingSize = value) }
        _isSaved.value = false
    }
    fun setTextGap(value: Float) { 
        _textGap.value = value 
        updateSelectedTextLayer { it.copy(textGap = value) }
        _isSaved.value = false
    }
    fun setTextOffsetX(value: Float) {
        _textOffsetX.value = value
        updateSelectedTextLayer { it.copy(offsetX = value) }
        _isSaved.value = false
    }
    fun setTextOffsetY(value: Float) {
        _textOffsetY.value = value
        updateSelectedTextLayer { it.copy(offsetY = value) }
        _isSaved.value = false
    }
    fun setTextColor(color: Color) { 
        _textColor.value = color 
        updateSelectedTextLayer { it.copy(textColor = color.toArgb()) }
        _isSaved.value = false
    }
    fun setTextAlign(value: TextAlignLabel) { 
        _textAlign.value = value 
        updateSelectedTextLayer { it.copy(textAlign = value.name) }
        _isSaved.value = false
    }

    fun setTextBackgroundStyle(value: TextBackgroundStyle) {
        _textBackgroundStyle.value = value
        updateSelectedTextLayer { it.copy(backgroundStyle = value.name) }
        _isSaved.value = false
    }

    fun setTextBackgroundColor(color: Color) {
        _textBackgroundColor.value = color
        updateSelectedTextLayer { it.copy(backgroundColor = color.toArgb()) }
        _isSaved.value = false
    }

    fun setTextBackgroundAlpha(value: Float) {
        _textBackgroundAlpha.value = value
        updateSelectedTextLayer { it.copy(backgroundAlpha = value) }
        _isSaved.value = false
    }

    fun setTextBackgroundPadding(value: Float) {
        _textBackgroundPadding.value = value
        updateSelectedTextLayer { it.copy(backgroundPadding = value) }
        _isSaved.value = false
    }

    fun setTextBackgroundCornerRadius(value: Float) {
        _textBackgroundCornerRadius.value = value
        updateSelectedTextLayer { it.copy(backgroundCornerRadius = value) }
        _isSaved.value = false
    }

    fun setHeadingBold(value: Boolean) { 
        _headingBold.value = value 
        updateSelectedTextLayer { it.copy(headingBold = value) }
        _isSaved.value = false
    }
    fun setSubheadingBold(value: Boolean) { 
        _subheadingBold.value = value 
        updateSelectedTextLayer { it.copy(subheadingBold = value) }
        _isSaved.value = false
    }

    fun setShowReflection(value: Boolean) { 
        _showReflection.value = value 
        _isSaved.value = false
    }

    fun setShadowIntensity(value: Float) { 
        _shadowIntensity.value = value 
        _isSaved.value = false
    }

    fun setShadowSoftness(value: Float) { 
        _shadowSoftness.value = value 
        _isSaved.value = false
    }

    fun setTextShadow(value: Boolean) { 
        _textShadow.value = value 
        updateSelectedTextLayer { it.copy(textShadow = value) }
        _isSaved.value = false
    }

    fun setTextZIndex(value: Int) { 
        _textZIndex.value = value 
        updateSelectedTextLayer { it.copy(zIndex = value) }
        _isSaved.value = false
    }

    fun setShowWatermark(value: Boolean) {
        _showWatermark.value = value
        _isSaved.value = false
    }

    fun setWatermarkText(value: String) {
        _watermarkText.value = value
        _isSaved.value = false
    }

    fun resetFrameTab() {
        initialConfig?.let { config ->
            _selectedDevice.value = DeviceModels.find { it.name == config.selectedDeviceName } ?: DeviceModels.first()
            _showReflection.value = config.showReflection
            setScreenBackgroundColor(Color(config.screenBackgroundColor))
        } ?: run {
            _selectedDevice.value = DeviceModels.first()
            _showReflection.value = true
            setScreenBackgroundColor(Color(0xFF2C2C2C))
        }
        _screenshot.value = null
        _screenshotUri.value = null
    }

    fun resetBackgroundTab() {
        initialConfig?.let { config ->
            _backgroundType.value = BackgroundType.valueOf(config.backgroundType)
            setBackgroundColor(Color(config.backgroundColor))
            setGradientColors(config.gradientColors.map { Color(it) })
            _backgroundImageOffsetX.value = config.backgroundImageOffsetX
            _backgroundImageOffsetY.value = config.backgroundImageOffsetY
            _backgroundImageScale.value = config.backgroundImageScale
            _backgroundImageBlur.value = config.backgroundImageBlur
        } ?: run {
            _backgroundType.value = BackgroundType.GRADIENT
            setBackgroundColor(Color(0xFF3F51B5))
            setGradientColors(listOf(Color(0xFF3F51B5), Color(0xFF006A6A)))
            _backgroundImageOffsetX.value = 0f
            _backgroundImageOffsetY.value = 0f
            _backgroundImageScale.value = 1.0f
            _backgroundImageBlur.value = 0f
        }
        _backgroundImage.value = null
        _backgroundImageUri.value = null
    }

    fun resetAdjustTab() {
        initialConfig?.let { config ->
            _scale.value = config.scale
            _imageScale.value = config.imageScale
            _screenshotRotation.value = config.screenshotRotation
            _aspectRatio.value = CompositionAspectRatio.valueOf(config.aspectRatio)
            _frameOffsetX.value = config.frameOffsetX
            _frameOffsetY.value = config.frameOffsetY
            _screenshotOffsetX.value = config.screenshotOffsetX
            _screenshotOffsetY.value = config.screenshotOffsetY
            _rotation.value = config.rotation
            _shadowIntensity.value = config.shadowIntensity
            _shadowSoftness.value = config.shadowSoftness
        } ?: run {
            _scale.value = 0.8f
            _imageScale.value = 1.0f
            _screenshotRotation.value = 0f
            _aspectRatio.value = CompositionAspectRatio.SQUARE
            _frameOffsetX.value = 0f
            _frameOffsetY.value = 0f
            _screenshotOffsetX.value = 0f
            _screenshotOffsetY.value = 0f
            _rotation.value = 0f
            _shadowIntensity.value = 0.3f
            _shadowSoftness.value = 1.0f
        }
    }

    fun resetTextTab() {
        initialConfig?.let { config ->
            _heading.value = config.heading
            _subheading.value = config.subheading
            _headingSize.value = config.headingSize
            _subheadingSize.value = config.subheadingSize
            _textGap.value = config.textGap
            _textOffsetX.value = config.textOffsetX
            _textOffsetY.value = config.textOffsetY
            _textColor.value = Color(config.textColor)
            _textAlign.value = TextAlignLabel.valueOf(config.textAlign)
            _textShadow.value = config.textShadow
            _headingFont.value = TextFont.valueOf(config.headingFont)
            _subheadingFont.value = TextFont.valueOf(config.subheadingFont)
            _headingBold.value = config.headingBold
            _subheadingBold.value = config.subheadingBold
            _textZIndex.value = config.textZIndex
            _showWatermark.value = if (config.watermarkText == "Made with Skreenup") settingsManager.showWatermark.value else config.showWatermark
            _watermarkText.value = if (config.watermarkText == "Made with Skreenup") settingsManager.customWatermark.value else config.watermarkText
        } ?: run {
            _heading.value = ""
            _subheading.value = ""
            _headingSize.value = 60f
            _subheadingSize.value = 40f
            _textGap.value = 20f
            _textOffsetX.value = 0f
            _textOffsetY.value = 0f
            _textColor.value = Color.White
            _textAlign.value = TextAlignLabel.CENTER
            _textShadow.value = true
            _headingFont.value = TextFont.POPPINS
            _subheadingFont.value = TextFont.POPPINS
            _headingBold.value = true
            _subheadingBold.value = false
            _textZIndex.value = 1
            _textBackgroundStyle.value = TextBackgroundStyle.NONE
            _textBackgroundColor.value = Color.Black
            _textBackgroundAlpha.value = 0.5f
            _textBackgroundPadding.value = 24f
            _textBackgroundCornerRadius.value = 16f
            _showWatermark.value = settingsManager.showWatermark.value
            _watermarkText.value = settingsManager.customWatermark.value
        }
    }

    fun resetAll() {
        resetFrameTab()
        resetBackgroundTab()
        resetAdjustTab()
        resetTextTab()
    }

    fun clearInitialConfig() {
        initialConfig = null
    }

    private val _isSaved = MutableStateFlow(true)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun loadPreset(id: Long) {
        viewModelScope.launch {
            presetDao.getAllPresets().first().find { it.id == id }?.let { p ->
                val config = Json.decodeFromString<EditorConfig>(p.configJson)
                initialConfig = config
                applyConfig(config)
                _isSaved.value = true
            }
        }
    }

    fun loadStaticTemplate(id: String) {
        PRESET_TEMPLATES.find { it.id == id }?.let { template ->
            initialConfig = template.config
            applyConfig(template.config)
            _isSaved.value = true
        }
    }

    // Better to use a direct query for single item
    fun loadProject(id: Long) {
        viewModelScope.launch {
            projectDao.getProjectById(id)?.let { p ->
                val config = Json.decodeFromString<EditorConfig>(p.configJson)
                initialConfig = config
                applyConfig(config)
                _isSaved.value = true
            }
        }
    }

    fun loadLastProject() {
        settingsManager.getLastEditorConfig()?.let { json ->
            try {
                val config = Json.decodeFromString<EditorConfig>(json)
                applyConfig(config)
                _isSaved.value = false // Last unsaved state
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveStateToPrefs() {
        if (settingsManager.continueLastProject.value) {
            val config = getCurrentConfig()
            settingsManager.saveLastEditorConfig(Json.encodeToString(config))
        }
    }

    private fun getCurrentConfig(): EditorConfig {
        return EditorConfig(
            selectedDeviceName = _selectedDevice.value.name,
            screenshotUri = _screenshotUri.value,
            backgroundType = _backgroundType.value.name,
            backgroundColor = _backgroundColor.value.toArgb(),
            gradientColors = _gradientColors.value.map { it.toArgb() },
            backgroundImageUri = _backgroundImageUri.value,
            backgroundImageOffsetX = _backgroundImageOffsetX.value,
            backgroundImageOffsetY = _backgroundImageOffsetY.value,
            backgroundImageScale = _backgroundImageScale.value,
            backgroundImageBlur = _backgroundImageBlur.value,
            screenBackgroundColor = _screenBackgroundColor.value.toArgb(),
            textLayers = _textLayers.value,
            // Legacy support
            heading = _heading.value,
            subheading = _subheading.value,
            headingFont = _headingFont.value.name,
            subheadingFont = _subheadingFont.value.name,
            headingSize = _headingSize.value,
            subheadingSize = _subheadingSize.value,
            textGap = _textGap.value,
            textOffsetX = _textOffsetX.value,
            textOffsetY = _textOffsetY.value,
            textColor = _textColor.value.toArgb(),
            textAlign = _textAlign.value.name,
            headingBold = _headingBold.value,
            subheadingBold = _subheadingBold.value,
            textShadow = _textShadow.value,
            textZIndex = _textZIndex.value,
            // End legacy
            scale = _scale.value,
            imageScale = _imageScale.value,
            screenshotRotation = _screenshotRotation.value,
            aspectRatio = _aspectRatio.value.name,
            frameOffsetX = _frameOffsetX.value,
            frameOffsetY = _frameOffsetY.value,
            screenshotOffsetX = _screenshotOffsetX.value,
            screenshotOffsetY = _screenshotOffsetY.value,
            rotation = _rotation.value,
            showReflection = _showReflection.value,
            shadowIntensity = _shadowIntensity.value,
            shadowSoftness = _shadowSoftness.value,
            showWatermark = _showWatermark.value,
            watermarkText = _watermarkText.value
        )
    }

    fun applyConfig(config: EditorConfig) {
        _selectedDevice.value = DeviceModels.find { it.name == config.selectedDeviceName } ?: DeviceModels.first()
        _backgroundType.value = BackgroundType.valueOf(config.backgroundType)
        _backgroundColor.value = Color(config.backgroundColor)
        _gradientColors.value = config.gradientColors.map { Color(it) }
        
        _screenshotUri.value = config.screenshotUri
        config.screenshotUri?.let { uriStr ->
            viewModelScope.launch {
                val bitmap = ImageLoaderHelper.loadBitmapFromUri(getApplication(), Uri.parse(uriStr))
                _screenshot.value = bitmap?.asImageBitmap()
            }
        }

        _backgroundImageUri.value = config.backgroundImageUri
        config.backgroundImageUri?.let { uriStr ->
            viewModelScope.launch {
                val bitmap = if (uriStr.startsWith("http")) {
                    ImageLoaderHelper.loadBitmapFromUrl(getApplication(), uriStr)
                } else {
                    ImageLoaderHelper.loadBitmapFromUri(getApplication(), Uri.parse(uriStr))
                }
                _backgroundImage.value = bitmap?.asImageBitmap()
            }
        }

        _backgroundImageOffsetX.value = config.backgroundImageOffsetX
        _backgroundImageOffsetY.value = config.backgroundImageOffsetY
        _backgroundImageScale.value = config.backgroundImageScale
        _backgroundImageBlur.value = config.backgroundImageBlur
        _screenBackgroundColor.value = Color(config.screenBackgroundColor)

        if (config.textLayers.isNotEmpty()) {
            _textLayers.value = config.textLayers
            config.textLayers.firstOrNull()?.let { selectTextLayer(it.id) }
        } else {
            // Migration from legacy
            val legacyLayer = TextLayer(
                heading = config.heading,
                subheading = config.subheading,
                headingFont = config.headingFont,
                subheadingFont = config.subheadingFont,
                headingSize = config.headingSize,
                subheadingSize = config.subheadingSize,
                textGap = config.textGap,
                offsetX = config.textOffsetX,
                offsetY = config.textOffsetY,
                textColor = if (config.textColor == -1) Color.White.toArgb() else config.textColor,
                textAlign = config.textAlign,
                headingBold = config.headingBold,
                subheadingBold = config.subheadingBold,
                textShadow = config.textShadow,
                zIndex = config.textZIndex
            )
            _textLayers.value = listOf(legacyLayer)
            selectTextLayer(legacyLayer.id)
        }

        _scale.value = config.scale
        _imageScale.value = config.imageScale
        _screenshotRotation.value = config.screenshotRotation
        _aspectRatio.value = CompositionAspectRatio.valueOf(config.aspectRatio)
        _frameOffsetX.value = config.frameOffsetX
        _frameOffsetY.value = config.frameOffsetY
        _screenshotOffsetX.value = config.screenshotOffsetX
        _screenshotOffsetY.value = config.screenshotOffsetY
        _rotation.value = config.rotation
        _showReflection.value = config.showReflection
        _shadowIntensity.value = config.shadowIntensity
        _shadowSoftness.value = config.shadowSoftness
        _textShadow.value = config.textShadow
        _textZIndex.value = config.textZIndex
        _showWatermark.value = if (config.watermarkText == "Made with Skreenup") settingsManager.showWatermark.value else config.showWatermark
        _watermarkText.value = if (config.watermarkText == "Made with Skreenup") settingsManager.customWatermark.value else config.watermarkText
    }

    fun saveTemplate(name: String = "My Template", previewUri: String? = null) {
        viewModelScope.launch {
            val currentConfig = getCurrentConfig()
            val templateConfig = currentConfig.copy(screenshotUri = null)
            val templateConfigJson = Json.encodeToString(templateConfig)
            val historyConfigJson = Json.encodeToString(currentConfig)
            
            // Save to Presets (Templates) without screenshot
            val preset = Preset(
                name = name,
                configJson = templateConfigJson,
                previewUri = previewUri
            )
            presetDao.insertPreset(preset)

            // Also Save to History (Projects) with screenshot
            val project = Project(
                name = name,
                configJson = historyConfigJson,
                previewUri = previewUri
            )
            projectDao.insertProject(project)

            _isSaved.value = true
        }
    }

    fun saveToHistory(previewUri: String? = null) {
        viewModelScope.launch {
            val config = getCurrentConfig()
            val project = Project(
                name = "Recent Project ${System.currentTimeMillis()}",
                configJson = Json.encodeToString(config),
                previewUri = previewUri
            )
            projectDao.insertProject(project)
        }
    }

    private fun parseHexColor(hex: String): Color? {
        return try {
            val h = if (hex.startsWith("#")) hex else "#$hex"
            Color(android.graphics.Color.parseColor(h))
        } catch (e: Exception) {
            null
        }
    }
}

object ImageLoaderHelper {
    private var singletonLoader: ImageLoader? = null

    private fun getLoader(context: android.content.Context): ImageLoader {
        return singletonLoader ?: synchronized(this) {
            singletonLoader ?: ImageLoader.Builder(context.applicationContext)
                .crossfade(true)
                .allowHardware(false)
                .build().also { singletonLoader = it }
        }
    }

    suspend fun loadBitmapFromUri(context: android.content.Context, uri: Uri): android.graphics.Bitmap? {
        return withContext(Dispatchers.IO) {
            val loader = getLoader(context)
            val request = ImageRequest.Builder(context)
                .data(uri)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
            } else {
                null
            }
        }
    }

    suspend fun loadBitmapFromUrl(context: android.content.Context, url: String): android.graphics.Bitmap? {
        return withContext(Dispatchers.IO) {
            val loader = getLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(1024, 1024) // Optimized size for backgrounds
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
            } else {
                null
            }
        }
    }
}
