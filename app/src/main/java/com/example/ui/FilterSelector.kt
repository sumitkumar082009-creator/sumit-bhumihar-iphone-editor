package com.example.ui

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditingState
import com.example.model.FilterPresets
import com.example.ui.theme.IosDarkBorder
import com.example.ui.theme.IosDarkSurface
import com.example.ui.theme.IosDarkSurfaceVariant
import com.example.ui.theme.IosTextMuted
import com.example.ui.theme.IosYellowAccent

@Composable
fun FilterSelector(
    state: EditingState,
    onUpdate: (EditingState) -> Unit
) {
    val currentPreset = FilterPresets.getByName(state.selectedFilter)

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
            // Header showing Selected Filter Name & Category
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.selectedFilter.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = if (state.selectedFilter != "Original") IosYellowAccent else Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = currentPreset.description,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = IosTextMuted
                )
            }

            // Intensity Slider (only shown if a filter is selected)
            if (state.selectedFilter != "Original") {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INTENSITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = IosTextMuted
                    )
                    Text(
                        text = "${(state.filterIntensity * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosYellowAccent
                    )
                }

                Slider(
                    value = state.filterIntensity,
                    onValueChange = { onUpdate(state.copy(filterIntensity = it)) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = IosYellowAccent,
                        inactiveTrackColor = IosDarkSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Equal-sized Horizontal Filters Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.width(8.dp)) }
                items(FilterPresets.ALL_FILTERS) { filter ->
                    val isSelected = state.selectedFilter == filter.name

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(76.dp)
                            .clickable {
                                onUpdate(state.copy(selectedFilter = filter.name))
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    when (filter.category) {
                                        "Vivid" -> Color(0xFF3E2723)
                                        "Dramatic" -> Color(0xFF1A237E)
                                        "B&W" -> Color(0xFF212121)
                                        "Stylized" -> Color(0xFF4A148C)
                                        else -> IosDarkSurfaceVariant
                                    }
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) IosYellowAccent else Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = IosYellowAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = filter.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = filter.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) IosYellowAccent else IosTextMuted
                        )
                    }
                }
                item { Spacer(modifier = Modifier.width(8.dp)) }
            }
        }
    }
}
