package com.ashes.crown

/** Pure layout rules for character portraits; values are dp. */
object PortraitLayoutRules {
    data class Size(val width: Int, val height: Int)

    fun relationPortrait(availableWidthDp: Int): Size = when {
        availableWidthDp < 280 -> Size(44, 58)
        availableWidthDp < 360 -> Size(50, 62)
        availableWidthDp < 480 -> Size(54, 66)
        else -> Size(58, 72)
    }

    fun storyPortrait(ultraCompact: Boolean, compactLandscape: Boolean): Size = when {
        ultraCompact -> Size(32, 45)
        compactLandscape -> Size(42, 59)
        else -> Size(48, 68)
    }
}
