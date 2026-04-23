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
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.VerticalAlignCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.ui.models.TextFont
import com.example.skreenup.ui.models.TextAlignLabel
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.*

import com.example.skreenup.ui.components.TabHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTabScreen(viewModel: EditorViewModel) {
    val heading by viewModel.heading.collectAsState()
    val subheading by viewModel.subheading.collectAsState()
    val headingFont by viewModel.headingFont.collectAsState()
    val subheadingFont by viewModel.subheadingFont.collectAsState()
    val headingSize by viewModel.headingSize.collectAsState()
    val subheadingSize by viewModel.subheadingSize.collectAsState()
    val textGap by viewModel.textGap.collectAsState()
    val textColor by viewModel.textColor.collectAsState()
    val textAlign by viewModel.textAlign.collectAsState()
    val headingBold by viewModel.headingBold.collectAsState()
    val subheadingBold by viewModel.subheadingBold.collectAsState()
    val textShadow by viewModel.textShadow.collectAsState()
    val showWatermark by viewModel.showWatermark.collectAsState()
    val watermarkText by viewModel.watermarkText.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .drawScrollbar(scrollState)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TabHeader(
            title = "Text Overlay",
            onReset = { viewModel.resetTextTab() }
        )

        // Text Input Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionHeader(label = "Heading", icon = Icons.Rounded.TextFields)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = heading,
                    onValueChange = { viewModel.setHeading(it) },
                    placeholder = { Text("Heading text...") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = false,
                    maxLines = 5
                )
                IconToggleButton(checked = headingBold, onCheckedChange = { viewModel.setHeadingBold(it) }) {
                    Icon(Icons.Rounded.FormatBold, contentDescription = null, tint = if (headingBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }
            
            FontPicker(selectedFont = headingFont, onFontSelected = { viewModel.setHeadingFont(it) })

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

            SectionHeader(label = "Subheading", icon = Icons.Rounded.TextFields)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = subheading,
                    onValueChange = { viewModel.setSubheading(it) },
                    placeholder = { Text("Subheading text...") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    minLines = 1,
                    maxLines = 5
                )
                IconToggleButton(checked = subheadingBold, onCheckedChange = { viewModel.setSubheadingBold(it) }) {
                    Icon(Icons.Rounded.FormatBold, contentDescription = null, tint = if (subheadingBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }
            
            FontPicker(selectedFont = subheadingFont, onFontSelected = { viewModel.setSubheadingFont(it) })
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Alignment & Layout
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Alignment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Text Sizing & Gap
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AdjustmentItem(
                label = "Heading Size",
                value = headingSize,
                onValueChange = { viewModel.setHeadingSize(it) },
                valueRange = 10f..150f,
                icon = Icons.Rounded.FormatSize,
                showAsRaw = true
            )
            AdjustmentItem(
                label = "Subheading Size",
                value = subheadingSize,
                onValueChange = { viewModel.setSubheadingSize(it) },
                valueRange = 10f..120f,
                icon = Icons.Rounded.FormatSize,
                showAsRaw = true
            )
            AdjustmentItem(
                label = "Gap Between",
                value = textGap,
                onValueChange = { viewModel.setTextGap(it) },
                valueRange = 0f..100f,
                icon = Icons.Rounded.VerticalAlignCenter,
                showAsRaw = true
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Text Color
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Text Color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Shadow", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = textShadow,
                        onCheckedChange = { viewModel.setTextShadow(it) }
                    )
                }
            }
            ColorSelector(selectedColor = textColor, onColorSelected = { viewModel.setTextColor(it) })
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Watermark Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Watermark", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Switch(
                    checked = showWatermark,
                    onCheckedChange = { viewModel.setShowWatermark(it) }
                )
            }
            
            if (showWatermark) {
                OutlinedTextField(
                    value = watermarkText,
                    onValueChange = { viewModel.setWatermarkText(it) },
                    placeholder = { Text("Watermark text...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && watermarkText == "Made with Skreenup") {
                                viewModel.setWatermarkText("")
                            }
                        },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun FontPicker(selectedFont: TextFont, onFontSelected: (TextFont) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(TextFont.entries) { font ->
            FilterChip(
                selected = selectedFont == font,
                onClick = { onFontSelected(font) },
                label = { 
                    Text(
                        text = font.label,
                        fontFamily = when(font.family) {
                            "cursive" -> FontFamily.Cursive
                            "serif" -> FontFamily.Serif
                            "serif-monospace" -> FontFamily.Monospace
                            else -> FontFamily.SansSerif
                        },
                        fontSize = 12.sp
                    ) 
                },
                shape = MaterialTheme.shapes.large
            )
        }
    }
}
