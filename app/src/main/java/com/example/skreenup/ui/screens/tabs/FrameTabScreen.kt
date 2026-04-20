package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.models.DeviceCategory
import com.example.skreenup.ui.components.MockupRenderer.drawMockup
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.models.DeviceModels
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.drawScrollbar
import com.example.skreenup.ui.components.ColorSelector

import com.example.skreenup.ui.components.TabHeader

@Composable
fun FrameTabScreen(viewModel: EditorViewModel) {
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val showReflection by viewModel.showReflection.collectAsState()
    val screenBackgroundColor by viewModel.screenBackgroundColor.collectAsState()
    val hexColorScreen by viewModel.hexColorScreen.collectAsState()
    var selectedCategory by remember { mutableStateOf(selectedDevice.category) }
    val scrollState = rememberScrollState()

    val filteredDevices = DeviceModels.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .drawScrollbar(scrollState)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabHeader(
            title = "Device Frame",
            action = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Reflection", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = showReflection,
                        onCheckedChange = { viewModel.setShowReflection(it) },
                        modifier = Modifier.scale(0.8f),
                        thumbContent = if (showReflection) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        )

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DeviceCategory.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.label) },
                    shape = MaterialTheme.shapes.large
                )
            }
        }

        // Device Items
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredDevices) { device ->
                DeviceFrameItem(
                    device = device,
                    isSelected = selectedDevice.id == device.id,
                    onClick = { viewModel.selectDevice(device) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // Screen Color Section
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Screen Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            ColorSelector(
                selectedColor = screenBackgroundColor,
                onColorSelected = { viewModel.setScreenBackgroundColor(it) }
            )

            OutlinedTextField(
                value = hexColorScreen,
                onValueChange = { viewModel.setHexColorScreen(it) },
                label = { Text("Manual Hex Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Palette, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun DeviceFrameItem(
    device: DeviceModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High-fidelity mini preview using the shared MockupRenderer
        Box(
            modifier = Modifier
                .height(110.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawMockup(
                    screenshot = null,
                    deviceModel = device,
                    backgroundType = BackgroundType.SOLID,
                    backgroundColor = Color.Transparent,
                    gradientColors = emptyList(),
                    backgroundImage = null,
                    backgroundImageOffsetX = 0f,
                    backgroundImageOffsetY = 0f,
                    backgroundImageScale = 1.0f,
                    backgroundImageBlur = 0f,
                    scale = 0.85f,
                    imageScale = 1.0f,
                    frameOffsetX = 0f,
                    frameOffsetY = 0f,
                    screenshotOffsetX = 0f,
                    screenshotOffsetY = 0f,
                    aspectRatio = CompositionAspectRatio.SQUARE,
                    showWatermark = false,
                    watermarkText = "",
                    isExport = false,
                    rotationDegrees = 0f,
                    shadowIntensity = 0.5f,
                    shadowSoftness = 0.8f
                )
            }
        }

        Text(
            text = device.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}
