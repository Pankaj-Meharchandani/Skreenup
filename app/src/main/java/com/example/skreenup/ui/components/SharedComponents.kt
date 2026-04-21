package com.example.skreenup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skreenup.ui.screens.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun GradientBackground() {
    val color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    Brush.verticalGradient(
                        0f to color,
                        1f to Color.Transparent
                    )
                )
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel(),
    content: @Composable (PaddingValues) -> Unit
) {
    val useGradient by settingsViewModel.useGradientBackground.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (useGradient) {
            GradientBackground()
        }
        
        Scaffold(
            containerColor = if (useGradient) Color.Transparent else MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                    navigationIcon = {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack, 
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    },
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            content(padding)
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TabHeader(
    title: String,
    onReset: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }
        
        if (action != null) {
            action()
        } else if (onReset != null) {
            Surface(
                onClick = onReset,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
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
    isDegrees: Boolean = false,
    showAsRaw: Boolean = false,
    hintPoints: List<Float> = listOf(
        valueRange.start,
        valueRange.start + (valueRange.endInclusive - valueRange.start) * 0.25f,
        valueRange.start + (valueRange.endInclusive - valueRange.start) * 0.5f,
        valueRange.start + (valueRange.endInclusive - valueRange.start) * 0.75f,
        valueRange.endInclusive
    )
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
            val displayValue = when {
                showAsRaw -> "${value.toInt()}"
                isDegrees -> "${value.toInt()}°"
                else -> "${(value * 100).toInt()}%"
            }
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        SnappingSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            hintPoints = hintPoints
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
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${value.toInt()}px", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
        SnappingSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -500f..500f,
            hintPoints = listOf(-500f, -250f, 0f, 250f, 500f)
        )
    }
}

@Composable
fun SnappingSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    snapThresholdPercent: Float = 0.03f, // 3% of range as requested (+-3)
    hintPoints: List<Float> = listOf(
        valueRange.start,
        valueRange.start + (valueRange.endInclusive - valueRange.start) * 0.25f,
        valueRange.start + (valueRange.endInclusive - valueRange.start) * 0.5f,
        valueRange.start + (valueRange.endInclusive - valueRange.start) * 0.75f,
        valueRange.endInclusive
    )
) {
    val totalRange = valueRange.endInclusive - valueRange.start
    val threshold = totalRange * snapThresholdPercent

    Box(
        modifier = Modifier.fillMaxWidth().height(44.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background Hint Dots
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp) // Align with Slider thumb track
        ) {
            val width = maxWidth
            hintPoints.forEach { point ->
                val fraction = if (totalRange != 0f) (point - valueRange.start) / totalRange else 0f
                Box(
                    modifier = Modifier
                        .offset(x = width * fraction - 3.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
            }
        }

        Slider(
            value = value,
            onValueChange = { newValue ->
                val closestSnap = hintPoints.minByOrNull { Math.abs(it - newValue) }
                val snappedValue = if (closestSnap != null && Math.abs(closestSnap - newValue) <= threshold) {
                    closestSnap
                } else {
                    newValue
                }
                onValueChange(snappedValue)
            },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ColorSelector(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color(0xFF3F51B5), Color(0xFF006A6A), Color(0xFFBA1A1A), 
        Color(0xFF6750A4), Color(0xFF0061A4), Color(0xFF006E1C),
        Color(0xFF7D5260), Color(0xFF1B1B1F), Color(0xFFFFFFFF),
        Color(0xFF000000), Color(0xFF2C2C2C), Color(0xFF424242)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(colors) { color ->
            ColorCircle(
                color = color,
                isSelected = selectedColor == color,
                onClick = { onColorSelected(color) }
            )
        }
    }
}

@Composable
fun ColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

fun Modifier.drawScrollbar(state: ScrollState): Modifier = drawWithContent {
    drawContent()
    if (state.maxValue > 0) {
        val viewPortHeight = size.height
        val totalHeight = state.maxValue + viewPortHeight
        val scrollbarHeight = (viewPortHeight / totalHeight) * viewPortHeight
        val scrollbarOffset = (state.value / totalHeight) * viewPortHeight

        drawRoundRect(
            color = Color.Gray.copy(alpha = 0.2f),
            topLeft = Offset(size.width - 6.dp.toPx(), scrollbarOffset),
            size = Size(4.dp.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
    }
}
