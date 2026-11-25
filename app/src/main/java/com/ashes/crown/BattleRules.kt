package com.ashes.crown

/**
 * Deterministic combat calculations and reward decisions.
 * This file deliberately has no Android or Compose dependencies.
 */
internal object BattleRules {
    data class RewardPlan(
        val eligible: Boolean,
        val experience: Int,
        val gold: Int,
        val reputation: Int,
        val countsTowardDailyLimit: Boolean,
        val isFirstUniqueBossVictory: Boolean
    )

    fun woundAttackPenalty(isWounded: Boolean): Int = if (isWounded) 3 else 0

    fun woundIncomingDamageBonus(isWounded: Boolean): Int = if (isWounded) 2 else 0

    fun armorValue(
        hasReinforcedArmor: Boolean,
        hasGuardArmor: Boolean,
        guardSkill: Int
    ): Int {
        val equipmentArmor = when {
            hasReinforcedArmor -> 5
            hasGuardArmor -> 3
            else -> 0
        }
        return equipmentArmor + guardSkill.coerceIn(0, 10) * 2
    }

    fun incomingDamage(rawDamage: Int, armor: Int, isGuarding: Boolean): Int {
        val mitigatedRaw = if (isGuarding) rawDamage / 2 else rawDamage
        return (mitigatedRaw - armor).coerceAtLeast(1)
    }

    fun defeatGoldLoss(currentGold: Int): Int =
        minOf(currentGold.coerceAtLeast(0), (currentGold / 10).coerceIn(3, 10))

    /**
     * Creates the complete reward decision for one victory. Ordinary encounters
     * receive up to four paid victories per day. The first unique Ancient Knight
     * victory bypasses that cap; later boss victories never pay repeatable rewards.
     */
    fun rewardPlan(
        defeatedName: String,
        firstUniqueBossVictory: Boolean,
        repeatableRewardsToday: Int,
        dailyLimit: Int = 4
    ): RewardPlan {
        val isAncientKnight = defeatedName.contains("Древний рыцарь")
        val eligible = firstUniqueBossVictory || (!isAncientKnight && repeatableRewardsToday < dailyLimit)
        val experience = when {
            isAncientKnight && firstUniqueBossVictory -> 90
            isAncientKnight -> 0
            defeatedName.contains("Наёмник") -> 35
            defeatedName.contains("изменник") -> 50
            else -> 20
        }
        val gold = when {
            isAncientKnight && firstUniqueBossVictory -> 25
            isAncientKnight -> 0
            else -> 5
        }
        return RewardPlan(
            eligible = eligible,
            experience = if (eligible) experience else 0,
            gold = if (eligible) gold else 0,
            reputation = if (eligible) 1 else 0,
            countsTowardDailyLimit = eligible && !isAncientKnight,
            isFirstUniqueBossVictory = firstUniqueBossVictory
        )
    }
}
