package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.EditorScreen
import com.example.ui.MainScreen
import com.example.ui.theme.IosDarkBackground
import com.example.ui.theme.SumitEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SumitEditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = IosDarkBackground
                ) {
                    SumitEditorApp()
                }
            }
        }
    }
}

@Composable
fun SumitEditorApp() {
    var activeImageUri by remember { mutableStateOf<Uri?>(null) }

    Crossfade(
        targetState = activeImageUri,
        label = "ScreenTransition"
    ) { uri ->
        if (uri == null) {
            MainScreen(
                onNavigateToEditor = { selectedUri ->
                    activeImageUri = selectedUri
                }
            )
        } else {
            EditorScreen(
                imageUri = uri,
                onBack = {
                    activeImageUri = null
                }
            )
        }
    }
}

