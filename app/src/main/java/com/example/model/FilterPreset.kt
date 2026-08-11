package com.example.model

import android.graphics.ColorMatrix

data class FilterPreset(
    val name: String,
    val description: String,
    val category: String, // "Vivid", "Dramatic", "B&W", "Stylized"
    val colorMatrix: ColorMatrix
)

object FilterPresets {
    val ALL_FILTERS = listOf(
        FilterPreset(
            name = "Original",
            description = "Natural photo without presets",
            category = "Standard",
            colorMatrix = ColorMatrix()
        ),
        FilterPreset(
            name = "Vivid",
            description = "Accentuates colors for punchy contrast and clarity",
            category = "Vivid",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.2f, 0.1f, 0.0f, 0f, 10f,
                0.0f, 1.2f, 0.1f, 0f, 10f,
                0.0f, 0.1f, 1.2f, 0f, 10f,
                0.0f, 0.0f, 0.0f, 1f, 0f
            ))
        ),
        FilterPreset(
            name = "Vivid Warm",
            description = "Enhances color saturation with rich golden tones",
            category = "Vivid",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.3f, 0.1f, 0.0f, 0f, 15f,
                0.0f, 1.2f, 0.0f, 0f, 10f,
                0.0f, 0.0f, 0.9f, 0f, -10f,
                0.0f, 0.0f, 0.0f, 1f, 0f
            ))
        ),
        FilterPreset(
            name = "Vivid Cool",
            description = "Punches up colors with crisp cyan and blue hues",
            category = "Vivid",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.0f, 0.0f, 0.1f, 0f, -5f,
                0.0f, 1.2f, 0.1f, 0f, 5f,
                0.1f, 0.1f, 1.35f, 0f, 15f,
                0.0f, 0.0f, 0.0f, 1f, 0f
            ))
        ),
        FilterPreset(
            name = "Dramatic",
            description = "Increases contrast and deepens shadows for intensity",
            category = "Dramatic",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.35f, -0.1f, -0.1f, 0f, -20f,
                -0.1f, 1.35f, -0.1f, 0f, -20f,
                -0.1f, -0.1f, 1.35f, 0f, -20f,
                0.0f,  0.0f,  0.0f,  1f, 0f
            ))
        ),
        FilterPreset(
            name = "Dramatic Warm",
            description = "Deep contrast paired with warm sun-drenched mood",
            category = "Dramatic",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.4f,  0.0f, -0.1f, 0f, -10f,
                -0.1f, 1.3f, -0.1f, 0f, -15f,
                -0.2f, -0.1f, 1.0f, 0f, -30f,
                0.0f,  0.0f,  0.0f,  1f, 0f
            ))
        ),
        FilterPreset(
            name = "Dramatic Cool",
            description = "Deep shadows combined with stark icy tones",
            category = "Dramatic",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.0f,  -0.1f, -0.1f, 0f, -25f,
                -0.1f, 1.3f,  0.0f,  0f, -15f,
                -0.1f, 0.0f,  1.45f, 0f, -5f,
                0.0f,  0.0f,  0.0f,  1f, 0f
            ))
        ),
        FilterPreset(
            name = "Mono",
            description = "Classic neutral black & white with smooth tones",
            category = "B&W",
            colorMatrix = ColorMatrix().apply { setSaturation(0f) }
        ),
        FilterPreset(
            name = "Silvertone",
            description = "Bright high-contrast silver black & white",
            category = "B&W",
            colorMatrix = ColorMatrix(floatArrayOf(
                0.35f, 0.65f, 0.15f, 0f, 10f,
                0.35f, 0.65f, 0.15f, 0f, 10f,
                0.35f, 0.65f, 0.15f, 0f, 10f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            ))
        ),
        FilterPreset(
            name = "Noir",
            description = "Cinematic dark black & white with deep blacks",
            category = "B&W",
            colorMatrix = ColorMatrix(floatArrayOf(
                0.45f, 0.55f, 0.10f, 0f, -25f,
                0.45f, 0.55f, 0.10f, 0f, -25f,
                0.45f, 0.55f, 0.10f, 0f, -25f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            ))
        ),
        FilterPreset(
            name = "Warm Portrait",
            description = "Soft warm glow optimized for skin tones",
            category = "Stylized",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.15f, 0.05f, 0.0f, 0f, 12f,
                0.02f, 1.08f, 0.0f, 0f, 8f,
                0.0f,  0.0f,  0.88f, 0f, -5f,
                0.0f,  0.0f,  0.0f,  1f, 0f
            ))
        ),
        FilterPreset(
            name = "Cyberpunk",
            description = "Futuristic neon cyan and magenta glow",
            category = "Stylized",
            colorMatrix = ColorMatrix(floatArrayOf(
                1.2f, -0.2f, 0.2f, 0f, 20f,
                -0.1f, 1.0f, 0.2f, 0f, -10f,
                0.3f, -0.1f, 1.4f, 0f, 30f,
                0.0f,  0.0f, 0.0f, 1f, 0f
            ))
        ),
        FilterPreset(
            name = "Vintage",
            description = "Filmic retro look with muted contrast and warm fade",
            category = "Stylized",
            colorMatrix = ColorMatrix(floatArrayOf(
                0.9f, 0.1f, 0.1f, 0f, 20f,
                0.1f, 0.8f, 0.1f, 0f, 15f,
                0.1f, 0.1f, 0.7f, 0f, 30f,
                0.0f, 0.0f, 0.0f, 1f, 0f
            ))
        )
    )

    fun getByName(name: String): FilterPreset {
        return ALL_FILTERS.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: ALL_FILTERS.first()
    }
}
