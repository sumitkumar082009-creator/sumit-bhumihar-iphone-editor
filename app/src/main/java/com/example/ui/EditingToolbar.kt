package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditingState
import com.example.ui.theme.IosDarkBorder
import com.example.ui.theme.IosDarkSurface
import com.example.ui.theme.IosDarkSurfaceVariant
import com.example.ui.theme.IosTextMuted
import com.example.ui.theme.IosYellowAccent
import kotlin.math.roundToInt

data class AdjustmentCategory(
    val name: String,
    val icon: ImageVector,
    val range: ClosedFloatingPointRange<Float>,
    val defaultValue: Float,
    val getValue: (EditingState) -> Float,
    val updateValue: (EditingState, Float) -> EditingState
)

val CATEGORIES = listOf(
    AdjustmentCategory(
        name = "Exposure",
        icon = Icons.Default.Exposure,
        range = -1f..1f,
        defaultValue = 0f,
        getValue = { it.exposure },
        updateValue = { s, v -> s.copy(exposure = v) }
    ),
    AdjustmentCategory(
        name = "Brilliance",
        icon = Icons.Default.WbSunny,
        range = -1f..1f,
        defaultValue = 0f,
        getValue = { it.brilliance },
        updateValue = { s, v -> s.copy(brilliance = v) }
    ),
    AdjustmentCategory(
        name = "Highlights",
        icon = Icons.Default.Highlight,
        range = -1f..1f,
        defaultValue = 0f,
        getValue = { it.highlights },
        updateValue = { s, v -> s.copy(highlights = v) }
    ),
    AdjustmentCategory(
        name = "Shadows",
        icon = Icons.Default.FilterDrama,
        range = -1f..1f,
        defaultValue = 0f,
        getValue = { it.shadows },
        updateValue = { s, v -> s.copy(shadows = v) }
    ),
    AdjustmentCategory(
        name = "Contrast",
        icon = Icons.Default.Contrast,
        range = 0.5f..1.5f,
        defaultValue = 1f,
        getValue = { it.contrast },
        updateValue = { s, v -> s.copy(contrast = v) }
    ),
    AdjustmentCategory(
        name = "Brightness",
        icon = Icons.Default.Brightness6,
        range = -0.5f..0.5f,
        defaultValue = 0f,
        getValue = { it.brightness },
        updateValue = { s, v -> s.copy(brightness = v) }
    ),
    AdjustmentCategory(
        name = "Black Point",
        icon = Icons.Default.Details,
        range = -0.5f..0.5f,
        defaultValue = 0f,
        getValue = { it.blackPoint },
        updateValue = { s, v -> s.copy(blackPoint = v) }
    ),
    AdjustmentCategory(
        name = "Saturation",
        icon = Icons.Default.Palette,
        range = 0f..2f,
        defaultValue = 1f,
        getValue = { it.saturation },
        updateValue = { s, v -> s.copy(saturation = v) }
    ),
    AdjustmentCategory(
        name = "Warmth",
        icon = Icons.Default.Thermostat,
        range = -1f..1f,
        defaultValue = 0f,
        getValue = { it.warmth },
        updateValue = { s, v -> s.copy(warmth = v) }
    ),
    AdjustmentCategory(
        name = "Tint",
        icon = Icons.Default.InvertColors,
        range = -1f..1f,
        defaultValue = 0f,
        getValue = { it.tint },
        updateValue = { s, v -> s.copy(tint = v) }
    ),
    AdjustmentCategory(
        name = "Vignette",
        icon = Icons.Default.ShutterSpeed,
        range = 0f..1f,
        defaultValue = 0f,
        getValue = { it.vignette },
        updateValue = { s, v -> s.copy(vignette = v) }
    )
)

@Composable
fun EditingToolbar(
    state: EditingState,
    onUpdate: (EditingState) -> Unit
) {
    var selectedCategoryName by remember { mutableStateOf("Exposure") }
    val currentCategory = CATEGORIES.firstOrNull { it.name == selectedCategoryName } ?: CATEGORIES.first()

    val currentValue = currentCategory.getValue(state)
    val isDefault = currentValue == currentCategory.defaultValue

    // Convert value to display string like +25, -10, 0
    val displayValue = remember(currentValue, currentCategory) {
        val diff = currentValue - currentCategory.defaultValue
        val scaled = (diff * 100).roundToInt()
        if (scaled > 0) "+$scaled" else "$scaled"
    }

    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = IosDarkSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = IosDarkBorder,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(vertical = 16.dp)
        ) {
            // Selected Category Header & Numeric Value
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentCategory.name.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayValue,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (!isDefault) IosYellowAccent else IosTextMuted
                    )

                    if (!isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset",
                            tint = IosYellowAccent,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    onUpdate(currentCategory.updateValue(state, currentCategory.defaultValue))
                                }
                        )
                    }
                }
            }

            // Slider
            Slider(
                value = currentValue,
                onValueChange = { newValue ->
                    onUpdate(currentCategory.updateValue(state, newValue))
                },
                valueRange = currentCategory.range,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = IosYellowAccent,
                    inactiveTrackColor = IosDarkSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Equal-sized Horizontal Category Options
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.width(8.dp)) }
                items(CATEGORIES) { cat ->
                    val isSelected = cat.name == selectedCategoryName
                    val value = cat.getValue(state)
                    val isModified = value != cat.defaultValue

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(76.dp)
                            .clickable { selectedCategoryName = cat.name }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> IosYellowAccent.copy(alpha = 0.2f)
                                        isModified -> IosDarkSurfaceVariant
                                        else -> IosDarkSurfaceVariant.copy(alpha = 0.6f)
                                    }
                                )
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = IosYellowAccent,
                                            shape = CircleShape
                                        )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.name,
                                tint = when {
                                    isSelected -> IosYellowAccent
                                    isModified -> Color.White
                                    else -> IosTextMuted
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = cat.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isSelected -> IosYellowAccent
                                isModified -> Color.White
                                else -> IosTextMuted
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.width(8.dp)) }
            }
        }
    }
}
