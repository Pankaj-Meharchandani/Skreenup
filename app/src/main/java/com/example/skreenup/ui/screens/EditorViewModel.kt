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

    private val _screenBackgroundColor = MutableStateFlow(Color(0xFF2C2C2C))
    val screenBackgroundColor: StateFlow<Color> = _screenBackgroundColor.asStateFlow()

    // Text State
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _textFont = MutableStateFlow(TextFont.ROBOTO)
    val textFont: StateFlow<TextFont> = _textFont.asStateFlow()

    private val _textSize = MutableStateFlow(48f)
    val textSize: StateFlow<Float> = _textSize.asStateFlow()

    private val _textOffsetX = MutableStateFlow(0f)
    val textOffsetX: StateFlow<Float> = _textOffsetX.asStateFlow()

    private val _textOffsetY = MutableStateFlow(0f)
    val textOffsetY: StateFlow<Float> = _textOffsetY.asStateFlow()

    private val _textColor = MutableStateFlow(Color.White)
    val textColor: StateFlow<Color> = _textColor.asStateFlow()

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

    fun setScale(value: Float) {
        val mid = 0.6f // (0.2 + 1.0) / 2
        val snapThreshold = 0.02f
        _scale.value = if (kotlin.math.abs(value - mid) <= snapThreshold) mid else value
    }

    fun setImageScale(value: Float) {
        val snapAngles = listOf(0f, 0.5f, 1.0f, 1.5f, 2.0f)
        val snapThreshold = 0.04f
        val snapped = snapAngles.firstOrNull { kotlin.math.abs(value - it) <= snapThreshold }
        _imageScale.value = snapped ?: value
    }

    fun setScreenshotRotation(value: Float) {
        val mid = 0f // (-180 + 180) / 2
        val snapThreshold = 4f
        _screenshotRotation.value = if (kotlin.math.abs(value - mid) <= snapThreshold) mid else value
    }

    fun setAspectRatio(ratio: CompositionAspectRatio) {
        _aspectRatio.value = ratio
    }

    // v2.1 Offset Setters
    fun setFrameOffsetX(value: Float) {
        val snapThreshold = 2f
        _frameOffsetX.value = if (kotlin.math.abs(value) <= snapThreshold) 0f else value
    }

    fun setFrameOffsetY(value: Float) {
        val snapThreshold = 2f
        _frameOffsetY.value = if (kotlin.math.abs(value) <= snapThreshold) 0f else value
    }

    fun setScreenshotOffsetX(value: Float) {
        val snapThreshold = 2f
        _screenshotOffsetX.value = if (kotlin.math.abs(value) <= snapThreshold) 0f else value
    }

    fun setScreenshotOffsetY(value: Float) {
        val snapThreshold = 2f
        _screenshotOffsetY.value = if (kotlin.math.abs(value) <= snapThreshold) 0f else value
    }

    // Rotation with snap
    fun setRotation(degrees: Float) {
        val snapAngles = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f, 360f)
        val snapThreshold = 4f
        val snapped = snapAngles.firstOrNull { kotlin.math.abs(degrees - it) <= snapThreshold }
        _rotation.value = snapped ?: degrees
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
    fun setText(value: String) { _text.value = value }
    fun setTextFont(value: TextFont) { _textFont.value = value }
    fun setTextSize(value: Float) { _textSize.value = value }
    fun setTextOffsetX(value: Float) {
        val snapThreshold = 2f
        _textOffsetX.value = if (kotlin.math.abs(value) <= snapThreshold) 0f else value
    }
    fun setTextOffsetY(value: Float) {
        val snapThreshold = 2f
        _textOffsetY.value = if (kotlin.math.abs(value) <= snapThreshold) 0f else value
    }
    fun setTextColor(color: Color) { _textColor.value = color }

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
}
