package com.example.skreenup.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import com.example.skreenup.data.PRESET_TEMPLATES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.skreenup.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: (Long?, Long?, String?) -> Unit,
    onNavigateToPresets: () -> Unit,
    onNavigateToYourTemplates: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val projects by viewModel.projects.collectAsState()
    val savedPresets by viewModel.presets.collectAsState()
    var presetToDelete by remember { mutableStateOf<com.example.skreenup.data.Preset?>(null) }
    var projectToDelete by remember { mutableStateOf<com.example.skreenup.data.Project?>(null) }

    if (presetToDelete != null) {
        AlertDialog(
            onDismissRequest = { presetToDelete = null },
            title = { Text("Delete Template") },
            text = { Text("Are you sure you want to delete '${presetToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        presetToDelete?.let { viewModel.deletePreset(it) }
                        presetToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { presetToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete History Item") },
            text = { Text("Are you sure you want to delete this project from your history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    AppScaffold(
        title = "Skreenup",
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
            }
        },
        settingsViewModel = settingsViewModel
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Create New at top
            item {
                HomeCard(
                    title = "Create New",
                    subtitle = "Start from scratch",
                    icon = Icons.Rounded.Add,
                    onClick = { onNavigateToEditor(null, null, null) }
                )
            }

            // 2. Preset Templates
            item {
                SectionHeader(title = "Preset Templates")
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(PRESET_TEMPLATES) { template ->
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val previewFile = remember(template.id) { 
                            java.io.File(context.filesDir, "preset_v11_${template.id}.png")
                        }
                        PresetPreviewCard(
                            onClick = { onNavigateToEditor(null, null, template.id) },
                            label = template.name,
                            previewUri = if (previewFile.exists()) previewFile.absolutePath else null
                        )
                    }
                }
            }

            // 3. Saved Templates
            if (savedPresets.isNotEmpty()) {
                item {
                    SectionHeader(title = "Saved Templates")
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(savedPresets, key = { it.id }) { preset ->
                            PresetPreviewCard(
                                onClick = { onNavigateToEditor(preset.id, null, null) },
                                onLongClick = { presetToDelete = preset },
                                label = preset.name,
                                previewUri = preset.previewUri,
                                icon = Icons.Rounded.Bookmark
                            )
                        }
                    }
                }
            }

            // 4. History
            item {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (projects.isEmpty()) {
                item {
                    Text(
                        "Your recent projects will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(projects, key = { it.id }) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onNavigateToEditor(null, project.id, null) },
                                onLongClick = { projectToDelete = project }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    ) {
                        ListItem(
                            headlineContent = { Text(project.name, color = MaterialTheme.colorScheme.onBackground) },
                            supportingContent = { Text("Modified: ${project.createdAt}", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            leadingContent = {
                                if (project.previewUri != null) {
                                    AsyncImage(
                                        model = project.previewUri,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Rounded.Smartphone, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Rounded.ChevronRight, 
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PresetPreviewCard(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    label: String,
    previewUri: String? = null,
    icon: ImageVector = Icons.Rounded.Layers
) {
    Card(
        modifier = Modifier
            .size(140.dp, 180.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (previewUri != null) {
                AsyncImage(
                    model = previewUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 40.dp) // Align with title text
            )
        }
    }
}
