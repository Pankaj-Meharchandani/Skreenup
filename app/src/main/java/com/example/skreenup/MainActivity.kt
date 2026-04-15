package com.example.skreenup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.skreenup.navigation.Editor
import com.example.skreenup.navigation.Home
import com.example.skreenup.navigation.SkreenupNavKey
import com.example.skreenup.ui.screens.EditorScreen
import com.example.skreenup.ui.screens.HomeScreen
import com.example.skreenup.ui.theme.SkreenupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkreenupTheme {
                SkreenupApp()
            }
        }
    }
}

@Composable
fun SkreenupApp() {
    val backStack = rememberNavBackStack(Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(onNavigateToEditor = { projectId ->
                    backStack.add(Editor(projectId = projectId))
                }, onNavigateWithPreset = { presetId ->
                    backStack.add(Editor(presetId = presetId))
                })
            }
            entry<Editor> { key ->
                EditorScreen(
                    projectId = key.projectId,
                    presetId = key.presetId,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
