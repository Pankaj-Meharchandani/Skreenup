package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.skreenup.ui.models.DeviceModel
import com.example.skreenup.ui.models.DeviceModels
import com.example.skreenup.ui.screens.EditorViewModel

@Composable
fun FrameTabScreen(viewModel: EditorViewModel) {
    val selectedDevice by viewModel.selectedDevice.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Device Frame",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Minimalistic device representation
        Box(
            modifier = Modifier
                .height(100.dp)
                .aspectRatio(device.widthMm / device.heightMm)
                .background(Color.Black, RoundedCornerShape((device.cornerRadiusDp / 4).dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape((device.cornerRadiusDp / 4).dp))
        )

        Text(
            text = device.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}
