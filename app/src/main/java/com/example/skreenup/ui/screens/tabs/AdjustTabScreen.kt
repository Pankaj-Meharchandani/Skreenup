package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
    val aspectRatio by viewModel.aspectRatio.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scaling",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(scale * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = scale,
                onValueChange = { viewModel.setScale(it) },
                valueRange = 0.2f..1.0f,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
    }
}
