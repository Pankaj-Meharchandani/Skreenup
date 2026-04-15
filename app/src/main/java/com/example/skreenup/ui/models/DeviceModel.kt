package com.example.skreenup.ui.models

import com.example.skreenup.ui.models.FrameType

enum class CutoutType {
    NONE,
    DYNAMIC_ISLAND,
    DOT,
    NOTCH,
    LAPTOP_NOTCH
}

data class DeviceModel(
    val id: String,
    val name: String,
    val widthMm: Float,
    val heightMm: Float,
    val resWidth: Int,
    val resHeight: Int,
    val type: FrameType,
    val cornerRadiusDp: Int = 32,
    val cutoutType: CutoutType = CutoutType.NONE,
    val hasChassis: Boolean = false
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
        cornerRadiusDp = 44, // Refined to 44pt
        cutoutType = CutoutType.DYNAMIC_ISLAND
    ),
    DeviceModel(
        id = "iphone_17",
        name = "iPhone 17",
        widthMm = 77.6f,
        heightMm = 160.7f,
        resWidth = 1290,
        resHeight = 2796,
        type = FrameType.IPHONE,
        cornerRadiusDp = 44, // Refined to 44pt
        cutoutType = CutoutType.DYNAMIC_ISLAND
    ),
    DeviceModel(
        id = "iphone_x",
        name = "iPhone X",
        widthMm = 70.9f,
        heightMm = 143.6f,
        resWidth = 1125,
        resHeight = 2436,
        type = FrameType.IPHONE,
        cornerRadiusDp = 40,
        cutoutType = CutoutType.NOTCH
    ),
    DeviceModel(
        id = "galaxy_s26",
        name = "Galaxy S26",
        widthMm = 71.5f,
        heightMm = 149.4f,
        resWidth = 1080,
        resHeight = 2340,
        type = FrameType.ANDROID_PHONE,
        cornerRadiusDp = 28,
        cutoutType = CutoutType.DOT
    ),
    DeviceModel(
        id = "galaxy_s26_ultra",
        name = "Galaxy S26 Ultra",
        widthMm = 78.1f,
        heightMm = 163.6f,
        resWidth = 1440,
        resHeight = 3120,
        type = FrameType.ANDROID_PHONE,
        cornerRadiusDp = 8,
        cutoutType = CutoutType.DOT
    ),
    DeviceModel(
        id = "laptop_full",
        name = "Full Laptop",
        widthMm = 304.1f,
        heightMm = 212.4f,
        resWidth = 2560,
        resHeight = 1600,
        type = FrameType.DESKTOP,
        cornerRadiusDp = 16,
        cutoutType = CutoutType.LAPTOP_NOTCH,
        hasChassis = true
    )
)
