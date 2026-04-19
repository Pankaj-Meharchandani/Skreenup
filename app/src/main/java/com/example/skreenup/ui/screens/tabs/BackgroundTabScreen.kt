package com.example.skreenup.ui.screens.tabs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.ColorSelector
import com.example.skreenup.ui.components.SnappingSlider
import com.example.skreenup.ui.components.drawScrollbar

@Composable
fun BackgroundTabScreen(viewModel: EditorViewModel) {
    val backgroundType by viewModel.backgroundType.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val gradientColors by viewModel.gradientColors.collectAsState()
    val backgroundImageOffsetX by viewModel.backgroundImageOffsetX.collectAsState()
    val backgroundImageOffsetY by viewModel.backgroundImageOffsetY.collectAsState()
    val backgroundImageScale by viewModel.backgroundImageScale.collectAsState()
    
    val hexColorSolid by viewModel.hexColorSolid.collectAsState()
    val hexColorStart by viewModel.hexColorGradientStart.collectAsState()
    val hexColorEnd by viewModel.hexColorGradientEnd.collectAsState()

    val scrollState = rememberScrollState()

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
            .verticalScroll(scrollState)
            .drawScrollbar(scrollState)
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
                    Text("Material Palette", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
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
                val presets = listOf(
                    "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?q=80&w=2070&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1964&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=2029&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=2070&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?q=80&w=1974&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1574169208507-84376144848b?q=80&w=2079&auto=format&fit=crop"
                )
                val context = LocalContext.current

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presets) { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { viewModel.setPresetBackgroundImage(url) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Background Scale", style = MaterialTheme.typography.labelMedium)
                            Text("${(backgroundImageScale * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                        }
                        SnappingSlider(
                            value = backgroundImageScale,
                            onValueChange = { viewModel.setBackgroundImageScale(it) },
                            valueRange = 0f..2f,
                            hintPoints = listOf(0f, 0.5f, 1f, 1.5f, 2f)
                        )
                    }

                    Column {
                        Text("Move Horizontal", style = MaterialTheme.typography.labelMedium)
                        SnappingSlider(
                            value = backgroundImageOffsetX,
                            onValueChange = { viewModel.setBackgroundImageOffsetX(it) },
                            valueRange = -500f..500f,
                            hintPoints = listOf(-500f, -250f, 0f, 250f, 500f)
                        )
                    }

                    Column {
                        Text("Move Vertical", style = MaterialTheme.typography.labelMedium)
                        SnappingSlider(
                            value = backgroundImageOffsetY,
                            onValueChange = { viewModel.setBackgroundImageOffsetY(it) },
                            valueRange = -500f..500f,
                            hintPoints = listOf(-500f, -250f, 0f, 250f, 500f)
                        )
                    }
                }
            }
            BackgroundType.TRANSPARENT -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Text(
                        "Background is now transparent. Export as PNG for best results.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(Modifier.height(24.dp))
    }
}
