package com.example.skreenup.ui.screens.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.skreenup.R

@Composable
fun AboutTabScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Icon / Logo Placeholder
        Icon(
            imageVector = Icons.Rounded.Handyman,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Skreenup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Skreenup is a high-quality screenshot framing tool designed for developers and creators. " +
                    "Wrap your work in professional device frames and customize backgrounds for a perfect showcase.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.size(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Key Features",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text("• iPhone 17 & Galaxy S26 Frames", style = MaterialTheme.typography.bodySmall)
            Text("• High-Resolution PNG Export", style = MaterialTheme.typography.bodySmall)
            Text("• Custom Gradients & Blur Backgrounds", style = MaterialTheme.typography.bodySmall)
            Text("• Material You Dynamic Theming", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Developed with ❤️ for the Android Community",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
