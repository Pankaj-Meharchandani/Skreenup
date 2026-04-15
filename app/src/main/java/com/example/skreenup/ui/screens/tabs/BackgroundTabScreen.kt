package com.example.skreenup.ui.screens.tabs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.components.BackgroundType
import com.example.skreenup.ui.screens.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundTabScreen(viewModel: EditorViewModel) {
    val backgroundType by viewModel.backgroundType.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val gradientColors by viewModel.gradientColors.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { 
            viewModel.setBackgroundImage(it)
            viewModel.setBackgroundType(BackgroundType.IMAGE)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Background Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(BackgroundType.entries) { type ->
                FilterChip(
                    selected = backgroundType == type,
                    onClick = { viewModel.setBackgroundType(type) },
                    label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        when (backgroundType) {
            BackgroundType.SOLID -> {
                ColorSelector(
                    selectedColor = backgroundColor,
                    onColorSelected = { viewModel.setBackgroundColor(it) }
                )
            }
            BackgroundType.GRADIENT -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Gradient Colors", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorCircle(
                            color = gradientColors[0],
                            isSelected = true,
                            onClick = { /* Simple cycle for demo */
                                val newColors = listOf(getNextColor(gradientColors[0]), gradientColors[1])
                                viewModel.setGradientColors(newColors)
                            }
                        )
                        ColorCircle(
                            color = gradientColors[1],
                            isSelected = true,
                            onClick = { 
                                val newColors = listOf(gradientColors[0], getNextColor(gradientColors[1]))
                                viewModel.setGradientColors(newColors)
                            }
                        )
                    }
                }
            }
            BackgroundType.IMAGE -> {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Icon(Icons.Rounded.Image, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Choose Background Image")
                }
            }
            BackgroundType.BLUR -> {
                Text(
                    "Using your screenshot as a blurred background.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ColorSelector(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color(0xFF3F51B5), Color(0xFF006A6A), Color(0xFFBA1A1A), 
        Color(0xFF6750A4), Color(0xFF0061A4), Color(0xFF006E1C),
        Color(0xFF7D5260), Color(0xFF1B1B1F), Color(0xFFFEFBFF)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Select Color", style = MaterialTheme.typography.labelLarge)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
}

@Composable
fun ColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

fun getNextColor(current: Color): Color {
    val colors = listOf(
        Color(0xFF3F51B5), Color(0xFF006A6A), Color(0xFFBA1A1A), 
        Color(0xFF6750A4), Color(0xFF0061A4), Color(0xFF006E1C)
    )
    val index = colors.indexOf(current)
    return if (index == -1 || index == colors.size - 1) colors[0] else colors[index + 1]
}
