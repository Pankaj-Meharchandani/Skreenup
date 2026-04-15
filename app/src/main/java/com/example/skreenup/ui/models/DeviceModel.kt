package com.example.skreenup.ui.models

import com.example.skreenup.ui.components.FrameType

data class DeviceModel(
    val id: String,
    val name: String,
    val widthMm: Float,
    val heightMm: Float,
    val resWidth: Int,
    val resHeight: Int,
    val type: FrameType,
    val cornerRadiusDp: Int = 32
) {
    val aspectRatio: Float get() = resWidth.toFloat() / resHeight.toFloat()
}

val DeviceModels = listOf(
    DeviceModel(
        id = "iphone_17_pro_max",
        name = "iPhone 17 Pro Max",
        widthMm = 78.0f,
        heightMm = 163.4f,
        resWidth = 1320,
        resHeight = 2868,
        type = FrameType.IPHONE,
        cornerRadiusDp = 44
    ),
    DeviceModel(
        id = "iphone_17",
        name = "iPhone 17",
        widthMm = 77.6f,
        heightMm = 160.7f,
        resWidth = 1290,
        resHeight = 2796,
        type = FrameType.IPHONE,
        cornerRadiusDp = 40
    ),
    DeviceModel(
        id = "galaxy_s26",
        name = "Galaxy S26",
        widthMm = 71.5f,
        heightMm = 149.4f,
        resWidth = 1080,
        resHeight = 2340,
        type = FrameType.ANDROID_PHONE,
        cornerRadiusDp = 28
    ),
    DeviceModel(
        id = "galaxy_s26_plus",
        name = "Galaxy S26+",
        widthMm = 75.8f,
        heightMm = 158.4f,
        resWidth = 1440,
        resHeight = 3120,
        type = FrameType.ANDROID_PHONE,
        cornerRadiusDp = 28
    ),
    DeviceModel(
        id = "galaxy_s26_ultra",
        name = "Galaxy S26 Ultra",
        widthMm = 78.1f,
        heightMm = 163.6f,
        resWidth = 1440,
        resHeight = 3120,
        type = FrameType.ANDROID_PHONE,
        cornerRadiusDp = 8
    ),
    DeviceModel(
        id = "laptop_generic",
        name = "Modern Laptop",
        widthMm = 304.1f,
        heightMm = 212.4f,
        resWidth = 2560,
        resHeight = 1600,
        type = FrameType.DESKTOP,
        cornerRadiusDp = 12
    )
)
