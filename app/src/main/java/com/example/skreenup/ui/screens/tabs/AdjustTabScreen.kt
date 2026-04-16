package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.ui.models.CompositionAspectRatio
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
    val screenshotRotation by viewModel.screenshotRotation.collectAsState()
    val rotation by viewModel.rotation.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Main Scaling
        AdjustmentItem(
            label = "Mockup Scale",
            value = scale,
            onValueChange = { viewModel.setScale(it) },
            valueRange = 0.2f..1.0f,
            icon = Icons.Rounded.Fullscreen
        )

        // Image Scaling
        AdjustmentItem(
            label = "Screenshot Fit",
            value = imageScale,
            onValueChange = { viewModel.setImageScale(it) },
            valueRange = 0.5f..1.5f,
            icon = Icons.Rounded.AspectRatio
        )

        // Screenshot Rotation
        AdjustmentItem(
            label = "Image Rotation",
            value = screenshotRotation,
            onValueChange = { viewModel.setScreenshotRotation(it) },
            valueRange = -180f..180f,
            icon = Icons.AutoMirrored.Rounded.RotateRight,
            isDegrees = true
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

        // Device Rotation
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Rounded.RotateRight, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(12.dp))
                    Text(
                        text = "Rotation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = "${rotation.toInt()}°",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = rotation,
                onValueChange = { viewModel.setRotation(it) },
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Frame Offsets
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionHeader(label = "Frame Positioning", icon = Icons.Rounded.OpenWith)
            OffsetSlider(label = "Horizontal (X)", value = frameOffsetX, onValueChange = { viewModel.setFrameOffsetX(it) })
            OffsetSlider(label = "Vertical (Y)", value = frameOffsetY, onValueChange = { viewModel.setFrameOffsetY(it) })
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

@Composable
fun SectionHeader(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.size(12.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun AdjustmentItem(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDegrees: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = if (isDegrees) "${value.toInt()}°" else "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (value == 0f) {
                    Spacer(Modifier.size(8.dp))
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                }
            }
            Text("${value.toInt()}px", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -500f..500f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
