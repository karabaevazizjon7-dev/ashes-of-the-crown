package com.ashes.crown

object PortraitLayoutRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        val widths = listOf(240, 279, 280, 320, 359, 360, 420, 480, 600, 900)
        val sizes = widths.map(PortraitLayoutRules::relationPortrait)
        check(sizes.all { it.width > 0 && it.height > it.width })
        check(sizes.zipWithNext().all { (a, b) -> b.width >= a.width && b.height >= a.height })
        check(PortraitLayoutRules.storyPortrait(true, false) == PortraitLayoutRules.Size(32, 45))
        check(PortraitLayoutRules.storyPortrait(false, true) == PortraitLayoutRules.Size(42, 59))
        check(PortraitLayoutRules.storyPortrait(false, false) == PortraitLayoutRules.Size(48, 68))
        println("PortraitLayoutRulesCheck: PASS (${widths.size} widths + story presets)")
    }
}
