package com.ashes.crown

/** Pure combat orchestration rules shared by the UI and deterministic tests. */
internal object BattleManager {
    data class AttackResult(val damage: Int, val enemyHealthAfter: Int, val defeated: Boolean)
    data class DefeatResult(val goldLoss: Int, val healthAfter: Int, val woundedUntilDay: Int)
    data class RestoredEncounter(val enemyName: String, val enemyHealth: Int)

    /** Restores only coherent active encounters; stale/corrupt partial battle state is discarded. */
    fun restoreEncounter(savedName: String?, savedHealth: Int): RestoredEncounter {
        val name = savedName.orEmpty().trim().take(80)
        val health = savedHealth.coerceIn(0, 100_000)
        return if (name.isBlank() || health <= 0) RestoredEncounter("", 0)
        else RestoredEncounter(name, health)
    }

    fun playerAttack(
        baseRoll: Int,
        weaponBonus: Int,
        powerSkill: Int,
        woundPenalty: Int,
        enemyHealth: Int
    ): AttackResult {
        // A delayed/double UI callback must never deal damage to an encounter that has ended.
        if (enemyHealth <= 0) return AttackResult(0, 0, false)
        val damage = (baseRoll.coerceAtLeast(0) + weaponBonus.coerceAtLeast(0) + powerSkill.coerceIn(0, 10) * 2 - woundPenalty.coerceAtLeast(0)).coerceAtLeast(1)
        val remaining = (enemyHealth - damage).coerceAtLeast(0)
        return AttackResult(damage, remaining, remaining == 0)
    }

    fun enemyAttack(rawDamage: Int, armor: Int, guarding: Boolean): Int =
        BattleRules.incomingDamage(rawDamage.coerceAtLeast(0), armor.coerceAtLeast(0), guarding)

    fun defeat(currentGold: Int, worldDay: Int, woundedUntilDay: Int): DefeatResult {
        val loss = BattleRules.defeatGoldLoss(currentGold)
        return DefeatResult(
            goldLoss = loss,
            healthAfter = 35,
            woundedUntilDay = maxOf(woundedUntilDay.coerceAtLeast(0), (worldDay.toLong().coerceAtLeast(1L) + 1L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        )
    }

    /** Only a named encounter at exactly zero HP can be finalized as a victory. */
    fun canResolveVictory(enemyName: String?, enemyHealth: Int): Boolean =
        !enemyName.isNullOrBlank() && enemyHealth == 0

    fun rewards(defeatedName: String, firstBossVictory: Boolean, rewardsToday: Int): BattleRules.RewardPlan =
        BattleRules.rewardPlan(defeatedName, firstBossVictory, rewardsToday)
}
