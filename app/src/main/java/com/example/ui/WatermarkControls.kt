package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditingState
import com.example.model.WatermarkPosition
import com.example.ui.theme.IosDarkSurface
import com.example.ui.theme.IosDarkSurfaceVariant
import com.example.ui.theme.IosYellowAccent

@Composable
fun WatermarkControls(
    state: EditingState,
    onUpdate: (EditingState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(IosDarkSurface)
            .padding(16.dp)
    ) {
        // Toggle Watermark
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null,
                    tint = IosYellowAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Add Photo Watermark",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Shot on iPhone & Sumit Editor Badge",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Switch(
                checked = state.showWatermark,
                onCheckedChange = { onUpdate(state.copy(showWatermark = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = IosYellowAccent,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = IosDarkSurfaceVariant
                )
            )
        }

        if (state.showWatermark) {
            Spacer(modifier = Modifier.height(14.dp))

            // Preset Badge Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.watermarkText.contains("Sumit")) IosYellowAccent else IosDarkSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onUpdate(
                                state.copy(
                                    watermarkText = "Edited with Sumit iPhone Editor",
                                    watermarkSubtext = "By Sumit Bhumihar"
                                )
                            )
                        }
                ) {
                    Text(
                        text = "Sumit Editor",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.watermarkText.contains("Sumit")) Color.Black else Color.White,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.watermarkText.contains("iPhone")) IosYellowAccent else IosDarkSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onUpdate(
                                state.copy(
                                    watermarkText = "Shot on iPhone 16 Pro",
                                    watermarkSubtext = "Sumit Photo Studio"
                                )
                            )
                        }
                ) {
                    Text(
                        text = "Shot on iPhone",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.watermarkText.contains("iPhone")) Color.Black else Color.White,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Input
            OutlinedTextField(
                value = state.watermarkText,
                onValueChange = { onUpdate(state.copy(watermarkText = it)) },
                label = { Text("Watermark Title", color = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IosYellowAccent,
                    unfocusedBorderColor = IosDarkSurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Position Chooser
            Text(
                text = "Position",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(WatermarkPosition.values()) { pos ->
                    val isSelected = state.watermarkPosition == pos
                    val label = when (pos) {
                        WatermarkPosition.BOTTOM_LEFT -> "Bottom Left"
                        WatermarkPosition.BOTTOM_RIGHT -> "Bottom Right"
                        WatermarkPosition.TOP_LEFT -> "Top Left"
                        WatermarkPosition.TOP_RIGHT -> "Top Right"
                        WatermarkPosition.CENTER -> "Center"
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IosYellowAccent else IosDarkSurfaceVariant,
                        modifier = Modifier.clickable {
                            onUpdate(state.copy(watermarkPosition = pos))
                        }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
