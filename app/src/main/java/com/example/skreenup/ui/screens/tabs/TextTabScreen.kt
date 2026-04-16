package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.FormatAlignRight
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.ui.models.TextFont
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTabScreen(viewModel: EditorViewModel) {
    val text by viewModel.text.collectAsState()
    val textFont by viewModel.textFont.collectAsState()
    val textSize by viewModel.textSize.collectAsState()
    val textOffsetX by viewModel.textOffsetX.collectAsState()
    val textOffsetY by viewModel.textOffsetY.collectAsState()
    val textColor by viewModel.textColor.collectAsState()
    val textAlign by viewModel.textAlign.collectAsState()
    val isBold by viewModel.isBold.collectAsState()
    val isItalic by viewModel.isItalic.collectAsState()
    val isUnderline by viewModel.isUnderline.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .drawScrollbar(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Text Input
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(label = "Custom Text", icon = Icons.Rounded.TextFields)
            OutlinedTextField(
                value = text,
                onValueChange = { viewModel.setText(it) },
                label = { Text("Enter multi-line text...") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                shape = MaterialTheme.shapes.medium
            )
            
            // Alignment & Style Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Alignment
                Row {
                    IconButton(onClick = { viewModel.setTextAlign(TextAlignLabel.LEFT) }) {
                        Icon(Icons.AutoMirrored.Rounded.FormatAlignLeft, contentDescription = null, tint = if (textAlign == TextAlignLabel.LEFT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { viewModel.setTextAlign(TextAlignLabel.CENTER) }) {
                        Icon(Icons.Rounded.FormatAlignCenter, contentDescription = null, tint = if (textAlign == TextAlignLabel.CENTER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { viewModel.setTextAlign(TextAlignLabel.RIGHT) }) {
                        Icon(Icons.AutoMirrored.Rounded.FormatAlignRight, contentDescription = null, tint = if (textAlign == TextAlignLabel.RIGHT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                }
                
                // Styles
                Row {
                    IconToggleButton(checked = isBold, onCheckedChange = { viewModel.setIsBold(it) }) {
                        Icon(Icons.Rounded.FormatBold, contentDescription = null, tint = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    IconToggleButton(checked = isItalic, onCheckedChange = { viewModel.setIsItalic(it) }) {
                        Icon(Icons.Rounded.FormatItalic, contentDescription = null, tint = if (isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    IconToggleButton(checked = isUnderline, onCheckedChange = { viewModel.setIsUnderline(it) }) {
                        Icon(Icons.Rounded.FormatUnderlined, contentDescription = null, tint = if (isUnderline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Font Selection
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Font Style",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TextFont.entries) { font ->
                    FilterChip(
                        selected = textFont == font,
                        onClick = { viewModel.setTextFont(font) },
                        label = { 
                            Text(
                                text = font.label,
                                fontFamily = when(font.family) {
                                    "cursive" -> FontFamily.Cursive
                                    "serif" -> FontFamily.Serif
                                    "serif-monospace" -> FontFamily.Monospace
                                    else -> FontFamily.SansSerif
                                },
                                fontWeight = if (font.family == "sans-serif-black") FontWeight.Black else FontWeight.Normal,
                                fontSize = 14.sp
                            ) 
                        },
                        shape = MaterialTheme.shapes.large
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Text Color
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Text Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            ColorSelector(
                selectedColor = textColor,
                onColorSelected = { viewModel.setTextColor(it) }
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Text Sizing
        AdjustmentItem(
            label = "Text Size",
            value = textSize,
            onValueChange = { viewModel.setTextSize(it) },
            valueRange = 10f..200f,
            icon = Icons.Rounded.FormatSize,
            showAsRaw = true,
            hintPoints = listOf(10f, 105f, 200f)
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Text Positioning
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionHeader(label = "Text Positioning", icon = Icons.Rounded.OpenWith)
            OffsetSlider(label = "Horizontal (X)", value = textOffsetX, onValueChange = { viewModel.setTextOffsetX(it) })
            OffsetSlider(label = "Vertical (Y)", value = textOffsetY, onValueChange = { viewModel.setTextOffsetY(it) })
        }

        Spacer(Modifier.height(40.dp))
    }
}
