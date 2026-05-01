package com.example.skreenup.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skreenup.data.AppTheme
import com.example.skreenup.data.ExportAction
import com.example.skreenup.ui.components.AppScaffold
import com.example.skreenup.ui.screens.UpdateState

import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val theme by settingsViewModel.theme.collectAsState()
    val useGradientBackground by settingsViewModel.useGradientBackground.collectAsState()
    val continueLastProject by settingsViewModel.continueLastProject.collectAsState()
    val useHaptics by settingsViewModel.useHaptics.collectAsState()
    val defaultExportAction by settingsViewModel.defaultExportAction.collectAsState()
    val showWatermark by settingsViewModel.showWatermark.collectAsState()
    val customWatermark by settingsViewModel.customWatermark.collectAsState()
    val updateState by settingsViewModel.updateState.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showExportActionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateState.UpToDate -> {
                Toast.makeText(context, "App is up to date", Toast.LENGTH_SHORT).show()
                settingsViewModel.resetUpdateState()
            }
            is UpdateState.Error -> {
                Toast.makeText(context, "Failed to check for updates", Toast.LENGTH_SHORT).show()
                settingsViewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    AppScaffold(
        title = "Settings",
        onBack = onBack,
        settingsViewModel = settingsViewModel
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("App Theme") },
                supportingContent = { 
                    Text(when(theme) {
                        AppTheme.LIGHT -> "Light"
                        AppTheme.DARK -> "Dark"
                        AppTheme.SYSTEM -> "System Default"
                    })
                },
                leadingContent = { Icon(Icons.Rounded.DarkMode, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { showThemeDialog = true }
            )

            ListItem(
                headlineContent = { Text("App Gradient Background") },
                supportingContent = { Text("Apply gradient background to app") },
                leadingContent = { Icon(Icons.Rounded.ColorLens, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                trailingContent = {
                    Switch(
                        checked = useGradientBackground,
                        onCheckedChange = { settingsViewModel.setUseGradientBackground(it) }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Continue Last Project") },
                supportingContent = { Text("Open editor from where you left off") },
                leadingContent = { Icon(Icons.Rounded.History, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                trailingContent = {
                    Switch(
                        checked = continueLastProject,
                        onCheckedChange = { settingsViewModel.setContinueLastProject(it) }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Haptic Feedback") },
                supportingContent = { Text("Vibrate on snapping and interactions") },
                leadingContent = { Icon(Icons.Rounded.Vibration, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                trailingContent = {
                    Switch(
                        checked = useHaptics,
                        onCheckedChange = { settingsViewModel.setUseHaptics(it) }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Default Export Action") },
                supportingContent = {
                    Text(when(defaultExportAction) {
                        ExportAction.ASK -> "Ask every time"
                        ExportAction.SAVE -> "Save to gallery"
                        ExportAction.SHARE -> "Share as image"
                        ExportAction.CLIPBOARD -> "Copy to clipboard"
                    })
                },
                leadingContent = { Icon(Icons.Rounded.Save, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { showExportActionDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Watermark",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Show Watermark") },
                supportingContent = { Text("Add watermark to your designs") },
                leadingContent = { Icon(Icons.Rounded.TextFields, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                trailingContent = {
                    Switch(
                        checked = showWatermark,
                        onCheckedChange = { settingsViewModel.setShowWatermark(it) }
                    )
                }
            )

            if (showWatermark) {
                ListItem(
                    headlineContent = {
                        OutlinedTextField(
                            value = customWatermark,
                            onValueChange = { settingsViewModel.setCustomWatermark(it) },
                            placeholder = { Text("Made with Skreenup") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("Custom Watermark Text") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "System",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Check for Updates") },
                supportingContent = { 
                    if (updateState is UpdateState.Checking) {
                        Text("Checking...")
                    } else {
                        val currentVersion = context.packageManager
                            .getPackageInfo(context.packageName, 0).versionName
                        Text("Current version: $currentVersion")
                    }
                },
                leadingContent = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable(
                    enabled = updateState !is UpdateState.Checking
                ) { 
                    settingsViewModel.checkForUpdates() 
                },
                trailingContent = {
                    if (updateState is UpdateState.Checking) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            )

            ListItem(
                headlineContent = { Text("Clear History") },
                supportingContent = { Text("Delete all recent projects") },
                leadingContent = { Icon(Icons.Rounded.DeleteSweep, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { settingsViewModel.clearHistory() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("About Skreenup") },
                leadingContent = { Icon(Icons.Rounded.Info, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable(onClick = onNavigateToAbout)
            )
        }
    }

    if (updateState is UpdateState.UpdateAvailable) {
        val release = (updateState as UpdateState.UpdateAvailable).release
        AlertDialog(
            onDismissRequest = { settingsViewModel.resetUpdateState() },
            title = { Text("Update Available") },
            text = { Text("A new version (${release.tag_name}) is available. Would you like to update?") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(release.html_url))
                    context.startActivity(intent)
                    settingsViewModel.resetUpdateState()
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.resetUpdateState() }) {
                    Text("Later")
                }
            }
        )
    }

    if (showExportActionDialog) {
        AlertDialog(
            onDismissRequest = { showExportActionDialog = false },
            title = { Text("Default Export Action") },
            text = {
                Column {
                    ExportAction.entries.forEach { actionOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsViewModel.setDefaultExportAction(actionOption)
                                    showExportActionDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = defaultExportAction == actionOption,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(when(actionOption) {
                                ExportAction.ASK -> "Ask every time"
                                ExportAction.SAVE -> "Save to gallery"
                                ExportAction.SHARE -> "Share as image"
                                ExportAction.CLIPBOARD -> "Copy to clipboard"
                            })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportActionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    AppTheme.entries.forEach { themeOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsViewModel.setTheme(themeOption)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = theme == themeOption,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(when(themeOption) {
                                AppTheme.LIGHT -> "Light"
                                AppTheme.DARK -> "Dark"
                                AppTheme.SYSTEM -> "System Default"
                            })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
