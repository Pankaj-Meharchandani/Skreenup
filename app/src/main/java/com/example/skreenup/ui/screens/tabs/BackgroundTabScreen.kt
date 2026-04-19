package com.example.skreenup.ui.screens.tabs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.skreenup.ui.models.BackgroundType
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.ColorSelector
import com.example.skreenup.ui.components.SnappingSlider
import com.example.skreenup.ui.components.drawScrollbar

data class BackgroundPreset(val url: String, val category: String)

val PRESET_BACKGROUNDS = listOf(
    // Abstract / Artistic (The "Old" favorites)
    BackgroundPreset("https://images.unsplash.com/photo-1579546929518-9e396f3cc809?q=80&w=2070&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1964&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=2029&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=2070&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?q=80&w=1974&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1574169208507-84376144848b?q=80&w=2079&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1614850523296-d8c1af93d400?q=80&w=2070&auto=format&fit=crop", "Abstract"),
    BackgroundPreset("https://images.unsplash.com/photo-1550684847-75bdda21cc95?q=80&w=2070&auto=format&fit=crop", "Abstract"),

    // Clean / Minimalist (Less busy, great for readability)
    BackgroundPreset("https://images.unsplash.com/photo-1494438639946-1ebd1d20bf85?q=80&w=2067&auto=format&fit=crop", "Clean"),
    BackgroundPreset("https://images.unsplash.com/photo-1554034483-04fac7973891?q=80&w=2070&auto=format&fit=crop", "Clean"),
    BackgroundPreset("https://images.unsplash.com/photo-1516550893923-42d28e5677af?q=80&w=2072&auto=format&fit=crop", "Clean"),
    BackgroundPreset("https://images.unsplash.com/photo-1508615039623-a25605d2b022?q=80&w=2070&auto=format&fit=crop", "Clean"),
    BackgroundPreset("https://images.unsplash.com/photo-1528459801416-a9e53bbf4e17?q=80&w=2012&auto=format&fit=crop", "Clean"),
    BackgroundPreset("https://images.unsplash.com/photo-1519750783826-e2420f4d687f?q=80&w=1974&auto=format&fit=crop", "Clean"),
    BackgroundPreset("https://images.unsplash.com/photo-1553095066-5014bd75ad9d?q=80&w=2070&auto=format&fit=crop", "Clean"),

    // Tech / Professional
    BackgroundPreset("https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=2070&auto=format&fit=crop", "Tech"),
    BackgroundPreset("https://images.unsplash.com/photo-1550745165-9bc0b252726f?q=80&w=2070&auto=format&fit=crop", "Tech"),
    BackgroundPreset("https://images.unsplash.com/photo-1519389950473-47ba0277781c?q=80&w=2070&auto=format&fit=crop", "Tech"),
    BackgroundPreset("https://images.unsplash.com/photo-1485827404703-89b55fcc595e?q=80&w=2070&auto=format&fit=crop", "Tech"),
    
    // Nature / Scenic
    BackgroundPreset("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2073&auto=format&fit=crop", "Nature"),
    BackgroundPreset("https://images.unsplash.com/photo-1473116763249-2faaef81ccda?q=80&w=2069&auto=format&fit=crop", "Nature"),
    BackgroundPreset("https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=2071&auto=format&fit=crop", "Nature"),
    BackgroundPreset("https://images.unsplash.com/photo-1511497584788-876760111969?q=80&w=1932&auto=format&fit=crop", "Nature"),
    BackgroundPreset("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=2070&auto=format&fit=crop", "Nature"),
    
    // Fun / Colorful
    BackgroundPreset("https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=2025&auto=format&fit=crop", "Fun"),
    BackgroundPreset("https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=2070&auto=format&fit=crop", "Fun"),
    BackgroundPreset("https://images.unsplash.com/photo-1515488764276-beab7607c1e6?q=80&w=2070&auto=format&fit=crop", "Fun"),
    BackgroundPreset("https://images.unsplash.com/photo-1514525253344-ad1919420042?q=80&w=2070&auto=format&fit=crop", "Fun")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundTabScreen(viewModel: EditorViewModel) {
    val backgroundType by viewModel.backgroundType.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val gradientColors by viewModel.gradientColors.collectAsState()
    val backgroundImageOffsetX by viewModel.backgroundImageOffsetX.collectAsState()
    val backgroundImageOffsetY by viewModel.backgroundImageOffsetY.collectAsState()
    val backgroundImageScale by viewModel.backgroundImageScale.collectAsState()
    val backgroundImageBlur by viewModel.backgroundImageBlur.collectAsState()
    
    val hexColorSolid by viewModel.hexColorSolid.collectAsState()
    val hexColorStart by viewModel.hexColorGradientStart.collectAsState()
    val hexColorEnd by viewModel.hexColorGradientEnd.collectAsState()

    val scrollState = rememberScrollState()
    var showAllPresets by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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
                val context = LocalContext.current

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Presets", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(
                            text = "view all >",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showAllPresets = true }
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PRESET_BACKGROUNDS.take(8)) { preset ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(preset.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { viewModel.setPresetBackgroundImage(preset.url) },
                                contentScale = ContentScale.Crop,
                                error = coil.compose.rememberAsyncImagePainter(com.example.skreenup.R.drawable.placeholder)
                            )
                        }
                    }

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Background Blur", style = MaterialTheme.typography.labelMedium)
                            Text("${backgroundImageBlur.toInt()}px", style = MaterialTheme.typography.labelSmall)
                        }
                        SnappingSlider(
                            value = backgroundImageBlur,
                            onValueChange = { viewModel.setBackgroundImageBlur(it) },
                            valueRange = 0f..100f,
                            hintPoints = listOf(0f, 25f, 50f, 75f, 100f)
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

    if (showAllPresets) {
        ModalBottomSheet(
            onDismissRequest = { showAllPresets = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "All Background Presets",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
                ) {
                    items(PRESET_BACKGROUNDS) { preset ->
                        Box {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(preset.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        viewModel.setPresetBackgroundImage(preset.url)
                                        showAllPresets = false
                                    },
                                contentScale = ContentScale.Crop,
                                error = coil.compose.rememberAsyncImagePainter(com.example.skreenup.R.drawable.placeholder)
                            )
                            Text(
                                text = preset.category,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
