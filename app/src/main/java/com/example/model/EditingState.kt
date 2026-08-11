package com.example.model

enum class WatermarkPosition {
    BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT, CENTER
}

data class EditingState(
    val exposure: Float = 0f,         // -1.0 to 1.0
    val brilliance: Float = 0f,       // -1.0 to 1.0
    val highlights: Float = 0f,       // -1.0 to 1.0
    val shadows: Float = 0f,          // -1.0 to 1.0
    val contrast: Float = 1f,         // 0.5 to 1.5
    val brightness: Float = 0f,       // -0.5 to 0.5
    val blackPoint: Float = 0f,       // -0.5 to 0.5
    val saturation: Float = 1f,       // 0.0 to 2.0
    val vibrance: Float = 0f,         // -1.0 to 1.0
    val warmth: Float = 0f,           // -1.0 to 1.0
    val tint: Float = 0f,             // -1.0 to 1.0
    val sharpness: Float = 0f,        // 0.0 to 1.0
    val vignette: Float = 0f,         // 0.0 to 1.0
    
    val selectedFilter: String = "Original",
    val filterIntensity: Float = 1f,  // 0.0 to 1.0
    
    val rotationDegrees: Float = 0f,  // 0, 90, 180, 270
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val straightenAngle: Float = 0f,  // -45.0 to 45.0
    val cropRatioName: String = "Original",
    val cropRatioValue: Float? = null, // null = full/original
    
    val showWatermark: Boolean = false,
    val watermarkText: String = "Edited with Sumit iPhone Editor",
    val watermarkSubtext: String = "By Sumit Bhumihar",
    val watermarkPosition: WatermarkPosition = WatermarkPosition.BOTTOM_LEFT
) {
    fun isModified(): Boolean {
        return exposure != 0f ||
                brilliance != 0f ||
                highlights != 0f ||
                shadows != 0f ||
                contrast != 1f ||
                brightness != 0f ||
                blackPoint != 0f ||
                saturation != 1f ||
                vibrance != 0f ||
                warmth != 0f ||
                tint != 0f ||
                sharpness != 0f ||
                vignette != 0f ||
                selectedFilter != "Original" ||
                rotationDegrees != 0f ||
                flipHorizontal ||
                flipVertical ||
                straightenAngle != 0f ||
                cropRatioValue != null ||
                showWatermark
    }
}
