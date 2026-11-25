package com.ashes.crown

fun main() {
    val valid = BattleManager.restoreEncounter("  Наёмник Чёрной розы  ", 48)
    check(valid.enemyName == "Наёмник Чёрной розы")
    check(valid.enemyHealth == 48)

    val missingName = BattleManager.restoreEncounter("", 48)
    check(missingName.enemyName.isEmpty() && missingName.enemyHealth == 0)
    val deadEncounter = BattleManager.restoreEncounter("Дорожный разбойник", 0)
    check(deadEncounter.enemyName.isEmpty() && deadEncounter.enemyHealth == 0)
    val negativeHealth = BattleManager.restoreEncounter("Дорожный разбойник", -9)
    check(negativeHealth.enemyName.isEmpty() && negativeHealth.enemyHealth == 0)
    val oversizedHealth = BattleManager.restoreEncounter("Древний рыцарь", Int.MAX_VALUE)
    check(oversizedHealth.enemyHealth == 100_000)
    val oversizedName = BattleManager.restoreEncounter("А".repeat(200), 10)
    check(oversizedName.enemyName.length == 80)

    val staleAttack = BattleManager.playerAttack(14, 7, 10, 0, 0)
    check(staleAttack == BattleManager.AttackResult(0, 0, false))
    val staleNegativeAttack = BattleManager.playerAttack(14, 7, 10, 0, -1)
    check(staleNegativeAttack == BattleManager.AttackResult(0, 0, false))
    val lethal = BattleManager.playerAttack(14, 0, 0, 0, 14)
    check(lethal == BattleManager.AttackResult(14, 0, true))
    check(BattleManager.canResolveVictory("Наёмник Чёрной розы", 0))
    check(!BattleManager.canResolveVictory("", 0))
    check(!BattleManager.canResolveVictory(null, 0))
    check(!BattleManager.canResolveVictory("Наёмник Чёрной розы", 1))
    check(!BattleManager.canResolveVictory("Наёмник Чёрной розы", -1))

    val normalDefeat = BattleManager.defeat(100, 8, 7)
    check(normalDefeat.woundedUntilDay == 9)
    val overflowDay = BattleManager.defeat(100, Int.MAX_VALUE, 0)
    check(overflowDay.woundedUntilDay == Int.MAX_VALUE)
    val invalidDay = BattleManager.defeat(100, Int.MIN_VALUE, 0)
    check(invalidDay.woundedUntilDay == 2)

    println("BattleIntegrityRulesCheck: restore invariants, stale callbacks and day-overflow boundaries passed")
}
