package com.example.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.processor.ImageProcessor
import com.example.ui.theme.IosBlueAccent
import com.example.ui.theme.IosDarkBackground
import com.example.ui.theme.IosDarkSurface
import com.example.ui.theme.IosDarkSurfaceVariant
import com.example.ui.theme.IosGreenAccent
import com.example.ui.theme.IosYellowAccent

@Composable
fun EditorScreen(
    imageUri: Uri,
    onBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(imageUri) {
        viewModel.loadImage(context, imageUri)
    }

    val state by viewModel.editingState.collectAsState()
    val originalBitmap by viewModel.originalBitmap.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isComparing by viewModel.isComparing.collectAsState()
    val showHistogram by viewModel.showHistogram.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Discard Changes?", color = Color.White) },
            text = { Text("You have unsaved changes. Are you sure you want to exit?", color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onBack()
                }) {
                    Text("Discard", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Keep Editing", color = Color.White)
                }
            },
            containerColor = IosDarkSurface
        )
    }

    Scaffold(
        topBar = {
            EditorTopBar(
                canUndo = canUndo,
                canRedo = canRedo,
                isModified = state.isModified(),
                showHistogram = showHistogram,
                isSaving = isSaving,
                onCancel = {
                    if (state.isModified()) {
                        showExitDialog = true
                    } else {
                        onBack()
                    }
                },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onToggleHistogram = { viewModel.toggleHistogram() },
                onReset = { viewModel.resetAll() },
                onSave = {
                    viewModel.saveImageToGallery(context) { savedUri ->
                        if (savedUri != null) {
                            onBack()
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(IosDarkSurface)) {
                // Tab Content Controls
                when (currentTab) {
                    EditorTab.ADJUST -> EditingToolbar(state = state, onUpdate = { viewModel.updateState(it) })
                    EditorTab.FILTERS -> FilterSelector(state = state, onUpdate = { viewModel.updateState(it) })
                    EditorTab.CROP -> CropControls(state = state, onUpdate = { viewModel.updateState(it) })
                    EditorTab.WATERMARK -> WatermarkControls(state = state, onUpdate = { viewModel.updateState(it) })
                }

                // Tab Selector Bottom Navigation Bar
                NavigationBar(
                    containerColor = IosDarkSurface,
                    contentColor = Color.White,
                    modifier = Modifier.height(64.dp)
                ) {
                    NavigationBarItem(
                        selected = currentTab == EditorTab.ADJUST,
                        onClick = { viewModel.setTab(EditorTab.ADJUST) },
                        icon = { Icon(Icons.Default.Tune, contentDescription = "Adjust") },
                        label = { Text("Adjust", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = IosYellowAccent,
                            indicatorColor = IosYellowAccent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == EditorTab.FILTERS,
                        onClick = { viewModel.setTab(EditorTab.FILTERS) },
                        icon = { Icon(Icons.Default.Filter, contentDescription = "Filters") },
                        label = { Text("Filters", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = IosYellowAccent,
                            indicatorColor = IosYellowAccent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == EditorTab.CROP,
                        onClick = { viewModel.setTab(EditorTab.CROP) },
                        icon = { Icon(Icons.Default.Crop, contentDescription = "Crop") },
                        label = { Text("Crop & Rotate", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = IosYellowAccent,
                            indicatorColor = IosYellowAccent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == EditorTab.WATERMARK,
                        onClick = { viewModel.setTab(EditorTab.WATERMARK) },
                        icon = { Icon(Icons.Default.Badge, contentDescription = "Watermark") },
                        label = { Text("Watermark", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = IosYellowAccent,
                            indicatorColor = IosYellowAccent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = IosDarkBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(IosDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            originalBitmap?.let { original ->
                // Compute modified bitmap if not comparing original
                val displayBitmap = remember(original, state, isComparing) {
                    if (isComparing) original
                    else ImageProcessor.applyAdjustments(original, state)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    viewModel.setComparing(true)
                                    tryAwaitRelease()
                                    viewModel.setComparing(false)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = displayBitmap.asImageBitmap(),
                        contentDescription = "Edited Photo Canvas",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )

                    // ORIGINAL Indicator Badge on Long Press
                    AnimatedVisibility(
                        visible = isComparing,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "ORIGINAL",
                                color = IosYellowAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Live Histogram View Overlay
                    if (showHistogram) {
                        HistogramView(
                            bitmap = displayBitmap,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        )
                    }

                    // Tap/Press & Hold Hint overlay if modified
                    if (state.isModified() && !isComparing) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Hold image to compare with original",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            } ?: CircularProgressIndicator(color = IosYellowAccent)
        }
    }
}

@Composable
private fun EditorTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    isModified: Boolean,
    showHistogram: Boolean,
    isSaving: Boolean,
    onCancel: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleHistogram: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        color = IosDarkSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel Action
            TextButton(onClick = onCancel) {
                Text(
                    text = "Cancel",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Undo / Redo / Reset / Histogram Center Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) Color.White else Color.DarkGray
                    )
                }

                IconButton(
                    onClick = onRedo,
                    enabled = canRedo
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) Color.White else Color.DarkGray
                    )
                }

                IconButton(onClick = onToggleHistogram) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Histogram",
                        tint = if (showHistogram) IosYellowAccent else Color.White
                    )
                }

                if (isModified) {
                    IconButton(onClick = onReset) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset All",
                            tint = Color.Red
                        )
                    }
                }
            }

            // Save Action
            if (isSaving) {
                CircularProgressIndicator(
                    color = IosYellowAccent,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = IosYellowAccent,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    TextButton(onClick = onSave) {
                        Text(
                            text = "Save",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
