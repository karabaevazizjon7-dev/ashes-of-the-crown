package com.ashes.crown

fun main() {
    check(CompanionRelationshipManager.pairs.size == 6)
    check(CompanionRelationshipManager.pairs.toSet().size == 6)
    for (pair in CompanionRelationshipManager.pairs) {
        val scene = CompanionRelationshipManager.scene(pair)
        check(scene.pair == pair && scene.title.isNotBlank() && scene.description.isNotBlank())
        val mediated = CompanionRelationshipManager.resolve(pair, 0, 0, CompanionRelationshipChoice.MEDIATE)
        check(mediated.firstScore == 4 && mediated.secondScore == 4)
        val sideA = CompanionRelationshipManager.resolve(pair, 0, 0, CompanionRelationshipChoice.SUPPORT_FIRST)
        val sideB = CompanionRelationshipManager.resolve(pair, 0, 0, CompanionRelationshipChoice.SUPPORT_SECOND)
        check(sideA.firstScore == -3 && sideA.secondScore == -3)
        check(sideB.firstScore == -3 && sideB.secondScore == -3)
        check(sideA.report.contains(pair.first) && sideB.report.contains(pair.second))
        check(CompanionRelationshipManager.resolve(pair, 99, 99, CompanionRelationshipChoice.MEDIATE).firstScore == 100)
        check(CompanionRelationshipManager.resolve(pair, -99, -99, CompanionRelationshipChoice.SUPPORT_FIRST).firstScore == -100)
    }
    check(CompanionRelationshipManager.normalize(500) == 100)
    check(CompanionRelationshipManager.normalize(-500) == -100)
    val overflowUp = CompanionRelationshipManager.resolve(CompanionPair.GARRETT_CELESTE, Int.MAX_VALUE, Int.MAX_VALUE, CompanionRelationshipChoice.MEDIATE)
    val overflowDown = CompanionRelationshipManager.resolve(CompanionPair.GARRETT_CELESTE, Int.MIN_VALUE, Int.MIN_VALUE, CompanionRelationshipChoice.SUPPORT_FIRST)
    check(overflowUp.firstScore == 100 && overflowUp.secondScore == 100)
    check(overflowDown.firstScore == -100 && overflowDown.secondScore == -100)
    println("Six companion pair scenes, mediation, taking sides, bounded affinity and overflow-safe reports passed")
}
