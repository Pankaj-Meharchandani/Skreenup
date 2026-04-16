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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.ColorSelector

@Composable
fun BackgroundTabScreen(viewModel: EditorViewModel) {
    val backgroundType by viewModel.backgroundType.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val screenBackgroundColor by viewModel.screenBackgroundColor.collectAsState()
    val gradientColors by viewModel.gradientColors.collectAsState()
    
    val hexColorSolid by viewModel.hexColorSolid.collectAsState()
    val hexColorStart by viewModel.hexColorGradientStart.collectAsState()
    val hexColorEnd by viewModel.hexColorGradientEnd.collectAsState()

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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Background Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(BackgroundType.entries) { type ->
                FilterChip(
                    selected = backgroundType == type,
                    onClick = { viewModel.setBackgroundType(type) },
                    label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    shape = MaterialTheme.shapes.large
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

        when (backgroundType) {
            BackgroundType.SOLID -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ColorSelector(
                        selectedColor = backgroundColor,
                        onColorSelected = { viewModel.setBackgroundColor(it) }
                    )
                    
                    OutlinedTextField(
                        value = hexColorSolid,
                        onValueChange = { viewModel.setHexColorSolid(it) },
                        label = { Text("Manual Hex Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Rounded.Palette, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
            BackgroundType.GRADIENT -> {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Gradient Builder", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(gradientColors[0]))
                            OutlinedTextField(
                                value = hexColorStart,
                                onValueChange = { viewModel.setHexColorGradientStart(it) },
                                label = { Text("Start") },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = "Swap Colors",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.swapGradientColors() }
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(gradientColors[1]))
                            OutlinedTextField(
                                value = hexColorEnd,
                                onValueChange = { viewModel.setHexColorGradientEnd(it) },
                                label = { Text("End") },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )
                        }
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Rounded.Collections, contentDescription = null)
                    Spacer(Modifier.size(12.dp))
                    Text("Import Gallery Image")
                }
            }
            BackgroundType.BLUR -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Text(
                        "Your screenshot is being used as a beautiful blurred background.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // Screen Color Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Screen Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            
            ColorSelector(
                selectedColor = screenBackgroundColor,
                onColorSelected = { viewModel.setScreenBackgroundColor(it) }
            )
        }
        
        Spacer(Modifier.height(24.dp))
    }
}
