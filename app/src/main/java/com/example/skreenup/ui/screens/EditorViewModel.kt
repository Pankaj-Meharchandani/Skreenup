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

    // Adjust State
    private val _scale = MutableStateFlow(0.8f)
    val scale: StateFlow<Float> = _scale.asStateFlow()

    private val _imageScale = MutableStateFlow(1.0f)
    val imageScale: StateFlow<Float> = _imageScale.asStateFlow()

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

    // v2.1 Hex Color Input Strings
    private val _hexColorSolid = MutableStateFlow("#3F51B5")
    val hexColorSolid: StateFlow<String> = _hexColorSolid.asStateFlow()

    private val _hexColorGradientStart = MutableStateFlow("#3F51B5")
    val hexColorGradientStart: StateFlow<String> = _hexColorGradientStart.asStateFlow()

    private val _hexColorGradientEnd = MutableStateFlow("#006A6A")
    val hexColorGradientEnd: StateFlow<String> = _hexColorGradientEnd.asStateFlow()

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

    fun setBackgroundImage(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(getApplication(), uri)
            _backgroundImage.value = bitmap?.asImageBitmap()
        }
    }

    fun setScale(value: Float) {
        _scale.value = value
    }

    fun setImageScale(value: Float) {
        _imageScale.value = value
    }

    fun setAspectRatio(ratio: CompositionAspectRatio) {
        _aspectRatio.value = ratio
    }

    // v2.1 Offset Setters
    fun setFrameOffsetX(value: Float) { _frameOffsetX.value = value }
    fun setFrameOffsetY(value: Float) { _frameOffsetY.value = value }
    fun setScreenshotOffsetX(value: Float) { _screenshotOffsetX.value = value }
    fun setScreenshotOffsetY(value: Float) { _screenshotOffsetY.value = value }

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
