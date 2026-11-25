package com.ashes.crown

/** Pure responsive typography rules for the two-page story reader. */
object StoryLayoutRules {
    fun bodyFontSp(
        screenHeightDp: Int,
        textLength: Int,
        choiceCount: Int,
        ultraCompact: Boolean,
        compactLandscape: Boolean
    ): Float {
        val base = when {
            ultraCompact -> 7.4f
            compactLandscape -> 8.7f
            screenHeightDp < 600 -> 9.2f
            else -> 10.4f
        }
        val textPenalty = when {
            textLength > 1_400 -> 1.5f
            textLength > 1_000 -> 1.1f
            textLength > 700 -> 0.7f
            textLength > 450 -> 0.35f
            else -> 0f
        }
        val choicePenalty = when {
            choiceCount >= 4 -> 0.7f
            choiceCount == 3 -> 0.35f
            else -> 0f
        }
        val heightBonus = if (!compactLandscape && screenHeightDp >= 760) 0.4f else 0f
        return (base - textPenalty - choicePenalty + heightBonus).coerceIn(7.0f, 10.8f)
    }

    // Keep choice text readable; content is allowed to wrap instead of being truncated.
    fun choiceTitleFontSp(ultraCompact: Boolean, compactLandscape: Boolean): Float = when {
        ultraCompact -> 8.0f
        compactLandscape -> 8.8f
        else -> 10.0f
    }

    fun choiceDetailFontSp(ultraCompact: Boolean, compactLandscape: Boolean): Float = when {
        ultraCompact -> 7.0f
        compactLandscape -> 7.6f
        else -> 8.4f
    }

    fun portraitWidthDp(ultraCompact: Boolean, compactLandscape: Boolean): Int = when {
        ultraCompact -> 32
        compactLandscape -> 42
        else -> 48
    }

    fun portraitHeightDp(ultraCompact: Boolean, compactLandscape: Boolean): Int = when {
        ultraCompact -> 45
        compactLandscape -> 59
        else -> 68
    }
}
