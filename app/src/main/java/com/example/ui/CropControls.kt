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
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditingState
import com.example.ui.theme.IosDarkSurface
import com.example.ui.theme.IosDarkSurfaceVariant
import com.example.ui.theme.IosYellowAccent
import kotlin.math.roundToInt

data class AspectRatioItem(
    val name: String,
    val value: Float?
)

val ASPECT_RATIOS = listOf(
    AspectRatioItem("Original", null),
    AspectRatioItem("1:1 Square", 1.0f),
    AspectRatioItem("9:16 Portrait", 0.5625f),
    AspectRatioItem("4:3 Classic", 1.333f),
    AspectRatioItem("16:9 Widescreen", 1.777f),
    AspectRatioItem("3:2 Photo", 1.5f)
)

@Composable
fun CropControls(
    state: EditingState,
    onUpdate: (EditingState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(IosDarkSurface)
            .padding(vertical = 12.dp)
    ) {
        // Quick Action Buttons (Rotate 90, Flip H, Flip V)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IosDarkSurfaceVariant,
                modifier = Modifier.clickable {
                    val nextRot = (state.rotationDegrees + 90f) % 360f
                    onUpdate(state.copy(rotationDegrees = nextRot))
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "Rotate 90°",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rotate 90°", fontSize = 12.sp, color = Color.White)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (state.flipHorizontal) IosYellowAccent else IosDarkSurfaceVariant,
                modifier = Modifier.clickable {
                    onUpdate(state.copy(flipHorizontal = !state.flipHorizontal))
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flip,
                        contentDescription = "Flip Horizontal",
                        tint = if (state.flipHorizontal) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Flip H",
                        fontSize = 12.sp,
                        color = if (state.flipHorizontal) Color.Black else Color.White
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (state.flipVertical) IosYellowAccent else IosDarkSurfaceVariant,
                modifier = Modifier.clickable {
                    onUpdate(state.copy(flipVertical = !state.flipVertical))
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Flip Vertical",
                        tint = if (state.flipVertical) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Flip V",
                        fontSize = 12.sp,
                        color = if (state.flipVertical) Color.Black else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Straighten Dial Slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Straighten Angle",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "${state.straightenAngle.roundToInt()}°",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.straightenAngle != 0f) IosYellowAccent else Color.Gray
            )
        }

        Slider(
            value = state.straightenAngle,
            onValueChange = { onUpdate(state.copy(straightenAngle = it)) },
            valueRange = -45f..45f,
            colors = SliderDefaults.colors(
                thumbColor = IosYellowAccent,
                activeTrackColor = IosYellowAccent,
                inactiveTrackColor = IosDarkSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Aspect Ratio Selector
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.width(10.dp)) }
            items(ASPECT_RATIOS) { aspect ->
                val isSelected = (state.cropRatioName == aspect.name) ||
                        (state.cropRatioValue == aspect.value && aspect.value != null)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) IosYellowAccent else IosDarkSurfaceVariant,
                    modifier = Modifier.clickable {
                        onUpdate(
                            state.copy(
                                cropRatioName = aspect.name,
                                cropRatioValue = aspect.value
                            )
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Crop,
                            contentDescription = aspect.name,
                            tint = if (isSelected) Color.Black else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = aspect.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.width(10.dp)) }
        }
    }
}
