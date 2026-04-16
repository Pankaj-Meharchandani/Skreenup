package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.models.TextFont
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
                        label = { Text(font.label) },
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
