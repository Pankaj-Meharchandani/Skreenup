package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.ui.models.*
import com.example.skreenup.ui.screens.EditorViewModel
import com.example.skreenup.ui.components.*
import com.example.skreenup.data.PRESET_TEXT_TEMPLATES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlaysTabScreen(viewModel: EditorViewModel) {
    val overlays by viewModel.overlayLayers.collectAsState()
    val selectedId by viewModel.selectedOverlayId.collectAsState()
    val activeTab by viewModel.activeOverlayTab.collectAsState()
    
    // Text State
    val heading by viewModel.heading.collectAsState()
    val subheading by viewModel.subheading.collectAsState()
    val headingFont by viewModel.headingFont.collectAsState()
    val subheadingFont by viewModel.subheadingFont.collectAsState()
    val headingSize by viewModel.headingSize.collectAsState()
    val subheadingSize by viewModel.subheadingSize.collectAsState()
    val textGap by viewModel.textGap.collectAsState()
    val textAlign by viewModel.textAlign.collectAsState()
    val headingBold by viewModel.headingBold.collectAsState()
    val subheadingBold by viewModel.subheadingBold.collectAsState()
    
    // Decoration State
    val selectedShape by viewModel.selectedShape.collectAsState()
    val thickness by viewModel.thickness.collectAsState()
    val cornerRadius by viewModel.cornerRadius.collectAsState()
    val arrowHeadSize by viewModel.arrowHeadSize.collectAsState()
    val curvature by viewModel.curvature.collectAsState()
    
    // Shared State
    val offsetX by viewModel.textOffsetX.collectAsState()
    val offsetY by viewModel.textOffsetY.collectAsState()
    val scale by viewModel.overlayScale.collectAsState()
    val rotation by viewModel.overlayRotation.collectAsState()
    val color by viewModel.textColor.collectAsState()
    val alpha by viewModel.overlayAlpha.collectAsState()
    
    val textShadow by viewModel.textShadow.collectAsState()
    val backgroundStyle by viewModel.textBackgroundStyle.collectAsState()
    val backgroundAlpha by viewModel.textBackgroundAlpha.collectAsState()
    val backgroundPadding by viewModel.textBackgroundPadding.collectAsState()
    val backgroundCornerRadius by viewModel.textBackgroundCornerRadius.collectAsState()

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
            title = "Overlays",
            onReset = { viewModel.resetTextTab() }
        )

        // 1. Top Selector
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = activeTab == EditorViewModel.OverlayTab.TEXT,
                onClick = { viewModel.setOverlayTab(EditorViewModel.OverlayTab.TEXT) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = { Icon(Icons.Rounded.TextFields, null) },
                label = { Text("Text") }
            )
            SegmentedButton(
                selected = activeTab == EditorViewModel.OverlayTab.DECORATIONS,
                onClick = { viewModel.setOverlayTab(EditorViewModel.OverlayTab.DECORATIONS) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = { Icon(Icons.Rounded.AutoAwesome, null) },
                label = { Text("Decorations") }
            )
        }

        // 2. Tab Content
        if (activeTab == EditorViewModel.OverlayTab.TEXT) {
            TextTabContent(viewModel)
        } else {
            DecorationsTabContent(viewModel, overlays)
        }

        // 3. Layer Selector
        if (overlays.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Your Overlays", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(overlays) { layer ->
                        FilterChip(
                            selected = selectedId == layer.id,
                            onClick = { viewModel.selectOverlay(layer.id) },
                            label = { 
                                val chipText = when (layer.type) {
                                    OverlayType.TEXT -> layer.heading.ifEmpty { "Text Layer" }
                                    OverlayType.STICKER -> {
                                        val nameParts = layer.stickerResName?.split("-") ?: emptyList()
                                        val logoIndex = nameParts.indexOfFirst { it == "logo" || it == "icon" }
                                        val significantParts = if (logoIndex > 0) nameParts.take(logoIndex) else nameParts.take(1)
                                        significantParts.joinToString(" ") { it.replaceFirstChar { it.uppercase() } }.ifEmpty { "Sticker" }
                                    }
                                    else -> {
                                        layer.shape.name.lowercase()
                                            .split("_")
                                            .joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
                                    }
                                }
                                Text(
                                    text = chipText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.removeOverlay(layer.id) },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun TextTabContent(viewModel: EditorViewModel) {
    val heading by viewModel.heading.collectAsState()
    val subheading by viewModel.subheading.collectAsState()
    val headingFont by viewModel.headingFont.collectAsState()
    val subheadingFont by viewModel.subheadingFont.collectAsState()
    val headingBold by viewModel.headingBold.collectAsState()
    val subheadingBold by viewModel.subheadingBold.collectAsState()
    val headingSize by viewModel.headingSize.collectAsState()
    val subheadingSize by viewModel.subheadingSize.collectAsState()
    val textGap by viewModel.textGap.collectAsState()
    val selectedId by viewModel.selectedOverlayId.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(label = "Templates", icon = Icons.Rounded.TextFields)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(PRESET_TEXT_TEMPLATES) { template ->
                OutlinedCard(
                    onClick = { viewModel.addOverlay(template.layer.copy(id = java.util.UUID.randomUUID().toString())) },
                    modifier = Modifier.width(140.dp)
                ) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(template.layer.heading.ifBlank { "Aa" }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (selectedId != null) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionHeader(label = "Edit Text", icon = Icons.Rounded.Edit)
            
            OutlinedTextField(
                value = heading,
                onValueChange = { viewModel.setHeading(it) },
                label = { Text("Heading") },
                modifier = Modifier.fillMaxWidth()
            )
            FontPicker(selectedFont = headingFont, onFontSelected = { viewModel.setHeadingFont(it) })
            
            OutlinedTextField(
                value = subheading,
                onValueChange = { viewModel.setSubheading(it) },
                label = { Text("Subheading") },
                modifier = Modifier.fillMaxWidth()
            )
            FontPicker(selectedFont = subheadingFont, onFontSelected = { viewModel.setSubheadingFont(it) })
            
            AdjustmentItem(
                label = "Heading Size",
                value = headingSize,
                onValueChange = { viewModel.setHeadingSize(it) },
                valueRange = 10f..300f,
                showAsRaw = true,
                icon = Icons.Rounded.Title
            )
            
            AdjustmentItem(
                label = "Subheading Size",
                value = subheadingSize,
                onValueChange = { viewModel.setSubheadingSize(it) },
                valueRange = 8f..200f,
                showAsRaw = true,
                icon = Icons.Rounded.Subtitles
            )
            
            AdjustmentItem(
                label = "Line Gap",
                value = textGap,
                onValueChange = { viewModel.setTextGap(it) },
                valueRange = 0f..200f,
                showAsRaw = true,
                icon = Icons.Rounded.VerticalAlignBottom
            )

            val textAlignment by viewModel.textAlignment.collectAsState()
            SectionHeader(label = "Alignment", icon = Icons.Rounded.FormatAlignCenter)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TextAlignLabel.entries.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = textAlignment == label,
                        onClick = { viewModel.setTextAlignment(label) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = TextAlignLabel.entries.size),
                        icon = {
                            Icon(
                                when (label) {
                                    TextAlignLabel.LEFT -> Icons.AutoMirrored.Rounded.FormatAlignLeft
                                    TextAlignLabel.CENTER -> Icons.Rounded.FormatAlignCenter
                                    TextAlignLabel.RIGHT -> Icons.AutoMirrored.Rounded.FormatAlignRight
                                },
                                null
                            )
                        },
                        label = { Text(label.label) }
                    )
                }
            }

            // Shared controls (Color, Opacity, etc. can be added here or in a generic section)
            CommonOverlayControls(viewModel)
        } else {
            Button(
                onClick = { viewModel.addOverlay(OverlayLayer(type = OverlayType.TEXT, heading = "New Text")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add New Text")
            }
        }
    }
}

@Composable
fun DecorationsTabContent(viewModel: EditorViewModel, overlays: List<OverlayLayer>) {
    val selectedId by viewModel.selectedOverlayId.collectAsState()
    val selectedShape by viewModel.selectedShape.collectAsState()
    val thickness by viewModel.thickness.collectAsState()
    val cornerRadius by viewModel.cornerRadius.collectAsState()
    val curvature by viewModel.curvature.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(label = "Shapes & Arrows", icon = Icons.Rounded.Category)
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(DecorationShape.entries) { shape ->
                val type = when (shape) {
                    DecorationShape.ARROW -> OverlayType.ARROW
                    DecorationShape.CHAT_BUBBLE -> OverlayType.BUBBLE
                    else -> OverlayType.SHAPE
                }
                
                OutlinedCard(
                    onClick = { viewModel.addOverlay(OverlayLayer(type = type, shape = shape, thickness = 4f)) },
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (selectedShape == shape && selectedId != null && overlays.find { it.id == selectedId }?.type != OverlayType.STICKER) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(getShapeIcon(shape), null, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }

        SectionHeader(label = "Stickers", icon = Icons.Rounded.StickyNote2)
        
        val stickerLogos = listOf(
            "figma-1-logo-svgrepo-com",
            "shopify-logo-svgrepo-com",
            "apple-11-logo-svgrepo-com",
            "reddit-1-logo-svgrepo-com",
            "snapchat-logo-svgrepo-com",
            "telegram-logo-svgrepo-com",
            "spotify-1-logo-svgrepo-com",
            "gmail-icon-logo-svgrepo-com",
            "youtube-icon-logo-svgrepo-com",
            "facebook-icon-logo-svgrepo-com",
            "github-icon-1-logo-svgrepo-com",
            "instagram-2-1-logo-svgrepo-com",
            "instagram-2016-logo-svgrepo-com",
            "slack-new-logo-logo-svgrepo-com",
            "dribbble-icon-1-logo-svgrepo-com",
            "linkedin-icon-2-logo-svgrepo-com",
            "google-play-badge-logo-svgrepo-com",
            "tiktok-icon-white-1-logo-svgrepo-com",
            "google-pay-primary-logo-logo-svgrepo-com",
            "download-on-the-app-store-apple-logo-svgrepo-com"
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stickerLogos) { stickerName ->
                val isSelected = overlays.find { it.id == selectedId }?.stickerResName == stickerName
                
                OutlinedCard(
                    onClick = { viewModel.addOverlay(OverlayLayer(type = OverlayType.STICKER, stickerResName = stickerName)) },
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val context = LocalContext.current
                        val resId = context.resources.getIdentifier(stickerName.replace("-", "_"), "raw", context.packageName)
                        if (resId != 0) {
                            AsyncImage(
                                model = resId,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        } else {
                            Icon(Icons.Rounded.Image, null, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }

        if (selectedId != null) {
            val selectedOverlay = overlays.find { it.id == selectedId }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionHeader(label = "Edit Decoration", icon = Icons.Rounded.Tune)
            
            if (selectedOverlay?.type == OverlayType.STICKER) {
                // Stickers only show common controls like Scale, Rotation, Opacity
                CommonOverlayControls(viewModel, showColorPicker = false)
            } else {
                if (selectedOverlay?.type == OverlayType.ARROW) {
                    AdjustmentItem(label = "Curvature", value = curvature, onValueChange = { viewModel.setDecorationCurvature(it) }, valueRange = -100f..100f, showAsRaw = true, icon = Icons.Rounded.Redo)
                }
                
                if (selectedOverlay?.type == OverlayType.SHAPE || selectedOverlay?.type == OverlayType.BUBBLE) {
                    AdjustmentItem(label = "Corner Radius", value = cornerRadius, onValueChange = { viewModel.setDecorationCornerRadius(it) }, valueRange = 0f..100f, showAsRaw = true, icon = Icons.Rounded.RoundedCorner)
                }

                AdjustmentItem(
                    label = "Thickness",
                    value = thickness,
                    onValueChange = { viewModel.setDecorationThickness(it) },
                    valueRange = 1f..50f,
                    showAsRaw = true,
                    icon = Icons.Rounded.LineWeight,
                    hintPoints = listOf(1f, 4f, 10f, 25f, 50f)
                )
                
                CommonOverlayControls(viewModel)
            }
        } else {
            Text("Tap a shape or sticker to add it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CommonOverlayControls(viewModel: EditorViewModel, showColorPicker: Boolean = true) {
    val scale by viewModel.overlayScale.collectAsState()
    val rotation by viewModel.overlayRotation.collectAsState()
    val color by viewModel.textColor.collectAsState()
    val alpha by viewModel.overlayAlpha.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (showColorPicker) {
            ColorPickerButton(color = color, tag = "text_color", label = "Color")
        }
        
        AdjustmentItem(label = "Opacity", value = alpha, onValueChange = { viewModel.setOverlayAlpha(it) }, valueRange = 0f..1f, icon = Icons.Rounded.Opacity)
        
        AdjustmentItem(label = "Scale", value = scale, onValueChange = { viewModel.setOverlayScale(it) }, valueRange = 0.1f..5f, showAsRaw = true, icon = Icons.Rounded.Scale)
        
        AdjustmentItem(label = "Rotation", value = rotation, onValueChange = { viewModel.setOverlayRotation(it) }, valueRange = 0f..360f, showAsRaw = true, icon = Icons.AutoMirrored.Rounded.RotateRight)
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

fun getShapeIcon(shape: DecorationShape): androidx.compose.ui.graphics.vector.ImageVector {
    return when (shape) {
        DecorationShape.SQUARE -> Icons.Rounded.Square
        DecorationShape.CIRCLE -> Icons.Rounded.Circle
        DecorationShape.RECTANGLE -> Icons.Rounded.Rectangle
        DecorationShape.TRIANGLE -> Icons.Rounded.ChangeHistory
        DecorationShape.STAR -> Icons.Rounded.Star
        DecorationShape.PENTAGON -> Icons.Rounded.Pentagon
        DecorationShape.HEXAGON -> Icons.Rounded.Hexagon
        DecorationShape.HEART -> Icons.Rounded.Favorite
        DecorationShape.CHAT_BUBBLE -> Icons.Rounded.ChatBubble
        DecorationShape.ARROW -> Icons.Rounded.TrendingFlat
    }
}

// Extension to migrate old presets (Removed as no longer needed after TextTemplates update)
