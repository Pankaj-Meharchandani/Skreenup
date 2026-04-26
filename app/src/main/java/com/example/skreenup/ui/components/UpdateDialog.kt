package com.example.skreenup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skreenup.update.GitHubRelease

@Composable
fun UpdateDialog(
    release: GitHubRelease,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Update: ${release.tag_name}") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "A new version of Skreenup is available. See what's new:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (!release.body.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Release Notes",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatMarkdownToText(release.body),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onUpdate(release.html_url) }) {
                Text("Update Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        }
    )
}

fun formatMarkdownToText(markdown: String): String {
    var text = markdown
    
    // Remove headers (# Header)
    text = text.replace(Regex("(?m)^#+\\s+(.*)$"), "$1")
    
    // Convert bold (**text** or __text__)
    text = text.replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
    text = text.replace(Regex("__(.*?)__"), "$1")
    
    // Convert italic (*text* or _text_)
    text = text.replace(Regex("\\*(.*?)\\*"), "$1")
    text = text.replace(Regex("_(.*?)_"), "$1")
    
    // Convert links [text](url) -> text
    text = text.replace(Regex("\\[(.*?)]\\(.*?\\)"), "$1")
    
    // Clean up code blocks
    text = text.replace(Regex("```[a-z]*\\n?([\\s\\S]*?)```"), "$1")
    text = text.replace(Regex("`(.*?)`"), "$1")
    
    // Bullet points: keep them but normalize
    text = text.replace(Regex("(?m)^[*-]\\s+"), "• ")
    
    return text.trim()
}
