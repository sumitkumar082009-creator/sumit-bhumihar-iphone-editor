package com.example.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HistogramView(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    var rBins by remember { mutableStateOf(FloatArray(256)) }
    var gBins by remember { mutableStateOf(FloatArray(256)) }
    var bBins by remember { mutableStateOf(FloatArray(256)) }
    var maxVal by remember { mutableStateOf(1f) }

    LaunchedEffect(bitmap) {
        if (bitmap == null) return@LaunchedEffect
        withContext(Dispatchers.Default) {
            val r = FloatArray(256)
            val g = FloatArray(256)
            val b = FloatArray(256)

            // Downsample for speed
            val width = bitmap.width
            val height = bitmap.height
            val step = (width * height / 10000).coerceAtLeast(1)

            val pixels = IntArray(width * height)
            try {
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                var i = 0
                while (i < pixels.size) {
                    val px = pixels[i]
                    val red = (px shr 16) and 0xFF
                    val green = (px shr 8) and 0xFF
                    val blue = px and 0xFF

                    r[red]++
                    g[green]++
                    b[blue]++

                    i += step
                }
            } catch (e: Exception) {
                // Ignore pixel read errors on recycled bitmap
            }

            var peak = 1f
            for (j in 0 until 256) {
                if (r[j] > peak) peak = r[j]
                if (g[j] > peak) peak = g[j]
                if (b[j] > peak) peak = b[j]
            }

            rBins = r
            gBins = g
            bBins = b
            maxVal = peak
        }
    }

    Box(
        modifier = modifier
            .width(160.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = w / 256f

            fun buildPath(bins: FloatArray): Path {
                val path = Path()
                path.moveTo(0f, h)
                for (i in 0 until 256) {
                    val x = i * step
                    val y = h - ((bins[i] / maxVal) * h)
                    path.lineTo(x, y)
                }
                path.lineTo(w, h)
                path.close()
                return path
            }

            drawPath(buildPath(rBins), Color.Red.copy(alpha = 0.35f))
            drawPath(buildPath(gBins), Color.Green.copy(alpha = 0.35f))
            drawPath(buildPath(bBins), Color.Blue.copy(alpha = 0.35f))

            // Outline
            drawRect(
                color = Color.White.copy(alpha = 0.2f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}
