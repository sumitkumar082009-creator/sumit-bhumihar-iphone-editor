package com.example.processor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import com.example.model.EditingState
import com.example.model.FilterPresets
import com.example.model.WatermarkPosition
import kotlin.math.max
import kotlin.math.min

object ImageProcessor {

    fun applyAdjustments(original: Bitmap, state: EditingState): Bitmap {
        // Step 1: Geometry Transformations (Rotation, Flip, Straighten, Crop)
        var sourceBitmap = transformGeometry(original, state)

        // Step 2: Color Adjustments
        val colorAdjusted = applyColorAdjustments(sourceBitmap, state)

        // Step 3: Vignette
        if (state.vignette > 0f) {
            applyVignette(colorAdjusted, state.vignette)
        }

        // Step 4: Watermark
        if (state.showWatermark) {
            applyWatermark(colorAdjusted, state)
        }

        return colorAdjusted
    }

    private fun transformGeometry(original: Bitmap, state: EditingState): Bitmap {
        val matrix = Matrix()

        // Flip
        val sx = if (state.flipHorizontal) -1f else 1f
        val sy = if (state.flipVertical) -1f else 1f
        if (state.flipHorizontal || state.flipVertical) {
            matrix.postScale(sx, sy, original.width / 2f, original.height / 2f)
        }

        // Rotation & Straightening
        val totalRotation = state.rotationDegrees + state.straightenAngle
        if (totalRotation != 0f) {
            matrix.postRotate(totalRotation, original.width / 2f, original.height / 2f)
        }

        val rotated = Bitmap.createBitmap(
            original, 0, 0, original.width, original.height, matrix, true
        )

        // Aspect Ratio Crop
        val cropRatio = state.cropRatioValue
        if (cropRatio != null && cropRatio > 0f) {
            val width = rotated.width
            val height = rotated.height
            val currentRatio = width.toFloat() / height.toFloat()

            var cropW = width
            var cropH = height

            if (currentRatio > cropRatio) {
                // Image is wider than desired ratio
                cropW = (height * cropRatio).toInt().coerceAtMost(width)
            } else {
                // Image is taller than desired ratio
                cropH = (width / cropRatio).toInt().coerceAtMost(height)
            }

            val startX = max(0, (width - cropW) / 2)
            val startY = max(0, (height - cropH) / 2)

            return Bitmap.createBitmap(rotated, startX, startY, cropW, cropH)
        }

        return rotated
    }

