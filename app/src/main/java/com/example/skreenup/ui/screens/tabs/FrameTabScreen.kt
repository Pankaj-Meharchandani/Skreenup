package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.components.MockupRenderer.drawMockup
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.models.DeviceModels
import com.example.skreenup.ui.screens.EditorViewModel

@Composable
fun FrameTabScreen(viewModel: EditorViewModel) {
    val selectedDevice by viewModel.selectedDevice.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Device Frame",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DeviceModels) { device ->
                DeviceFrameItem(
                    device = device,
                    isSelected = selectedDevice.id == device.id,
                    onClick = { viewModel.selectDevice(device) }
                )
            }
        }
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
                    scale = 0.85f,
                    imageScale = 1.0f,
                    frameOffsetX = 0f,
                    frameOffsetY = 0f,
                    screenshotOffsetX = 0f,
                    screenshotOffsetY = 0f,
                    aspectRatio = CompositionAspectRatio.SQUARE,
                    showWatermark = false,
                    watermarkText = "",
                    isExport = false
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
