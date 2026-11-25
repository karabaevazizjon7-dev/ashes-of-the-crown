package com.ashes.crown

fun main() {
    val zero = PlayerProgressManager.grantExperience(PlayerProgressState(1, 0, 0), 0)
    check(zero.state == PlayerProgressState(1, 0, 0) && zero.levelsGained == 0)

    val oneLevel = PlayerProgressManager.grantExperience(PlayerProgressState(1, 0, 0), 100)
    check(oneLevel.state == PlayerProgressState(2, 0, 1))
    check(oneLevel.levelsGained == 1 && oneLevel.message?.contains("уровень 2") == true)

    val multiLevel = PlayerProgressManager.grantExperience(PlayerProgressState(1, 0, 0), 350)
    check(multiLevel.state == PlayerProgressState(3, 50, 2))
    check(multiLevel.levelsGained == 2)

    val carryOver = PlayerProgressManager.grantExperience(PlayerProgressState(4, 350, 7), 100)
    check(carryOver.state == PlayerProgressState(5, 50, 8))

    val negative = PlayerProgressManager.grantExperience(PlayerProgressState(-4, -20, -1), -10)
    check(negative.state == PlayerProgressState(1, 0, 0))

    val overflow = PlayerProgressManager.grantExperience(PlayerProgressState(1, Int.MAX_VALUE - 5, 0), 100)
    check(overflow.state.experience >= 0)
    check(overflow.state.level >= 1)

    val initial = listOf("A", "", "B")
    check(JournalManager.normalize(initial) == listOf("A", "B"))
    check(JournalManager.append(listOf("A"), "B") == listOf("A", "B"))
    check(JournalManager.append(listOf("A"), " ") == listOf("A"))
    check(JournalManager.append(listOf("A", "B", "C"), "D", 2) == listOf("C", "D"))
    check(JournalManager.normalize((1..600).map(Int::toString)).size == JournalManager.MAX_ENTRIES)
    check(JournalManager.normalize((1..600).map(Int::toString)).first() == "101")
    check(JournalManager.normalize(listOf("A", "B"), 0).isEmpty())

    println("Player progression and bounded journal checks passed")
}
