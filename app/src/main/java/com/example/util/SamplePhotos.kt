package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

data class SamplePhotoItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val generator: () -> Bitmap
)

object SamplePhotos {

    val SAMPLES = listOf(
        SamplePhotoItem(
            id = "sample_sunset",
            title = "Golden Hour Mountains",
            subtitle = "Landscape Sunset",
            generator = { generateSunsetMountainBitmap() }
        ),
        SamplePhotoItem(
            id = "sample_cyberpunk",
            title = "Cyber City Neon",
            subtitle = "Urban Nightlife",
            generator = { generateCyberpunkCityBitmap() }
        ),
        SamplePhotoItem(
            id = "sample_beach",
            title = "Tropical Paradise",
            subtitle = "Coast & Sun",
            generator = { generateTropicalBeachBitmap() }
        ),
        SamplePhotoItem(
            id = "sample_portrait",
            title = "Studio Portrait",
            subtitle = "Moody Lighting",
            generator = { generatePortraitBitmap() }
        )
    )

    fun getUriForSample(context: Context, sample: SamplePhotoItem): Uri {
        val cacheDir = File(context.cacheDir, "sample_photos").apply { mkdirs() }
        val file = File(cacheDir, "${sample.id}.jpg")
        
        if (!file.exists()) {
            val bitmap = sample.generator()
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
        }

        return Uri.fromFile(file)
    }

    private fun generateSunsetMountainBitmap(): Bitmap {
        val w = 1080
        val h = 1350
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Sky Gradient (Purple to Warm Orange to Golden Yellow)
        val skyGradient = LinearGradient(
            0f, 0f, 0f, h * 0.7f,
            intArrayOf(
                Color.parseColor("#2D1B4E"),
                Color.parseColor("#8C3B5E"),
                Color.parseColor("#E06A3B"),
                Color.parseColor("#F9AA33")
            ),
            floatArrayOf(0f, 0.35f, 0.7f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val skyPaint = Paint().apply { shader = skyGradient }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), skyPaint)

        // Sun Glow
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.5f, h * 0.55f, 220f,
                intArrayOf(Color.parseColor("#FFF3A1"), Color.parseColor("#00F9AA33")),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.5f, h * 0.55f, 220f, sunPaint)

