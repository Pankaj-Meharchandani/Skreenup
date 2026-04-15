package com.example.skreenup.ui.screens

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import com.example.skreenup.ui.components.BackgroundType
import com.example.skreenup.ui.components.CompositionAspectRatio
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
    }

    fun setGradientColors(colors: List<Color>) {
        _gradientColors.value = colors
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
