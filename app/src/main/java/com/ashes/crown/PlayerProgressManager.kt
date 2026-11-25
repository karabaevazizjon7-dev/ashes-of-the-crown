package com.ashes.crown

/** Immutable player progression snapshot. */
internal data class PlayerProgressState(
    val level: Int,
    val experience: Int,
    val skillPoints: Int
)

internal data class ExperienceGrantResult(
    val state: PlayerProgressState,
    val levelsGained: Int,
    val message: String?
)

/** Pure progression rules shared by rewards and future character screens. */
internal object PlayerProgressManager {
    // Keep live progression inside the same limits enforced by SaveManager.
    const val MAX_LEVEL = 1_000
    const val MAX_EXPERIENCE = 1_000_000
    const val MAX_SKILL_POINTS = 100_000

    fun normalize(state: PlayerProgressState): PlayerProgressState = state.copy(
        level = state.level.coerceIn(1, MAX_LEVEL),
        experience = state.experience.coerceIn(0, MAX_EXPERIENCE),
        skillPoints = state.skillPoints.coerceIn(0, MAX_SKILL_POINTS)
    )

    fun grantExperience(state: PlayerProgressState, amount: Int): ExperienceGrantResult {
        val safe = normalize(state)
        if (amount <= 0) return ExperienceGrantResult(safe, 0, null)

        var level = safe.level
        var xp = (safe.experience.toLong() + amount.toLong()).coerceAtMost(MAX_EXPERIENCE.toLong())
        var points = safe.skillPoints
        var gained = 0
        while (level < MAX_LEVEL) {
            val threshold = level.toLong() * 100L
            if (xp < threshold) break
            xp -= threshold
            level++
            if (points < MAX_SKILL_POINTS) points++
            gained++
        }
        val next = PlayerProgressState(level, xp.toInt(), points)
        val message = if (gained > 0) {
            "Повышение уровня: теперь уровень $level. Получено очков навыков: $gained."
        } else null
        return ExperienceGrantResult(next, gained, message)
    }
}
