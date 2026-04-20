package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.models.CompositionAspectRatio
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustTabScreen(viewModel: EditorViewModel) {
    val scale by viewModel.scale.collectAsState()
    val imageScale by viewModel.imageScale.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    
    val screenshotOffsetX by viewModel.screenshotOffsetX.collectAsState()
    val screenshotOffsetY by viewModel.screenshotOffsetY.collectAsState()
    val screenshotRotation by viewModel.screenshotRotation.collectAsState()
    val rotation by viewModel.rotation.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .drawScrollbar(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Main Scaling
        AdjustmentItem(
            label = "Mockup Scale",
            value = scale,
            onValueChange = { viewModel.setScale(it) },
            valueRange = 0.2f..1.0f,
            icon = Icons.Rounded.Fullscreen,
            hintPoints = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
        )

        // Image Scaling
        AdjustmentItem(
            label = "Screenshot Fit",
            value = imageScale,
            onValueChange = { viewModel.setImageScale(it) },
            valueRange = 0.0f..2.0f,
            icon = Icons.Rounded.AspectRatio,
            hintPoints = listOf(0f, 0.5f, 1f, 1.5f, 2f)
        )

        // Export Ratio
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AspectRatio, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(12.dp))
                Text(
                    text = "Export Canvas Ratio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CompositionAspectRatio.entries) { ratio ->
                    FilterChip(
                        selected = aspectRatio == ratio,
                        onClick = { viewModel.setAspectRatio(ratio) },
                        label = { Text(ratio.label) },
                        shape = MaterialTheme.shapes.large
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Rotations Section
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            SectionHeader(label = "Orientation Controls", icon = Icons.AutoMirrored.Rounded.RotateRight)

            // Frame Rotation
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Frame Rotation", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${rotation.toInt()}°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
                SnappingSlider(
                    value = rotation,
                    onValueChange = { viewModel.setRotation(it) },
                    valueRange = 0f..360f,
                    hintPoints = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f, 360f)
                )
            }

            // Image Rotation
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Image Rotation", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${screenshotRotation.toInt()}°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
                SnappingSlider(
                    value = screenshotRotation,
                    onValueChange = { viewModel.setScreenshotRotation(it) },
                    valueRange = -180f..180f,
                    hintPoints = listOf(-180f, -90f, 0f, 90f, 180f)
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Screenshot Offsets
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionHeader(label = "Screenshot Tuning", icon = Icons.Rounded.OpenWith)
            OffsetSlider(label = "Internal X", value = screenshotOffsetX, onValueChange = { viewModel.setScreenshotOffsetX(it) })
            OffsetSlider(label = "Internal Y", value = screenshotOffsetY, onValueChange = { viewModel.setScreenshotOffsetY(it) })
        }
        
        Spacer(Modifier.height(40.dp))
    }
}