        // Mountain Layer 1 (Distant - Purplish)
        val m1Path = Path().apply {
            moveTo(0f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.52f)
            lineTo(w * 0.55f, h * 0.6f)
            lineTo(w * 0.85f, h * 0.48f)
            lineTo(w.toFloat(), h * 0.58f)
            lineTo(w.toFloat(), h.toFloat())
            lineTo(0f, h.toFloat())
            close()
        }
        val m1Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#88422A50")
        }
        canvas.drawPath(m1Path, m1Paint)

        // Mountain Layer 2 (Foreground Silhouette)
        val m2Path = Path().apply {
            moveTo(0f, h * 0.72f)
            lineTo(w * 0.35f, h * 0.61f)
            lineTo(w * 0.65f, h * 0.7f)
            lineTo(w.toFloat(), h * 0.59f)
            lineTo(w.toFloat(), h.toFloat())
            lineTo(0f, h.toFloat())
            close()
        }
        val m2Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF1F112B")
        }
        canvas.drawPath(m2Path, m2Paint)

        // Pine Tree Silhouettes
        val treePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF100817")
        }
        for (i in 0..12) {
            val tx = (i * 90 + 30).toFloat()
            val ty = h * 0.75f + (i % 3) * 30f
            val treePath = Path().apply {
                moveTo(tx, ty - 80f)
                lineTo(tx - 25f, ty)
                lineTo(tx + 25f, ty)
                close()
            }
            canvas.drawPath(treePath, treePaint)
        }

        return bitmap
    }

    private fun generateCyberpunkCityBitmap(): Bitmap {
        val w = 1080
        val h = 1350
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark Cyan Sky
        val skyGradient = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(
                Color.parseColor("#050814"),
                Color.parseColor("#0A192F"),
                Color.parseColor("#1B2A4A")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), Paint().apply { shader = skyGradient })

        // Magenta & Cyan Neon Skyline Glows
        val neonPaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.3f, h * 0.45f, 350f,
                intArrayOf(Color.parseColor("#88FF0077"), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.3f, h * 0.45f, 350f, neonPaint1)

        val neonPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.75f, h * 0.5f, 400f,
                intArrayOf(Color.parseColor("#8800F0FF"), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.75f, h * 0.5f, 400f, neonPaint2)

        // Buildings Silhouettes
        val buildingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF080C16")
        }
        val windowCyan = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF00F0FF") }
        val windowPink = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFFF0077") }
        val windowYellow = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFFFD700") }

        val buildingBounds = listOf(
            floatArrayOf(50f, 400f, 250f, h.toFloat()),
            floatArrayOf(270f, 250f, 480f, h.toFloat()),
            floatArrayOf(500f, 350f, 720f, h.toFloat()),
            floatArrayOf(740f, 200f, 980f, h.toFloat())
        )

        for (b in buildingBounds) {
            canvas.drawRect(b[0], b[1], b[2], b[3], buildingPaint)

            // Random windows
            var wy = b[1] + 40f
            while (wy < h - 100f) {
                var wx = b[0] + 20f
                while (wx < b[2] - 30f) {
                    if ((wx.toInt() + wy.toInt()) % 3 != 0) {
                        val p = when ((wx.toInt() + wy.toInt()) % 3) {
                            1 -> windowCyan
                            2 -> windowPink
                            else -> windowYellow
                        }
                        canvas.drawRect(wx, wy, wx + 18f, wy + 25f, p)
                    }
                    wx += 35f
                }
                wy += 45f
            }
        }

        return bitmap
    }

    private fun generateTropicalBeachBitmap(): Bitmap {
        val w = 1080
        val h = 1350
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Tropical Turquoise Gradient
        val skyGrad = LinearGradient(
            0f, 0f, 0f, h * 0.55f,
            intArrayOf(Color.parseColor("#1A8CFF"), Color.parseColor("#66CCFF"), Color.parseColor("#E0F7FA")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h * 0.55f, Paint().apply { shader = skyGrad })

        // Sea
        val seaGrad = LinearGradient(
            0f, h * 0.55f, 0f, h * 0.78f,
            intArrayOf(Color.parseColor("#00B4D8"), Color.parseColor("#0077B6")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.55f, w.toFloat(), h * 0.78f, Paint().apply { shader = seaGrad })

        // Golden Sand
        val sandGrad = LinearGradient(
            0f, h * 0.78f, 0f, h.toFloat(),
            intArrayOf(Color.parseColor("#E9C46A"), Color.parseColor("#D4A373")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.78f, w.toFloat(), h.toFloat(), Paint().apply { shader = sandGrad })

        // Sun
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFF4A3") }
        canvas.drawCircle(w * 0.8f, h * 0.3f, 90f, sunPaint)

        // Palm Leaf Silhouette
        val palmPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF1C3119") }
        val trunkPath = Path().apply {
            moveTo(w * 0.15f, h.toFloat())
            quadTo(w * 0.25f, h * 0.65f, w * 0.35f, h * 0.45f)
            lineTo(w * 0.38f, h * 0.46f)
            quadTo(w * 0.28f, h * 0.66f, w * 0.18f, h.toFloat())
            close()
        }
        canvas.drawPath(trunkPath, palmPaint)

        return bitmap
    }

    private fun generatePortraitBitmap(): Bitmap {
        val w = 1080
        val h = 1350
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Warm Studio Background Gradient
        val bgGrad = RadialGradient(
            w * 0.5f, h * 0.4f, 700f,
            intArrayOf(Color.parseColor("#4A3B32"), Color.parseColor("#1F1814"), Color.parseColor("#0B0806")),
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), Paint().apply { shader = bgGrad })

        // Soft Warm Key Light Glow
        val keyLight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.35f, h * 0.35f, 350f,
                intArrayOf(Color.parseColor("#88FFB085"), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.35f, h * 0.35f, 350f, keyLight)

        // Elegant Portrait Silhouette Shape
        val skinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF2A1E17")
        }
        val headPath = Path().apply {
            addCircle(w * 0.5f, h * 0.38f, 180f, Path.Direction.CW)
            // Neck & Shoulders
            moveTo(w * 0.3f, h.toFloat())
            cubicTo(w * 0.3f, h * 0.65f, w * 0.42f, h * 0.52f, w * 0.42f, h * 0.5f)
            lineTo(w * 0.58f, h * 0.5f)
            cubicTo(w * 0.58f, h * 0.52f, w * 0.7f, h * 0.65f, w * 0.7f, h.toFloat())
            close()
        }
        canvas.drawPath(headPath, skinPaint)

        return bitmap
    }
}
