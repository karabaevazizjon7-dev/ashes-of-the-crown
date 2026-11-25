package com.ashes.crown

object StoryLayoutRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        val sizes = mutableListOf<Float>()
        for (height in listOf(320, 400, 600, 800, 1000)) {
            for (length in listOf(100, 450, 700, 1000, 1400, 2000)) {
                for (choices in 0..5) {
                    val size = StoryLayoutRules.bodyFontSp(height, length, choices, height < 360, height < 430)
                    check(size in 7.0f..10.8f) { "Font outside safe range: $size" }
                    sizes += size
                }
            }
        }
        check(StoryLayoutRules.bodyFontSp(800, 100, 1, false, false) > StoryLayoutRules.bodyFontSp(400, 1500, 5, false, true))
        check(StoryLayoutRules.portraitWidthDp(true, true) < StoryLayoutRules.portraitWidthDp(false, false))
        check(StoryLayoutRules.portraitHeightDp(true, true) < StoryLayoutRules.portraitHeightDp(false, false))
        check(StoryLayoutRules.choiceTitleFontSp(true, true) < StoryLayoutRules.choiceTitleFontSp(false, false))
        println("StoryLayoutRulesCheck: PASS (${sizes.size} responsive combinations)")
    }
}