    private fun applyColorAdjustments(original: Bitmap, state: EditingState): Bitmap {
        val bitmap = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val masterCm = ColorMatrix()

        // 1. Filter Preset (blended with intensity)
        val filterPreset = FilterPresets.getByName(state.selectedFilter)
        if (state.selectedFilter != "Original") {
            val filterMatrix = filterPreset.colorMatrix
            if (state.filterIntensity < 1f) {
                val blended = blendColorMatrix(ColorMatrix(), filterMatrix, state.filterIntensity)
                masterCm.postConcat(blended)
            } else {
                masterCm.postConcat(filterMatrix)
            }
        }

        // 2. Saturation
        val satCm = ColorMatrix()
        satCm.setSaturation(state.saturation)
        masterCm.postConcat(satCm)

        // 3. Contrast & Brightness & Exposure & Black Point
        // Contrast scales around 128 midtones
        val contrast = state.contrast + (state.brilliance * 0.2f)
        val brightness = (state.brightness * 255f) + (state.exposure * 60f) + (state.brilliance * 30f)
        val blackPoint = state.blackPoint * 50f
        
        val contrastMatrix = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness - blackPoint,
            0f, contrast, 0f, 0f, brightness - blackPoint,
            0f, 0f, contrast, 0f, brightness - blackPoint,
            0f, 0f, 0f, 1f, 0f
        ))
        masterCm.postConcat(contrastMatrix)

        // 4. Highlights & Shadows Adjustments
        if (state.highlights != 0f || state.shadows != 0f) {
            val highOffset = state.highlights * 25f
            val shadOffset = state.shadows * 25f
            val hsMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, highOffset + shadOffset,
                0f, 1f, 0f, 0f, highOffset + shadOffset,
                0f, 0f, 1f, 0f, highOffset + shadOffset,
                0f, 0f, 0f, 1f, 0f
            ))
            masterCm.postConcat(hsMatrix)
        }

        // 5. Warmth (Yellow/Blue shift) and Tint (Magenta/Green shift)
        val warm = state.warmth * 0.25f
        val tint = state.tint * 0.25f
        val tempMatrix = ColorMatrix(floatArrayOf(
            1f + warm, 0f, 0f, 0f, warm * 20f,
            0f, 1f + tint, 0f, 0f, tint * 15f,
            0f, 0f, 1f - warm, 0f, -warm * 20f,
            0f, 0f, 0f, 1f, 0f
        ))
        masterCm.postConcat(tempMatrix)

        paint.colorFilter = ColorMatrixColorFilter(masterCm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return bitmap
    }

    private fun blendColorMatrix(identity: ColorMatrix, filter: ColorMatrix, alpha: Float): ColorMatrix {
        val idArr = identity.array
        val fArr = filter.array
        val outArr = FloatArray(20)
        for (i in 0 until 20) {
            outArr[i] = idArr[i] + (fArr[i] - idArr[i]) * alpha
        }
        return ColorMatrix(outArr)
    }

    private fun applyVignette(bitmap: Bitmap, vignetteStrength: Float) {
        val canvas = Canvas(bitmap)
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val radius = max(width, height) * 0.7f

        val colors = intArrayOf(Color.TRANSPARENT, Color.argb((vignetteStrength * 180).toInt(), 0, 0, 0))
        val stops = floatArrayOf(0.4f, 1.0f)

        val gradient = RadialGradient(
            width / 2f, height / 2f, radius,
            colors, stops, Shader.TileMode.CLAMP
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = gradient
        }

        canvas.drawRect(0f, 0f, width, height, paint)
    }

    private fun applyWatermark(bitmap: Bitmap, state: EditingState) {
        val canvas = Canvas(bitmap)
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()

        val textSize = max(24f, min(w, h) * 0.04f)
        val subTextSize = textSize * 0.65f
        val padding = textSize * 0.8f

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            this.textSize = textSize
            isFakeBoldText = true
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            this.textSize = subTextSize
            setShadowLayer(3f, 1f, 1f, Color.BLACK)
        }

        val title = state.watermarkText
        val sub = state.watermarkSubtext

        val titleBounds = Rect()
        textPaint.getTextBounds(title, 0, title.length, titleBounds)

        var x = padding
        var y = h - padding - subTextSize - 10f

        when (state.watermarkPosition) {
            WatermarkPosition.BOTTOM_LEFT -> {
                x = padding
                y = h - padding - subTextSize
            }
            WatermarkPosition.BOTTOM_RIGHT -> {
                x = w - titleBounds.width() - padding
                y = h - padding - subTextSize
            }
            WatermarkPosition.TOP_LEFT -> {
                x = padding
                y = padding + textSize
            }
            WatermarkPosition.TOP_RIGHT -> {
                x = w - titleBounds.width() - padding
                y = padding + textSize
            }
            WatermarkPosition.CENTER -> {
                x = (w - titleBounds.width()) / 2f
                y = h / 2f
            }
        }

        // Draw small badge pill background
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(140, 0, 0, 0)
        }
        val pillRect = RectF(
            x - 12f,
            y - textSize - 8f,
            x + titleBounds.width() + 16f,
            y + subTextSize + 12f
        )
        canvas.drawRoundRect(pillRect, 12f, 12f, pillPaint)

        canvas.drawText(title, x, y, textPaint)
        if (sub.isNotEmpty()) {
            canvas.drawText(sub, x, y + subTextSize + 6f, subPaint)
        }
    }
}
