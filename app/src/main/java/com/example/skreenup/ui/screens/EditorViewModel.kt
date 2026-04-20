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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedDevice = MutableStateFlow(DeviceModels.first())
    val selectedDevice: StateFlow<DeviceModel> = _selectedDevice.asStateFlow()

    private val _screenshot = MutableStateFlow<ImageBitmap?>(null)
    val screenshot: StateFlow<ImageBitmap?> = _screenshot.asStateFlow()

    // Background State
    private val _backgroundType = MutableStateFlow(BackgroundType.GRADIENT)
    val backgroundType: StateFlow<BackgroundType> = _backgroundType.asStateFlow()

    private val _backgroundColor = MutableStateFlow(Color(0xFF3F51B5))
    val backgroundColor: StateFlow<Color> = _backgroundColor.asStateFlow()

    private val _gradientColors = MutableStateFlow(listOf(Color(0xFF3F51B5), Color(0xFF006A6A)))
    val gradientColors: StateFlow<List<Color>> = _gradientColors.asStateFlow()

    private val _backgroundImage = MutableStateFlow<ImageBitmap?>(null)
    val backgroundImage: StateFlow<ImageBitmap?> = _backgroundImage.asStateFlow()

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

    // v2.1 Offsets
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

    // v2.2 New Styling Features
    private val _textShadow = MutableStateFlow(true)
    val textShadow: StateFlow<Boolean> = _textShadow.asStateFlow()

    // v2.1 Hex Color Input Strings
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
    }

    fun setScreenshot(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(getApplication(), uri)
            _screenshot.value = bitmap?.asImageBitmap()
        }
    }

    fun setBackgroundType(type: BackgroundType) {
        _backgroundType.value = type
    }

    fun setBackgroundColor(color: Color) {
        _backgroundColor.value = color
        _hexColorSolid.value = "#" + Integer.toHexString(color.toArgb()).uppercase().substring(2)
    }

    fun setGradientColors(colors: List<Color>) {
        _gradientColors.value = colors
        _hexColorGradientStart.value = "#" + Integer.toHexString(colors[0].toArgb()).uppercase().substring(2)
        _hexColorGradientEnd.value = "#" + Integer.toHexString(colors[1].toArgb()).uppercase().substring(2)
    }

    fun swapGradientColors() {
        val colors = _gradientColors.value
        if (colors.size >= 2) {
            setGradientColors(listOf(colors[1], colors[0]))
        }
    }

    fun setBackgroundImage(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(getApplication(), uri)
            _backgroundImage.value = bitmap?.asImageBitmap()
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
        viewModelScope.launch {
            val bitmap = loadBitmapFromUrl(getApplication(), url)
            _backgroundImage.value = bitmap?.asImageBitmap()
            _backgroundType.value = BackgroundType.IMAGE
        }
    }

    fun setScale(value: Float) {
        _scale.value = value
    }

    fun setImageScale(value: Float) {
        _imageScale.value = value
    }

    fun setScreenshotRotation(value: Float) {
        _screenshotRotation.value = value
    }

    fun setAspectRatio(ratio: CompositionAspectRatio) {
        _aspectRatio.value = ratio
    }

    // v2.1 Offset Setters
    fun setFrameOffsetX(value: Float) {
        _frameOffsetX.value = value
    }

    fun setFrameOffsetY(value: Float) {
        _frameOffsetY.value = value
    }

    fun setScreenshotOffsetX(value: Float) {
        _screenshotOffsetX.value = value
    }

    fun setScreenshotOffsetY(value: Float) {
        _screenshotOffsetY.value = value
    }

    // Rotation with snap
    fun setRotation(degrees: Float) {
        _rotation.value = degrees
    }

    // v2.1 Hex Color Setters
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

    // Text Setters
    fun setHeading(value: String) { _heading.value = value }
    fun setSubheading(value: String) { _subheading.value = value }
    fun setHeadingFont(value: TextFont) { _headingFont.value = value }
    fun setSubheadingFont(value: TextFont) { _subheadingFont.value = value }
    fun setHeadingSize(value: Float) { _headingSize.value = value }
    fun setSubheadingSize(value: Float) { _subheadingSize.value = value }
    fun setTextGap(value: Float) { _textGap.value = value }
    fun setTextOffsetX(value: Float) {
        _textOffsetX.value = value
    }
    fun setTextOffsetY(value: Float) {
        _textOffsetY.value = value
    }
    fun setTextColor(color: Color) { _textColor.value = color }
    fun setTextAlign(value: TextAlignLabel) { _textAlign.value = value }
    fun setHeadingBold(value: Boolean) { _headingBold.value = value }
    fun setSubheadingBold(value: Boolean) { _subheadingBold.value = value }

    fun setShowReflection(value: Boolean) { _showReflection.value = value }

    fun setTextShadow(value: Boolean) { _textShadow.value = value }

    fun resetAll() {
        _selectedDevice.value = DeviceModels.first()
        _backgroundType.value = BackgroundType.GRADIENT
        setBackgroundColor(Color(0xFF3F51B5))
        setGradientColors(listOf(Color(0xFF3F51B5), Color(0xFF006A6A)))
        _backgroundImage.value = null
        _backgroundImageOffsetX.value = 0f
        _backgroundImageOffsetY.value = 0f
        _backgroundImageScale.value = 1.0f
        _backgroundImageBlur.value = 0f
        setScreenBackgroundColor(Color(0xFF2C2C2C))
        
        _heading.value = ""
        _subheading.value = ""
        _headingSize.value = 60f
        _subheadingSize.value = 40f
        _textGap.value = 20f
        _textOffsetX.value = 0f
        _textOffsetY.value = 0f
        _textColor.value = Color.White
        _textAlign.value = TextAlignLabel.CENTER
        
        _scale.value = 0.8f
        _imageScale.value = 1.0f
        _screenshotRotation.value = 0f
        _aspectRatio.value = CompositionAspectRatio.SQUARE
        _frameOffsetX.value = 0f
        _frameOffsetY.value = 0f
        _screenshotOffsetX.value = 0f
        _screenshotOffsetY.value = 0f
        _rotation.value = 0f
        _showReflection.value = true
        _textShadow.value = true
    }

    private fun parseHexColor(hex: String): Color? {
        return try {
            val h = if (hex.startsWith("#")) hex else "#$hex"
            Color(android.graphics.Color.parseColor(h))
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
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

    private suspend fun loadBitmapFromUrl(context: android.content.Context, url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
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
}
