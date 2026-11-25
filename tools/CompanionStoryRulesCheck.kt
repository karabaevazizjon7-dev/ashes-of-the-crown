package com.ashes.crown

fun main() {
    check(CompanionStoryManager.LAST_STAGE == 3)
    for (name in CompanionManager.roster) {
        check(CompanionStoryManager.chapter(name, 0) != null)
        check(CompanionStoryManager.chapter(name, 1) != null)
        check(CompanionStoryManager.chapter(name, 2) != null)
        check(CompanionStoryManager.chapter(name, 3) == null)
        val first = CompanionStoryManager.resolve(name, 0, 0, CompanionStoryChoice.SUPPORT)
        check(first.nextStage == 1 && first.bondAfter == 3 && !first.completed)
        val second = CompanionStoryManager.resolve(name, 1, 10, CompanionStoryChoice.INVESTIGATE)
        check(second.nextStage == 2 && second.bondAfter in 11..12)
        val final = CompanionStoryManager.resolve(name, 2, 20, CompanionStoryChoice.SUPPORT)
        check(final.nextStage == 3 && final.completed && final.bondAfter == 23)
    }
    check(CompanionStoryManager.normalizeStage(-20) == 0)
    check(CompanionStoryManager.normalizeStage(99) == 3)
    check(CompanionStoryManager.chapter("unknown", 0)?.companion == "Томас")
    check(runCatching { CompanionStoryManager.resolve("Гаррет Морн", 1, 9, CompanionStoryChoice.SUPPORT) }.isFailure)
    val completed = CompanionStoryManager.resolve("Селеста", 3, 40, CompanionStoryChoice.SUPPORT)
    check(completed.completed && completed.nextStage == 3 && completed.bondAfter == 40)
    val overflowBond = CompanionStoryManager.resolve("Гаррет Морн", 0, Int.MAX_VALUE, CompanionStoryChoice.SUPPORT)
    check(overflowBond.bondAfter == 100)
    println("Four companion arcs, chapter progression, trust gates, choices, completion, stage normalization and bond overflow passed")
}
