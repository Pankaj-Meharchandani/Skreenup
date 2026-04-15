package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.components.CompositionAspectRatio
import com.example.skreenup.ui.screens.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustTabScreen(viewModel: EditorViewModel) {
    val scale by viewModel.scale.collectAsState()
    val imageScale by viewModel.imageScale.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    
    val frameOffsetX by viewModel.frameOffsetX.collectAsState()
    val frameOffsetY by viewModel.frameOffsetY.collectAsState()
    val screenshotOffsetX by viewModel.screenshotOffsetX.collectAsState()
    val screenshotOffsetY by viewModel.screenshotOffsetY.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Main Scaling
        AdjustmentItem(
            label = "Frame Scaling",
            value = scale,
            onValueChange = { viewModel.setScale(it) },
            valueRange = 0.2f..1.0f
        )

        // Image Scaling
        AdjustmentItem(
            label = "Image Scaling",
            value = imageScale,
            onValueChange = { viewModel.setImageScale(it) },
            valueRange = 0.5f..1.5f
        )

        // Export Ratio
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Export Ratio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CompositionAspectRatio.entries) { ratio ->
                    FilterChip(
                        selected = aspectRatio == ratio,
                        onClick = { viewModel.setAspectRatio(ratio) },
                        label = { Text(ratio.label) }
                    )
                }
            }
        }

        HorizontalDivider()

        // Frame Offsets
        Text("Frame Position", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OffsetSlider(label = "Offset X", value = frameOffsetX, onValueChange = { viewModel.setFrameOffsetX(it) })
        OffsetSlider(label = "Offset Y", value = frameOffsetY, onValueChange = { viewModel.setFrameOffsetY(it) })

        HorizontalDivider()

        // Screenshot Offsets
        Text("Screenshot Position", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OffsetSlider(label = "Offset X", value = screenshotOffsetX, onValueChange = { viewModel.setScreenshotOffsetX(it) })
        OffsetSlider(label = "Offset Y", value = screenshotOffsetY, onValueChange = { viewModel.setScreenshotOffsetY(it) })
    }
}

@Composable
fun AdjustmentItem(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OffsetSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("${value.toInt()}px", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -500f..500f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
