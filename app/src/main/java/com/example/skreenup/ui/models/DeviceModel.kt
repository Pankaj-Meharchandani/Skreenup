package com.example.skreenup.ui.models


enum class CutoutType {
    NONE,
    DYNAMIC_ISLAND,
    DOT,
    NOTCH,
    LAPTOP_NOTCH,
    SAFARI,
    CHROME
}

data class DeviceModel(
    val id: String,
    val name: String,
    val widthMm: Float,
    val heightMm: Float,
    val resWidth: Int,
    val resHeight: Int,
    val type: FrameType,
    val category: DeviceCategory,
    val cornerRadiusDp: Int = 32,
    val bezelWidthDp: Int = 4,
    val hasReflection: Boolean = true,
    val cutoutType: CutoutType = CutoutType.NONE,
    val hasChassis: Boolean = false
) {
    val aspectRatio: Float get() = resWidth.toFloat() / resHeight.toFloat()
}

val DeviceModels = listOf(
    // ── Phone ──
    DeviceModel(
        id = "iphone_17_pro_max",
        name = "iPhone 17 Pro Max",
        widthMm = 78.0f,
        heightMm = 163.4f,
        resWidth = 1320,
        resHeight = 2868,
        type = FrameType.IPHONE,
        category = DeviceCategory.PHONE,
        cornerRadiusDp = 44,
        bezelWidthDp = 3,
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
        category = DeviceCategory.PHONE,
        cornerRadiusDp = 44,
        bezelWidthDp = 4,
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
        category = DeviceCategory.PHONE,
        cornerRadiusDp = 40,
        bezelWidthDp = 8,
        hasReflection = false,
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
        category = DeviceCategory.PHONE,
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
        category = DeviceCategory.PHONE,
        cornerRadiusDp = 8,
        cutoutType = CutoutType.DOT
    ),

    // ── Tablet ──
    DeviceModel(
        id = "ipad_pro_13",
        name = "iPad Pro 13\"",
        widthMm = 214.9f,
        heightMm = 280.6f,
        resWidth = 2064,
        resHeight = 2752,
        type = FrameType.TABLET,
        category = DeviceCategory.TABLET,
        cornerRadiusDp = 18,
        bezelWidthDp = 6,
        cutoutType = CutoutType.NONE
    ),
    DeviceModel(
        id = "ipad_air",
        name = "iPad Air",
        widthMm = 178.5f,
        heightMm = 247.6f,
        resWidth = 1640,
        resHeight = 2360,
        type = FrameType.TABLET,
        category = DeviceCategory.TABLET,
        cornerRadiusDp = 18,
        bezelWidthDp = 6,
        cutoutType = CutoutType.NONE
    ),
    DeviceModel(
        id = "ipad_2025",
        name = "iPad 2025",
        widthMm = 179.5f,
        heightMm = 248.6f,
        resWidth = 2160,
        resHeight = 2960,
        type = FrameType.TABLET,
        category = DeviceCategory.TABLET,
        cornerRadiusDp = 18,
        bezelWidthDp = 6,
        cutoutType = CutoutType.NONE
    ),
    DeviceModel(
        id = "ipad_mini",
        name = "iPad Mini",
        widthMm = 134.8f,
        heightMm = 195.4f,
        resWidth = 1488,
        resHeight = 2266,
        type = FrameType.TABLET,
        category = DeviceCategory.TABLET,
        cornerRadiusDp = 18,
        bezelWidthDp = 6,
        cutoutType = CutoutType.NONE
    ),

    // ── Laptop ──
    DeviceModel(
        id = "macbook_pro",
        name = "MacBook Pro",
        widthMm = 312.6f,
        heightMm = 221.2f,
        resWidth = 3024,
        resHeight = 1964,
        type = FrameType.LAPTOP,
        category = DeviceCategory.LAPTOP,
        cornerRadiusDp = 12,
        bezelWidthDp = 6,
        hasReflection = false,
        cutoutType = CutoutType.LAPTOP_NOTCH,
        hasChassis = true
    ),

    // ── Web ──
    DeviceModel(
        id = "web_safari",
        name = "Safari",
        widthMm = 344.0f,
        heightMm = 210.0f,
        resWidth = 1920,
        resHeight = 1080,
        type = FrameType.DESKTOP,
        category = DeviceCategory.WEB,
        cornerRadiusDp = 10,
        bezelWidthDp = 2,
        hasReflection = false,
        cutoutType = CutoutType.SAFARI
    ),
    DeviceModel(
        id = "web_chrome",
        name = "Chrome",
        widthMm = 344.0f,
        heightMm = 210.0f,
        resWidth = 1920,
        resHeight = 1080,
        type = FrameType.DESKTOP,
        category = DeviceCategory.WEB,
        cornerRadiusDp = 8,
        bezelWidthDp = 2,
        hasReflection = false,
        cutoutType = CutoutType.CHROME
    ),
    DeviceModel(
        id = "web_desktop",
        name = "Web 1920",
        widthMm = 344.0f,
        heightMm = 194.0f,
        resWidth = 1920,
        resHeight = 1080,
        type = FrameType.DESKTOP,
        category = DeviceCategory.WEB,
        cornerRadiusDp = 8,
        bezelWidthDp = 2,
        hasReflection = false,
        cutoutType = CutoutType.NONE
    ),

    // ── PC ──
    DeviceModel(
        id = "pc_monitor",
        name = "Monitor 27\"",
        widthMm = 597.0f,
        heightMm = 336.0f,
        resWidth = 2560,
        resHeight = 1440,
        type = FrameType.DESKTOP,
        category = DeviceCategory.PC,
        cornerRadiusDp = 4,
        bezelWidthDp = 8,
        hasReflection = false,
        cutoutType = CutoutType.NONE,
        hasChassis = true
    )
)
