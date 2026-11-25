package com.ashes.crown

object StoryContentIntegrityCheck {
    @JvmStatic fun main(args: Array<String>) {
        for (ultra in listOf(false, true)) for (landscape in listOf(false, true)) {
            val title = StoryLayoutRules.choiceTitleFontSp(ultra, landscape)
            val detail = StoryLayoutRules.choiceDetailFontSp(ultra, landscape)
            check(title >= 8.0f) { "Choice title too small: $title" }
            check(detail >= 7.0f) { "Choice detail too small: $detail" }
        }
        val main = java.io.File("app/src/main/java/com/ashes/crown/MainActivity.kt").readText()
        val storyBlock = main.substring(main.indexOf("fun RenderPage_fallback()"), main.indexOf("@Composable", main.indexOf("fun RenderPage_fallback()") + 20).let { if (it < 0) main.length else it })
        check("Text(choice.title" in storyBlock && "Text(choice.detail" in storyBlock)
        check("maxLines = 2, overflow = TextOverflow.Ellipsis" !in storyBlock) { "Story choices must not silently truncate" }
        check("Text(scene.text" in storyBlock) { "Full narrative body must remain rendered" }
        println("StoryContentIntegrityCheck: PASS")
    }
}
